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
    private final int framePointer;

    @Getter
    private final int numParams;

    @Getter
    private final boolean isFunction;

    public Scope (String scopeName, int rbp) {
        name = scopeName;
        variables = new ArrayList<>();
        framePointer = rbp;
        numParams = 0;
        isFunction = false;
    }

    public Scope (String scopeName, int rbp, int params) {
        name = scopeName;
        variables = new ArrayList<>();
        framePointer = rbp;
        numParams = params;
        isFunction = true;
    }

}
