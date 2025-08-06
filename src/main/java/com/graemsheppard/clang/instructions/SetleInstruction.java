package com.graemsheppard.clang.instructions;

import com.graemsheppard.clang.enums.Register;
import com.graemsheppard.clang.instructions.operands.Operand;
import com.graemsheppard.clang.instructions.operands.RegisterOperand;

public class SetleInstruction extends Instruction {

    private Operand operand;

    public SetleInstruction(Operand op) {
        operand = op;
    }

    public SetleInstruction(Register reg) {
        operand = new RegisterOperand(reg);
    }

    @Override
    public String toString() {
        return "\tsetle \t" + operand.toString() + "\n";
    }

}
