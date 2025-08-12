package com.graemsheppard.clang.instructions.operands;

import lombok.Getter;

public class ImmediateOperand extends Operand {

    @Getter
    private String value;

    public ImmediateOperand(String value) {
        this.value = value;
    }

    public ImmediateOperand(int value) {
        this.value = String.valueOf(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
