package com.graemsheppard.clang.nodes;

import lombok.Getter;

public class DeclarationStatementNode extends StatementNode {

    @Getter
    private final String identifier;

    @Getter
    private final ExpressionNode expression;

    public DeclarationStatementNode(String id, ExpressionNode expr) {
        identifier = id;
        expression = expr;
    }

    public DeclarationStatementNode(String id) {
        identifier = id;
        expression = new ValueExpressionNode(0);
    }
}
