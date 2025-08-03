package com.graemsheppard.camlang.instructions.operands;

import com.graemsheppard.camlang.enums.Register;
import lombok.Getter;

public class RegisterOperand extends Operand {

    @Getter
    private Register register;

    public RegisterOperand(Register reg) {
        register = reg;
    }

    @Override
    public String toString() {
        return register.getName();
    }
}
