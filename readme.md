# Clang (Aka C--)
Experimental implementation of a c-like language using 
Java to build the compiler.

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