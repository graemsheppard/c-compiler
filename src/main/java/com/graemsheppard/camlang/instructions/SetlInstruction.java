package com.graemsheppard.camlang.instructions;

import com.graemsheppard.camlang.enums.Register;
import com.graemsheppard.camlang.instructions.operands.Operand;
import com.graemsheppard.camlang.instructions.operands.RegisterOperand;

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
