package com.graemsheppard.clang.instructions.operands;

import com.graemsheppard.clang.enums.Register;
import lombok.Getter;

public class MemoryOperand extends Operand {

    @Getter
    private final int offset;

    @Getter
    private final Operand inner;

    public MemoryOperand(Register reg) {
        this.inner = new RegisterOperand(reg);
        this.offset = 0;
    }
    public MemoryOperand(Operand inner, int offset) {
        this.inner = inner;
        this.offset = offset;
    }

    public MemoryOperand(Register reg, int offset) {
        this.inner = new RegisterOperand(reg);
        this.offset = offset;
    }

    @Override
    public String toString() {
        if (offset == 0)
            return "[" + inner.toString() + "]";
        char sign = offset >= 0 ? '+' : '-';
        return "[" + inner.toString() + sign + Math.abs(offset) + "]";
    }

}
