package com.graemsheppard.clang.nodes;

import com.graemsheppard.clang.enums.DataType;
import lombok.Getter;

public class ExpressionNode extends StatementNode {

    @Getter
    protected DataType type = DataType.INFERRED;
}
