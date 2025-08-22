package com.graemsheppard.clang.nodes;

import com.graemsheppard.clang.enums.DataType;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class FunctionCallExpressionNode extends ExpressionNode {

    @Getter
    private final String identifier;

    @Getter
    private final List<ExpressionNode> params;

    public FunctionCallExpressionNode(String id, DataType dt) {
        identifier = id;
        params = new ArrayList<>();
        type = dt;
    }
}
