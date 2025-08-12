package com.graemsheppard.clang.nodes;

import lombok.Getter;

public class ReturnStatementNode extends StatementNode {

    @Getter
    private ExpressionNode expression;

    public ReturnStatementNode(ExpressionNode expr) {
        expression = expr;
    }
}
