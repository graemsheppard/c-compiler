package com.graemsheppard.camlang.instructions;

import com.graemsheppard.camlang.enums.Register;
import com.graemsheppard.camlang.instructions.operands.Operand;
import com.graemsheppard.camlang.instructions.operands.RegisterOperand;

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
