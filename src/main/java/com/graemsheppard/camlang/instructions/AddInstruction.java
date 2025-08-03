package com.graemsheppard.camlang.instructions;

import com.graemsheppard.camlang.instructions.operands.Operand;
import lombok.Getter;

public class AddInstruction extends Instruction {

    @Getter
    private final Operand operand1;

    @Getter
    private final Operand operand2;

    public AddInstruction(Operand op1, Operand op2) {
        operand1 = op1;
        operand2 = op2;
    }

    @Override
    public String toString() {
        return "\tadd \t" + operand1.toString() + ", \t" + operand2.toString() + "\n";
    }
}
