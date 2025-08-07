    default rel
section .data
	str_0: 	db "Program exited with code", 	10
section .text
    global _main
_main:
    mov     rbp,    rsp
	mov 	rax, 	30
	push 	rax
	jmp 	end_func1
func1:
	push 	rbp
	mov 	rbp, 	rsp
	mov 	rax, 	3
	push 	rax
	jmp 	end_func2
func2:
	push 	rbp
	mov 	rbp, 	rsp
	mov 	rax, 	[rbp+24]
	push 	rax
	mov 	rax, 	[rbp+16]
	push 	rax
	pop 	rbx
	pop 	rax
	add 	rax, 	rbx
	push 	rax
	mov 	rax, 	[rbp+32]
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
end_func2:
	mov 	rax, 	[rbp+24]
	push 	rax
	mov 	rax, 	[rbp+16]
	push 	rax
	call 	func2
	pop 	rax
	pop 	rax
	mov 	rsp, 	rbp
	pop 	rbp
	ret
end_func1:
	mov 	rax, 	12
	push 	rax
	mov 	rax, 	22
	push 	rax
	call 	func1
	pop 	rax
	pop 	rax
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
