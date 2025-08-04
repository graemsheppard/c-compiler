package com.graemsheppard.camlang.nodes;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class FunctionCallExpressionNode extends ExpressionNode {

    @Getter
    private final String identifier;

    @Getter
    private final List<ExpressionNode> params;

    public FunctionCallExpressionNode(String id) {
        identifier = id;
        params = new ArrayList<>();
    }
}
