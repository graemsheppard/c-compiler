package com.graemsheppard.clang.enums;

import lombok.Getter;

import java.util.Arrays;

public enum DataType {
    INTEGER("int"),
    FLOAT("float"),
    VOID("void"),
    CHAR("char"),
    BOOL("bool"),
    INFERRED("inferred");

    @Getter
    private final String type;

    DataType(String type) {
        this.type = type;
    }

    public static DataType from(String value) {
        return Arrays.stream(DataType.values()).filter(d -> d.getType().equalsIgnoreCase(value)).findAny().get();
    }
}
