package com.graemsheppard.clang.enums;

import lombok.Getter;

public enum Register {
    RAX("rax"),
    RBX("rbx"),
    RDI("rdi"),
    RSI("rsi"),
    R10("r10"),
    RSP("rsp"),
    RBP("rbp"),
    AL("al");

    @Getter
    private String name;

    Register(String name) {
        this.name = name;
    }

}
