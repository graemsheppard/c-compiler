package com.graemsheppard.clang.nodes;

import lombok.Getter;

public class AddressExpressionNode extends ExpressionNode {

    @Getter
    private IdentifierExpressionNode identifier;

    public AddressExpressionNode(IdentifierExpressionNode id) {
        identifier = id;
    }
}
