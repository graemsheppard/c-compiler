package com.graemsheppard.clang.instructions;

import com.graemsheppard.clang.enums.Register;
import com.graemsheppard.clang.instructions.operands.Operand;
import com.graemsheppard.clang.instructions.operands.RegisterOperand;

public class SetneInstruction extends Instruction {

    private Operand operand;

    public SetneInstruction(Operand op) {
        operand = op;
    }

    public SetneInstruction(Register reg) {
        operand = new RegisterOperand(reg);
    }

    @Override
    public String toString() {
        return "\tsetne \t" + operand.toString() + "\n";
    }

}
