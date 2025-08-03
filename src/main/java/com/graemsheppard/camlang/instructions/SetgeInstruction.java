package com.graemsheppard.camlang.instructions;

import com.graemsheppard.camlang.enums.Register;
import com.graemsheppard.camlang.instructions.operands.Operand;
import com.graemsheppard.camlang.instructions.operands.RegisterOperand;

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
