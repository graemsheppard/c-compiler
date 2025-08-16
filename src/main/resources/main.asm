    default rel
section .data
	str_0: 	db 'Program exited with code: ', 	10
section .text
    global _main
_main:
    mov     rbp,    rsp
	mov 	rax, 	0x0
	push 	rax
	mov 	rax, 	0x0
	push 	rax
	mov 	rax, 	0xa
	push 	rax
if_0:
	mov 	rax, 	[rbp-8]
	push 	rax
	mov 	rax, 	0x0
	push 	rax
	pop 	rbx
	pop 	rax
	cmp 	rax, 	rbx
	setne 	al
	movzx 	rax, 	al
	test 	rax, 	rax
	jz   	if_0_else
	sub 	rsp, 	8
	mov 	[rsp], 	rsp
	mov 	rax, 	0x1
	push 	rax
	mov 	rsp, 	[rbp-32]
	pop 	rsp
	add 	rsp, 	8
	jmp 	endif_0
if_0_else:
	sub 	rsp, 	8
	mov 	[rsp], 	rsp
	sub 	rsp, 	8
	mov 	[rsp], 	rsp
while_0:
	mov 	rax, 	[rbp-16]
	push 	rax
	mov 	rax, 	0x64
	push 	rax
	pop 	rbx
	pop 	rax
	cmp 	rax, 	rbx
	setl 	al
	movzx 	rax, 	al
	test 	rax, 	rax
	jz   	end_while_0
	mov 	rax, 	0x0
	push 	rax
	sub 	rsp, 	8
	mov 	[rsp], 	rsp
while_1:
	mov 	rax, 	[rbp-48]
	push 	rax
	mov 	rax, 	0xa
	push 	rax
	pop 	rbx
	pop 	rax
	cmp 	rax, 	rbx
	setl 	al
	movzx 	rax, 	al
	test 	rax, 	rax
	jz   	end_while_1
	mov 	rax, 	[rbp-16]
	push 	rax
	mov 	rax, 	[rbp-48]
	push 	rax
	pop 	rbx
	pop 	rax
	add 	rax, 	rbx
	push 	rax
	lea 	rax, 	[rbp-8]
	push 	rax
	call 	itoa
	add 	rsp, 	16
	push 	rax
	mov 	rax, 	[rbp-64]
	push 	rax
	mov 	rax, 	[rbp-8]
	push 	rax
	call 	print
	add 	rsp, 	16
	lea 	rax, 	[rbp-24]
	push 	rax
	mov 	rax, 	0x1
	push 	rax
	call 	print
	add 	rsp, 	16
	mov 	rax, 	[rbp-48]
	push 	rax
	mov 	rax, 	0x1
	push 	rax
	pop 	rbx
	pop 	rax
	add 	rax, 	rbx
	mov 	[rbp-48], 	rax
	mov 	rsp, 	[rbp-56]
	jmp 	while_1
	pop 	rsp
end_while_1:
	add 	rsp, 	8
	mov 	rax, 	[rbp-16]
	push 	rax
	mov 	rax, 	0xa
	push 	rax
	pop 	rbx
	pop 	rax
	add 	rax, 	rbx
	mov 	[rbp-16], 	rax
	mov 	rsp, 	[rbp-40]
	jmp 	while_0
	pop 	rsp
end_while_0:
	add 	rsp, 	8
	mov 	rsp, 	[rbp-32]
	pop 	rsp
	add 	rsp, 	8
endif_0:
	push 	0
	sub 	rsp, 	8
exit:   ; exit(int code)
    push    rbp
    mov     rbp,    rsp
    push    0                       ; allocate return address for string length
    lea     rax,    [str_0]
    push    rax
    push    26
    call    print
    add     rsp,    16
	mov		rax,	[rbp+16]		; retrieve parameter value
	push    rax
	lea     rax,    [rbp-8]
	push    rax
	call	itoa					; get string pointer in rax and size in rdx
	add     rsp,    16
	push    rax
	mov     rax,    [rbp-8]
	push    rax
	call    print
	add     rsp,    16
    mov     rax,    0x02000001
    mov     rdi,    [rbp+16]
    syscall
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
print: ; print(void* str, int length)
    push    rbp
    mov     rbp,    rsp
    mov     rax,    0x02000004
    mov     rdi,    1
    mov     rdx,    [rbp+16]
    mov     rsi,    [rbp+24]
    syscall
    mov     rsp,    rbp
    pop     rbp
    ret
