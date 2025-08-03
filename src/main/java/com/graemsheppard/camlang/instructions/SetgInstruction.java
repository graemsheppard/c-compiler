package com.graemsheppard.camlang.instructions;

import com.graemsheppard.camlang.enums.Register;
import com.graemsheppard.camlang.instructions.operands.Operand;
import com.graemsheppard.camlang.instructions.operands.RegisterOperand;

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
