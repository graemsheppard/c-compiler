package com.graemsheppard.camlang.instructions;

import com.graemsheppard.camlang.enums.Register;
import com.graemsheppard.camlang.instructions.operands.ImmediateOperand;
import com.graemsheppard.camlang.instructions.operands.Operand;
import com.graemsheppard.camlang.instructions.operands.RegisterOperand;
import lombok.Getter;

public class MovInstruction extends Instruction {

    @Getter
    private final Operand operand1;

    @Getter
    private final Operand operand2;

    public MovInstruction(Operand op1, Operand op2) {
        operand1 = op1;
        operand2 = op2;
    }

    public MovInstruction(Register reg, String value) {
        operand1 = new RegisterOperand(reg);
        operand2 = new ImmediateOperand(value);
    }

    public MovInstruction(Register reg, Operand op2) {
        operand1 = new RegisterOperand(reg);
        operand2 = op2;
    }


    @Override
    public String toString() {
        return "\tmov \t" + operand1.toString() + ", \t" + operand2.toString() + "\n";
    }
}
