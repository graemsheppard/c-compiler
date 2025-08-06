package com.graemsheppard.clang.instructions.operands;

import lombok.Getter;

public class LabelOperand extends Operand {

    @Getter
    private String label;

    public LabelOperand(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label.toString();
    }
}
