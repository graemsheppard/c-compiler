package com.graemsheppard.clang.enums;

import lombok.Getter;

public enum Register {
    RAX("rax"),
    RBX("rbx"),
    RDI("rdi"),
    RSI("rsi"),
    R8("r8"),
    R9("r9"),
    R10("r10"),
    R11("r11"),
    R12("r12"),
    R13("r13"),
    R14("r14"),
    R15("r15"),
    RSP("rsp"),
    RBP("rbp"),
    AL("al");

    @Getter
    private String name;

    Register(String name) {
        this.name = name;
    }

}
