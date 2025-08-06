package com.graemsheppard.clang.instructions;

import com.graemsheppard.clang.instructions.operands.LabelOperand;
import com.graemsheppard.clang.instructions.operands.Operand;
import lombok.Getter;

public class JmpInstruction extends Instruction {

    @Getter
    private final Operand label;

    public JmpInstruction(String label) {
        this.label = new LabelOperand(label);
    }

    @Override
    public String toString() {
        return "\tjmp \t" + label + "\n";
    }
}
