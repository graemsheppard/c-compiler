package com.graemsheppard.camlang.instructions;

import com.graemsheppard.camlang.enums.Register;
import com.graemsheppard.camlang.instructions.operands.ImmediateOperand;
import com.graemsheppard.camlang.instructions.operands.Operand;
import com.graemsheppard.camlang.instructions.operands.RegisterOperand;
import lombok.Getter;

public class MovzxInstruction extends Instruction {

    @Getter
    private final Operand operand1;

    @Getter
    private final Operand operand2;

    public MovzxInstruction(Operand op1, Operand op2) {
        operand1 = op1;
        operand2 = op2;
    }

    public MovzxInstruction(Register reg, String value) {
        operand1 = new RegisterOperand(reg);
        operand2 = new ImmediateOperand(value);
    }

    public MovzxInstruction(Register reg, Operand op2) {
        operand1 = new RegisterOperand(reg);
        operand2 = op2;
    }

    public MovzxInstruction(Register reg1, Register reg2) {
        operand1 = new RegisterOperand(reg1);
        operand2 = new RegisterOperand(reg2);
    }


    @Override
    public String toString() {
        return "\tmovzx \t" + operand1.toString() + ", \t" + operand2.toString() + "\n";
    }
}
