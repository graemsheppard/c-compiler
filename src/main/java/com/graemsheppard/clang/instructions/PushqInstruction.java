package com.graemsheppard.clang.instructions;

import com.graemsheppard.clang.instructions.operands.ImmediateOperand;
import com.graemsheppard.clang.instructions.operands.Operand;
import lombok.Getter;

public class PushqInstruction extends Instruction {

    @Getter
    private final Operand operand;

    public PushqInstruction(Operand op) {
        operand = op;
    }

    public PushqInstruction(int value) {
        operand = new ImmediateOperand(value);
    }

    @Override
    public String toString() {
        return "\tpush qword \t" + operand.toString() + "\n";
    }
}
