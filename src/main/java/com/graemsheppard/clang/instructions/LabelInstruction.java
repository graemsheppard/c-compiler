package com.graemsheppard.clang.instructions;

import lombok.Getter;

public class LabelInstruction extends Instruction {

    @Getter
    private final String name;

    public LabelInstruction(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name + ":\n";
    }
}
