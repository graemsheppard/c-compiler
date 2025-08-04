package com.graemsheppard.camlang.instructions;

import com.graemsheppard.camlang.enums.Register;
import com.graemsheppard.camlang.instructions.operands.RegisterOperand;
import lombok.Getter;

public class TestInstruction extends Instruction {

    @Getter
    private final RegisterOperand register1;

    private final RegisterOperand register2;

    public TestInstruction(Register reg1, Register reg2) {
        register1 = new RegisterOperand(reg1);
        register2 = new RegisterOperand(reg2);
    }

    @Override
    public String toString() {
        return "\ttest \t" + register1 + ", \t" + register2 + "\n";
    }
}
