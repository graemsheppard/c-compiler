    default rel
section .data
	str_0: 	db "Program exited with code", 	10
section .text
    global _main
_main:
	mov 	rax, 	0
	push 	rax
if_0:
	mov 	rax, 	[rsp+0]
	push 	rax
	mov 	rax, 	10
	push 	rax
	pop 	rbx
	pop 	rax
	cmp 	rax, 	rbx
	setl 	al
	movzx 	rax, 	al
	test 	rax, 	rax
	jz   	endif_0
	mov 	rax, 	1
	push 	rax
if_1:
	mov 	rax, 	[rsp+0]
	push 	rax
	mov 	rax, 	1
	push 	rax
	pop 	rbx
	pop 	rax
	cmp 	rax, 	rbx
	setne 	al
	movzx 	rax, 	al
	test 	rax, 	rax
	jz   	if_1_elif_1
	mov 	rax, 	1
	push 	rax
	pop 	r10
	call 	exit
	jmp 	endif_1
if_1_elif_1:
	mov 	rax, 	[rsp+8]
	push 	rax
	mov 	rax, 	0
	push 	rax
	pop 	rbx
	pop 	rax
	cmp 	rax, 	rbx
	sete 	al
	movzx 	rax, 	al
	test 	rax, 	rax
	jz   	if_1_else
	mov 	rax, 	2
	push 	rax
	pop 	r10
	call 	exit
	jmp 	endif_1
if_1_else:
	mov 	rax, 	3
	push 	rax
	pop 	r10
	call 	exit
endif_1:
	jmp 	endif_0
	add 	rsp, 	8
endif_0:
	mov 	rax, 	4
	push 	rax
	pop 	r10
	call 	exit
    jmp     exit
exit:
    mov     rax,    0x02000004
    mov     rdi,    1
    lea     rsi,    [str_0]
	mov 	rdx, 	25
    syscall
    mov     rax,    0x02000001
    mov     rdi,    r10
    syscall
