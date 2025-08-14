package com.graemsheppard.clang.instructions;

import com.graemsheppard.clang.enums.Register;
import com.graemsheppard.clang.instructions.operands.ImmediateOperand;
import com.graemsheppard.clang.instructions.operands.Operand;
import com.graemsheppard.clang.instructions.operands.RegisterOperand;
import lombok.Getter;

public class LeaInstruction extends Instruction {

    @Getter
    private final Operand operand1;

    @Getter
    private final Operand operand2;

    public LeaInstruction(Operand op1, Operand op2) {
        operand1 = op1;
        operand2 = op2;
    }

    public LeaInstruction(Register reg, Operand op2) {
        operand1 = new RegisterOperand(reg);
        operand2 = op2;
    }


    @Override
    public String toString() {
        return "\tlea \t" + operand1.toString() + ", \t" + operand2.toString() + "\n";
    }
}
