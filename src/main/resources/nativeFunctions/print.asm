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
