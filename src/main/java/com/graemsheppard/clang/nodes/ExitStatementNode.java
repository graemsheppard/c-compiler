package com.graemsheppard.clang.nodes;

import lombok.Getter;

public class ExitStatementNode extends StatementNode {

    @Getter
    private ExpressionNode expressionNode;

    public ExitStatementNode(ExpressionNode expression) {
        expressionNode = expression;
    }
}
