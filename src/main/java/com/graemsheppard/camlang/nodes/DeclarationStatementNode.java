package com.graemsheppard.camlang.nodes;

import lombok.Getter;

public class DeclarationStatementNode extends StatementNode {

    @Getter
    private String identifier;

    @Getter
    private ExpressionNode expression;

    public DeclarationStatementNode(String id, ExpressionNode expr) {
        identifier = id;
        expression = expr;
    }
}
