package com.graemsheppard.clang.nodes;

import lombok.Getter;

public class IdentifierExpressionNode extends ExpressionNode {

    @Getter
    private final String identifier;

    public IdentifierExpressionNode(String id) {
        identifier = id;
    }
}
