    default rel
section .data
	str_0: 	db "Program exited with code", 	10
section .text
    global _main
_main:
br_0:
    mov     rax,    10
    test    rax,    rax
    jz      br_1
    mov     r10,    0
    jmp     br_3
br_1:
    mov     rax,    10
    test    rax,    rax
    jz     br_2
    mov     r10,    1
    jmp     br_3
br_2:
    mov     r10,    2
br_3:
    call    exit
exit:
    mov     rax,    0x02000004
    mov     rdi,    1
    lea     rsi,    [str_0]
	mov 	rdx, 	25
    syscall
    mov     rax,    0x02000001
    mov     rdi,    r10
    syscall
