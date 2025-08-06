package com.graemsheppard.clang.instructions;

import com.graemsheppard.clang.instructions.operands.Operand;
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
