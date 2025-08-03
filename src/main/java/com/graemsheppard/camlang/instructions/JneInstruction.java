package com.graemsheppard.camlang.instructions;

import com.graemsheppard.camlang.instructions.operands.LabelOperand;
import com.graemsheppard.camlang.instructions.operands.Operand;
import lombok.Getter;

public class JneInstruction extends Instruction {

    @Getter
    private final Operand label;

    public JneInstruction(String label) {
        this.label = new LabelOperand(label);
    }

    @Override
    public String toString() {
        return "\tjne \t" + label + "\n";
    }
}
