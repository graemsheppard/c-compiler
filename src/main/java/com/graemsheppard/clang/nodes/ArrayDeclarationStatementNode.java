package com.graemsheppard.clang.nodes;

import com.graemsheppard.clang.enums.DataType;
import lombok.Getter;

import java.util.List;

public class ArrayDeclarationStatementNode extends StatementNode {

    @Getter
    private final DataType type;

    @Getter
    private final List<ValueExpressionNode> values;

    @Getter
    private final String identifier;

    @Getter
    private final int size;

    public ArrayDeclarationStatementNode(String identifier, DataType type, List<ValueExpressionNode> values) {
        this.identifier = identifier;
        this.type = type;
        this.values = values;
        this.size = values.size();
    }

    public ArrayDeclarationStatementNode(String identifier, DataType type, int size) {
        this.identifier = identifier;
        this.type = type;
        this.size = size;
        this.values = null;
    }

}
