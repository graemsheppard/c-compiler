package com.graemsheppard.camlang.instructions;

import com.graemsheppard.camlang.enums.Register;
import com.graemsheppard.camlang.instructions.operands.Operand;
import com.graemsheppard.camlang.instructions.operands.RegisterOperand;

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
        return "\tsetne \t" + operand.toString();
    }

}
