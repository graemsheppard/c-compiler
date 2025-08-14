itoa:   ; itoa(int val, int* len)
    push    rbp
    mov     rbp,    rsp
	mov		r8,		[rbp+24]
	sub		rsp,	20				; make room for buffer
	mov 	rcx,	1 				; divisor
	mov		r11,	0				; string length
	test	r8,		r8
	jns		itoa_loop				; handle negative
	mov		al,		'-'
	add		r11,	1
	mov		byte	[rsp+1], al
	neg		r8
itoa_loop:
	mov 	rax,	r8
	xor 	rdx,	rdx
	idiv	rcx
	cmp		rax,	10 				; loop condition
	jl		end_itoa_loop 			; condition negated
	imul	rcx, 	10
	jmp		itoa_loop
end_itoa_loop:
	mov 	r9, 	rcx
	xor		rdx, 	rdx
itoa_h:
	test	r9,	r9
	jz		itoa_h_return
	mov		rcx, 	r9
	mov		rax,	r8
	xor		rdx,	rdx
	idiv	rcx
	mov		r8,		rdx
	mov		r10,	rax
	add		al, 	'0'				; convert to char
	mov		rdi,	rsp
	add		r11,	1				; increment char count
	add		rdi,	r11				; pointer to char location
	mov 	byte	[rdi],	al
	mov		rax, 	r9
	mov		rcx,	10
	xor		rdx,	rdx
	idiv	rcx
	mov		r9,		rax
	jmp		itoa_h
itoa_h_return:
	push 	r11
	mov 	rax,	0x020000C5 		; mmap syscall
	xor		rdi,	rdi
	mov		rsi,	r11
	mov		rdx,	3				; read/write
	mov		r10, 	0x1002			; private / anon
	mov 	r8,		-1				; fd
	xor		r9,		r9				; offset
	syscall
	pop 	r11
	cld
	mov 	rcx,	r11				; number of repetitions
	mov		rdi,	rax				; destination address to copy bytes to
	mov 	rsi,	rsp				; source address
	add		rsi,	1
	rep		movsb
	mov     rsi,    [rbp+16]
	mov		[rsi],  r11           ; set the 2nd param to length
	mov     rsp,    rbp
	pop		rbp
	ret
