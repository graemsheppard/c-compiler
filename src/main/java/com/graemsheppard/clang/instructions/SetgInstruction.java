package com.graemsheppard.clang.instructions;

import com.graemsheppard.clang.enums.Register;
import com.graemsheppard.clang.instructions.operands.Operand;
import com.graemsheppard.clang.instructions.operands.RegisterOperand;

public class SetgInstruction extends Instruction {

    private Operand operand;

    public SetgInstruction(Operand op) {
        operand = op;
    }

    public SetgInstruction(Register reg) {
        operand = new RegisterOperand(reg);
    }

    @Override
    public String toString() {
        return "\tsetg \t" + operand.toString() + "\n";
    }

}
