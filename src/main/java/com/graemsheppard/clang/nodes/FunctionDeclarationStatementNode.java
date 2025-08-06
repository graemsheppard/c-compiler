package com.graemsheppard.clang.nodes;

import lombok.Getter;

import java.util.List;

public class FunctionDeclarationStatementNode extends StatementNode {

    @Getter
    private final String identifier;

    @Getter
    private final List<StatementNode> body;

    @Getter
    private final List<DeclarationStatementNode> params;

    public FunctionDeclarationStatementNode(String id, List<DeclarationStatementNode> params, List<StatementNode> body) {
        this.identifier = id;
        this.params = params;
        this.body = body;
    }
}
