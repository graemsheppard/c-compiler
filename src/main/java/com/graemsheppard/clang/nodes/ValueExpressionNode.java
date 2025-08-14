package com.graemsheppard.clang.nodes;

import com.graemsheppard.clang.enums.DataType;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

public class ValueExpressionNode extends ExpressionNode {
    @Getter
    private String value;

    @Getter
    private DataType type;

    public ValueExpressionNode() {}

    public ValueExpressionNode(String val) {
        value = val;
    }

    public ValueExpressionNode(int val) {
        type = DataType.INTEGER;
        value = "0x" + Integer.toHexString(val);
    }

    public ValueExpressionNode(float val) {
        type = DataType.FLOAT;
        // TODO
        value = "0x" + Float.toHexString(val);
    }
}
