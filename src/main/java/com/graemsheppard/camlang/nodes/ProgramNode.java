package com.graemsheppard.camlang.nodes;

import lombok.Getter;

import java.util.List;

public class ProgramNode {

    @Getter
    private List<StatementNode> statementNodes;

    public ProgramNode(List<StatementNode> statementNodes) {
        this.statementNodes = statementNodes;
    }

}
