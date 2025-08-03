package com.graemsheppard.camlang.instructions.operands;

import lombok.Getter;

public class ImmediateOperand extends Operand {

    @Getter
    private String value;

    public ImmediateOperand(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
