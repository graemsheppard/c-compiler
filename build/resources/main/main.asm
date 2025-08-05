    default rel
section .data
	str_0: 	db "Program exited with code", 	10
section .text
    global _main
_main:
    mov     rbp,    rsp
	mov 	rax, 	1
	push 	rax
	jmp 	end_test
test:
	push 	rbp
	mov 	rbp, 	rsp
	mov 	rax, 	[rbp+8]
	push 	rax
	mov 	rax, 	[rbp+0]
	push 	rax
	pop 	rbx
	pop 	rax
	add 	rax, 	rbx
	push 	rax
	pop 	r10
	call 	exit
	mov 	rsp, 	rbp
	pop 	rbp
	ret
end_test:
	mov 	rax, 	1
	push 	rax
	call 	test
    mov      r10,     0
    call     exit
exit:
    mov     rax,    0x02000004
    mov     rdi,    1
    lea     rsi,    [str_0]
	mov 	rdx, 	25
    syscall
    mov     rax,    0x02000001
    mov     rdi,    r10
    syscall
