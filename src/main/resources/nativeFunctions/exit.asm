exit:
    push    rbp
    mov     rbp,    rsp
    lea     rax,    [str_0]
    push    rax
    push    26
    call    print
    add     rsp,    16
	mov		rax,	[rbp+16]		; retrieve parameter value
	push    rax
	call	itoa					; get string pointer in rax and size in rdx
	add     rsp,    8
	push    rax
	push    rdx
	call    print
	add     rsp,    16
    mov     rax,    0x02000001
    mov     rdi,    [rbp+16]
    syscall
