package com.graemsheppard.camlang;

import lombok.Getter;

public class VariableLocation {

    @Getter
    private final String identifier;

    @Getter
    private final Scope scope;

    @Getter
    private final int offset;

    public VariableLocation(String identifier, int offset, Scope scope) {
        this.identifier = identifier;
        this.offset = offset;
        this.scope = scope;
    }

    public int getLocationRelativeTo(int framePointer) {
        return scope.getFramePointer() - framePointer + offset;
    }
}
