package com.graemsheppard.camlang.instructions;

import com.graemsheppard.camlang.enums.Register;
import com.graemsheppard.camlang.instructions.operands.Operand;
import lombok.Getter;

public class PopInstruction extends Instruction {

    @Getter
    private final Operand operand;

    public PopInstruction(Operand op) {
        operand = op;
    }

    @Override
    public String toString() {
        return "\tpop \t" + operand.toString() + "\n";
    }
}
