package com.graemsheppard.clang.nodes;

import com.graemsheppard.clang.enums.DataType;
import lombok.Getter;

public class AddressExpressionNode extends ExpressionNode {

    @Getter
    private final IdentifierExpressionNode identifier;

    public AddressExpressionNode(IdentifierExpressionNode id) {
        identifier = id;
        type = DataType.INTEGER;
    }
}
