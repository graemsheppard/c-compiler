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
