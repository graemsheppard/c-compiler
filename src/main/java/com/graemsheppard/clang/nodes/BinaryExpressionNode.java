package com.graemsheppard.clang.nodes;

import com.graemsheppard.clang.enums.TokenType;
import lombok.Getter;
import lombok.Setter;

public class BinaryExpressionNode extends ExpressionNode {

    @Getter
    @Setter
    private TokenType operator;

    @Getter
    @Setter
    private ExpressionNode leftExpression;

    @Getter
    @Setter
    private ExpressionNode rightExpression;

    public BinaryExpressionNode() {}

    public BinaryExpressionNode(ExpressionNode left) {
        leftExpression = left;
    }

    public BinaryExpressionNode(ExpressionNode left, ExpressionNode right) {
        leftExpression = left;
        rightExpression = right;
    }

}
