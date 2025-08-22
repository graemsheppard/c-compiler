package com.graemsheppard.clang.nodes;

import com.graemsheppard.clang.enums.DataType;
import lombok.Getter;

public class IdentifierExpressionNode extends ExpressionNode {

    @Getter
    private final String identifier;

    public IdentifierExpressionNode(String id, DataType dt) {
        identifier = id;
        type = dt;
    }
}
