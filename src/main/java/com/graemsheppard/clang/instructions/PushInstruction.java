package com.graemsheppard.clang.instructions;

import com.graemsheppard.clang.instructions.operands.ImmediateOperand;
import com.graemsheppard.clang.instructions.operands.Operand;
import lombok.Getter;

public class PushInstruction extends Instruction {

    @Getter
    private final Operand operand;

    public PushInstruction(Operand op) {
        operand = op;
    }

    public PushInstruction(int value) {
        operand = new ImmediateOperand(value);
    }

    @Override
    public String toString() {
        return "\tpush \t" + operand.toString() + "\n";
    }
}
