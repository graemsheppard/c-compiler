package com.graemsheppard.camlang.instructions;

import com.graemsheppard.camlang.enums.Register;
import com.graemsheppard.camlang.instructions.operands.Operand;
import com.graemsheppard.camlang.instructions.operands.RegisterOperand;

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
