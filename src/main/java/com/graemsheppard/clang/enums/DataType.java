package com.graemsheppard.clang.enums;

import lombok.Getter;

public enum DataType {
    INTEGER("int"),
    FLOAT("float"),
    VOID("void");

    @Getter
    private final String type;

    DataType(String type) {
        this.type = type;
    }
}
