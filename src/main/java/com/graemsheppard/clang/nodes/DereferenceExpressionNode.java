package com.graemsheppard.clang.nodes;

import lombok.Getter;

public class DereferenceExpressionNode extends ExpressionNode {

    @Getter
    private final ExpressionNode expression;

    public DereferenceExpressionNode(ExpressionNode expr) {
        expression = expr;
    }
}
