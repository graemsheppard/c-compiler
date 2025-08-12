package com.graemsheppard.clang.instructions;

import com.graemsheppard.clang.enums.Register;
import com.graemsheppard.clang.instructions.operands.ImmediateOperand;
import com.graemsheppard.clang.instructions.operands.Operand;
import com.graemsheppard.clang.instructions.operands.RegisterOperand;
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

    public MovInstruction(Register reg, int value) {
        operand1 = new RegisterOperand(reg);
        operand2 = new ImmediateOperand(value);
    }

    public MovInstruction(Register dest, Register src) {
        operand1 = new RegisterOperand(dest);
        operand2 = new RegisterOperand(src);
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
