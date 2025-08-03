package com.graemsheppard.camlang.nodes;

import lombok.Getter;

public class AssignmentStatementNode extends StatementNode {

    @Getter
    private final String identifier;

    @Getter
    private final ExpressionNode expression;

    public AssignmentStatementNode(String id, ExpressionNode expr) {
        identifier = id;
        expression = expr;
    }
}
