package com.graemsheppard.clang.instructions;

import com.graemsheppard.clang.enums.Register;
import com.graemsheppard.clang.instructions.operands.Operand;
import com.graemsheppard.clang.instructions.operands.RegisterOperand;

public class SetlInstruction extends Instruction {

    private Operand operand;

    public SetlInstruction(Operand op) {
        operand = op;
    }

    public SetlInstruction(Register reg) {
        operand = new RegisterOperand(reg);
    }

    @Override
    public String toString() {
        return "\tsetl \t" + operand.toString() + "\n";
    }

}
