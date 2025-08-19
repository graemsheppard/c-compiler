package com.graemsheppard.clang.nodes;

import lombok.Getter;

public class AssignmentStatementNode extends StatementNode {

    @Getter
    private final String identifier;

    @Getter
    private final ExpressionNode expression;

    @Getter
    private final ExpressionNode offset;

    public AssignmentStatementNode(String id, ExpressionNode expr) {
        identifier = id;
        expression = expr;
        offset = null;
    }

    public AssignmentStatementNode(String id, ExpressionNode expr, ExpressionNode offs) {
        identifier = id;
        expression = expr;
        offset = offs;
    }
}
