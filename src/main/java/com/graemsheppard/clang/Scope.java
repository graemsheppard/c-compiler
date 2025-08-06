package com.graemsheppard.clang;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class Scope {

    @Getter
    private final String name;

    @Getter
    private final List<String> variables;

    @Getter
    private int framePointer;

    public Scope (String scopeName, int rbp) {
        name = scopeName;
        variables = new ArrayList<>();
        framePointer = rbp;
    }
}
