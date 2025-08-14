package com.graemsheppard.clang.nodes;

import lombok.Getter;

public class DeclarationStatementNode extends StatementNode {

    @Getter
    private final String identifier;

    @Getter
    private final boolean isPointer;

    @Getter
    private final ExpressionNode expression;

    public DeclarationStatementNode(String id, ExpressionNode expr) {
        isPointer = false;
        identifier = id;
        expression = expr;
    }

    public DeclarationStatementNode(String id, ExpressionNode expr, boolean pointer) {
        isPointer = pointer;
        identifier = id;
        expression = expr;
    }

    public DeclarationStatementNode(String id) {
        isPointer = false;
        identifier = id;
        expression = new ValueExpressionNode(0);
    }

    public DeclarationStatementNode(String id, boolean pointer) {
        isPointer = pointer;
        identifier = id;
        expression = new ValueExpressionNode(0);
    }


}
