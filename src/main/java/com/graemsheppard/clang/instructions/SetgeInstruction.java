package com.graemsheppard.clang.instructions;

import com.graemsheppard.clang.enums.Register;
import com.graemsheppard.clang.instructions.operands.Operand;
import com.graemsheppard.clang.instructions.operands.RegisterOperand;

public class SetgeInstruction extends Instruction {

    private Operand operand;

    public SetgeInstruction(Operand op) {
        operand = op;
    }

    public SetgeInstruction(Register reg) {
        operand = new RegisterOperand(reg);
    }

    @Override
    public String toString() {
        return "\tsetge \t" + operand.toString() + "\n";
    }

}
