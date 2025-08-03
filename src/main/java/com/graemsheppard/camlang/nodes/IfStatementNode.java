package com.graemsheppard.camlang.nodes;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class IfStatementNode extends StatementNode {

    @Getter
    private ExpressionNode condition;

    @Getter
    private List<StatementNode> statements;

    public IfStatementNode(ExpressionNode expression) {
        condition = expression;
        statements = new ArrayList<>();
    }
}
