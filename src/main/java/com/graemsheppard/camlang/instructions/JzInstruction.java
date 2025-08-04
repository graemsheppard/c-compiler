package com.graemsheppard.camlang.instructions;

import com.graemsheppard.camlang.instructions.operands.LabelOperand;
import com.graemsheppard.camlang.instructions.operands.Operand;
import lombok.Getter;

public class JzInstruction extends Instruction {

    @Getter
    private final Operand label;

    public JzInstruction(String label) {
        this.label = new LabelOperand(label);
    }

    @Override
    public String toString() {
        return "\tjz   \t" + label + "\n";
    }
}
