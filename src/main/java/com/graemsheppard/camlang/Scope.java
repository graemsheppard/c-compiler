package com.graemsheppard.camlang;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class Scope {

    @Getter
    private final String name;

    @Getter
    private final int stackOffset;

    @Getter
    private final List<String> variables;

    public Scope (String scopeName, int sp) {
        name = scopeName;
        stackOffset = sp;
        variables = new ArrayList<>();
    }
}
