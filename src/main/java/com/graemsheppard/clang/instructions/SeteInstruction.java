package com.graemsheppard.clang.instructions;

import com.graemsheppard.clang.enums.Register;
import com.graemsheppard.clang.instructions.operands.Operand;
import com.graemsheppard.clang.instructions.operands.RegisterOperand;

public class SeteInstruction extends Instruction {

    private Operand operand;

    public SeteInstruction(Operand op) {
        operand = op;
    }

    public SeteInstruction(Register reg) {
        operand = new RegisterOperand(reg);
    }

    @Override
    public String toString() {
        return "\tsete \t" + operand.toString() + "\n";
    }

}
