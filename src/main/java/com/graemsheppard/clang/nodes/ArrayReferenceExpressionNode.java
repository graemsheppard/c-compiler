package com.graemsheppard.clang.nodes;

import lombok.Getter;

public class ArrayReferenceExpressionNode extends ExpressionNode {

    @Getter
    private final String identifier;

    @Getter
    private final ExpressionNode expression;

    public ArrayReferenceExpressionNode(String id, ExpressionNode expr) {
        identifier = id;
        expression = expr;
    }
}
