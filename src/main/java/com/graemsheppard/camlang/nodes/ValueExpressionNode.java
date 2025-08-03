package com.graemsheppard.camlang.nodes;

import lombok.Getter;
import lombok.Setter;

public class ValueExpressionNode extends ExpressionNode {
    @Getter
    @Setter
    private String value;

    public ValueExpressionNode() {}

    public ValueExpressionNode(String val) {
        value = val;
    }
}
