package com.graemsheppard.clang.instructions;

import com.graemsheppard.clang.enums.Register;
import com.graemsheppard.clang.instructions.operands.Operand;
import com.graemsheppard.clang.instructions.operands.RegisterOperand;
import lombok.Getter;

public class IMulInstruction extends Instruction {

    @Getter
    private final Operand operand1;

    @Getter
    private final Operand operand2;

    public IMulInstruction(Operand op1, Operand op2) {
        operand1 = op1;
        operand2 = op2;
    }

    public IMulInstruction(Register reg1, Register reg2) {
        operand1 = new RegisterOperand(reg1);
        operand2 = new RegisterOperand(reg2);
    }

    @Override
    public String toString() {
        return "\timul \t" + operand1.toString() + ", \t" + operand2.toString() + "\n";
    }
}
