package com.graemsheppard.camlang.instructions;

import com.graemsheppard.camlang.enums.Register;
import com.graemsheppard.camlang.instructions.operands.Operand;
import lombok.Getter;

public class PushInstruction extends Instruction {

    @Getter
    private final Operand operand;

    public PushInstruction(Operand op) {
        operand = op;
    }

    @Override
    public String toString() {
        return "\tpush \t" + operand.toString() + "\n";
    }
}
