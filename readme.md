# Clang (Aka C--)
Experimental implementation of a c-like language using 
Java to build the compiler. Currently only compiles
for MacOS running Intel x86 architecture.

### Features

- Variables
- Functions
- If/Else Statements
- While loops
- Pointers


This uses recursive-descent parsing and performs slight optimization
on redundant instructions, for example:
```
    mov     rax     10
    push    rax
    pop     rax
```
would get simplified to:
```
    mov     rax     10
```

## Example
Usage example:
    `clang main.c`
outputs `main.asm`, the assembly to run on MacOS using Intel x86 architecture.

`main.c`:
```
int testVar = 30;
int test(int i) {
    exit(testVar + i);
}
int k = test(1);
```

`main.asm`:
```
    default rel
section .data
    str_0:  db "Program exited with code", 	10
section .text
    global _main
_main:
    mov     rbp,    rsp
    mov     rax,    30
    push    rax
    jmp     end_test
test:
    mov     rax,    0
    push    rax
    mov     rax,    [rbp+8]
    push    rax
    mov     rax,    [rbp-8]
    push    rax
    pop     rbx
    pop     rax
    add     rax,    rbx
    push    rax
    pop     r10
    call    exit
    mov     rsp,    rbp
    pop     rbp
    ret
end_test:
    push    rbp
    mov     rbp,    rsp
    mov     rax,    1
    push    rax
    call    test
    mov     r10,    0
    call     exit
exit:
    mov     rax,    0x02000004
    mov     rdi,    1
    lea     rsi,    [str_0]
    mov     rdx,    25
    syscall
    mov     rax,    0x02000001
    mov     rdi,    r10
    syscall

```