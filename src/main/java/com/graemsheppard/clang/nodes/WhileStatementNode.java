package com.graemsheppard.clang.nodes;

import lombok.Getter;

import java.util.List;

public class WhileStatementNode extends StatementNode {

    @Getter
    private final ExpressionNode condition;

    @Getter
    private final List<StatementNode> body;

    public WhileStatementNode(ExpressionNode condition, List<StatementNode> body) {
        this.condition = condition;
        this.body = body;
    }


}
