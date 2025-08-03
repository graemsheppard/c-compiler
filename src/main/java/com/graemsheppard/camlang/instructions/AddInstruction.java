package com.graemsheppard.camlang.instructions;

import com.graemsheppard.camlang.enums.Register;
import com.graemsheppard.camlang.instructions.operands.ImmediateOperand;
import com.graemsheppard.camlang.instructions.operands.Operand;
import com.graemsheppard.camlang.instructions.operands.RegisterOperand;
import lombok.Getter;

public class AddInstruction extends Instruction {

    @Getter
    private final Operand operand1;

    @Getter
    private final Operand operand2;

    public AddInstruction(Operand op1, Operand op2) {
        operand1 = op1;
        operand2 = op2;
    }

    public AddInstruction(Register reg1, Register reg2) {
        operand1 = new RegisterOperand(reg1);
        operand2 = new RegisterOperand(reg2);
    }

    public AddInstruction(Register reg1, int value) {
        operand1 = new RegisterOperand(reg1);
        operand2 = new ImmediateOperand(String.valueOf(value));
    }

    @Override
    public String toString() {
        return "\tadd \t" + operand1.toString() + ", \t" + operand2.toString() + "\n";
    }
}
