    default rel
section .data
	str_0: 	db "Program exited with code", 	10
section .text
    global _main
_main:
	mov 	rax, 	1 
	push 	rax
	mov 	rax, 	[rsp+0]
	push 	rax
	mov 	rax, 	1 
	push 	rax
	pop 	rbx
	pop 	rax
	cmp 	rax, 	rbx 
	sete 	al
	movzx 	rax, 	al
	push 	rax
	pop 	rax
	cmp 	rax, 	1
	jne 	br_0
	mov 	rax, 	2 
	push 	rax
	mov 	rax, 	[rsp+8]
	push 	rax
	pop 	rbx
	pop 	rax
	add 	rax, 	rbx 
	push 	rax
	mov 	rax, 	[rsp+0]
	push 	rax
	mov 	rax, 	[rsp+8]
	push 	rax
	mov 	rax, 	3 
	push 	rax
	pop 	rbx
	pop 	rax
	cmp 	rax, 	rbx 
	sete 	al
	movzx 	rax, 	al
	push 	rax
	pop 	rax
	cmp 	rax, 	1
	jne 	br_1
	mov 	rax, 	3 
	push 	rax
	mov 	rax, 	[rsp+16]
	push 	rax
	pop 	rbx
	pop 	rax
	add 	rax, 	rbx 
	push 	rax
	mov 	rax, 	[rsp+0]
	push 	rax
br_1:
	add 	rsp, 	16
	mov 	rax, 	[rsp+8]
	push 	rax
	mov 	rax, 	4 
	push 	rax
	pop 	rbx
	pop 	rax
	add 	rax, 	rbx 
	push 	rax
br_0:
	add 	rsp, 	24
	mov 	rax, 	[rsp+0]
	push 	rax
	mov 	rax, 	[rsp+0]
	push 	rax
	mov 	rax, 	1 
	push 	rax
	pop 	rbx
	pop 	rax
	add 	rax, 	rbx 
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
