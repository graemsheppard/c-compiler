    default rel
section .data
	str_0: 	db "Program exited with code", 	10
section .text
    global _main
_main:
    mov     rbp,     rsp     ; say 128
    mov     rax,     10
    push    rax
    mov     rax,     5
    push    rax
    pop     rbx
    pop     rax
    add     rax,    rbx
    push    rax             ; store variable 'x' at rbp-stackSize [rbp-8]
    push    0
    push    rbp             ; store the current frame pointer say 104
    mov     rbp,    rsp     ; update the frame pointer to current stack pointer
    call    func
    mov     rsp,    rbp     ; return stack pointer to the start of the frame
    pop     rbp             ; pop the old frame pointer
    call    exit
func:
    mov     r10,    [rbp+16]       ; prepare to exit 9
    ret
exit:
    mov     rax,    0x02000004
    mov     rdi,    1
    lea     rsi,    [str_0]
	mov 	rdx, 	25
    syscall
    mov     rax,    0x02000001
    mov     rdi,    r10
    syscall
