package com.graemsheppard.clang.instructions;

import com.graemsheppard.clang.instructions.operands.LabelOperand;
import com.graemsheppard.clang.instructions.operands.Operand;
import lombok.Getter;

public class CallInstruction extends Instruction {

    @Getter
    private final Operand label;

    public CallInstruction(String label) {
        this.label = new LabelOperand(label);
    }

    @Override
    public String toString() {
        return "\tcall \t" + label + "\n";
    }
}
