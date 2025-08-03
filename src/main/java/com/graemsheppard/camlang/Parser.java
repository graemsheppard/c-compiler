package com.graemsheppard.camlang;

import com.graemsheppard.camlang.enums.TokenType;
import com.graemsheppard.camlang.nodes.*;
import lombok.Getter;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

public class Parser {

    @Getter
    private final List<Token> tokens;

    private int index = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public ProgramNode parse() {
        index = 0;
        return parseProgram();
    }

    public ProgramNode parseProgram() {
        List<StatementNode> statementNodes = new ArrayList<>();
        while (currentToken().getType() != TokenType.EOF) {
            statementNodes.add(parseStatement());
        }
        return new ProgramNode(statementNodes);
    }

    public StatementNode parseStatement() {
        StatementNode statementNode;
        if (currentToken().type == TokenType.INT && nextToken().type == TokenType.IDENTIFIER) {
            consume();
            String id = currentToken().getText();
            consume();
            if (currentToken().getType() != TokenType.ASSIGN)
                throw new RuntimeException("Unexpected token: " + currentToken().getText() + ", '=' expected.");
            consume();
            statementNode = new DeclarationStatementNode(id, parseExpression_p0());
            if (currentToken().type != TokenType.SEMICOLON)
                throw new RuntimeException("Unexpected token: " + currentToken().text + ", ';' expected");
            consume();
        } else if (currentToken().getType() == TokenType.IDENTIFIER && nextToken().getType() == TokenType.ASSIGN) {
            String id = currentToken().getText();
            consume();
            consume();
            statementNode = new AssignmentStatementNode(id, parseExpression_p0());
            if (currentToken().type != TokenType.SEMICOLON)
                throw new RuntimeException("Unexpected token: " + currentToken().text + ", ';' expected");
            consume();
        } else if (currentToken().type == TokenType.EXIT && nextToken().type == TokenType.OPEN_PARENTHESIS) {
            consume();
            consume();
            statementNode = new ExitStatementNode(parseExpression_p0());
            if (currentToken().getType() != TokenType.CLOSE_PARENTHESIS)
                throw new RuntimeException("')' expected");
            consume();
            if (currentToken().type != TokenType.SEMICOLON)
                throw new RuntimeException("Unexpected token: " + currentToken().text + ", ';' expected");
            consume();
        } else if (currentToken().getType() == TokenType.IF && nextToken().getType() == TokenType.OPEN_PARENTHESIS) {
            consume();
            consume();
            statementNode = new IfStatementNode(parseExpression_p0());
            if (currentToken().getType() != TokenType.CLOSE_PARENTHESIS)
                throw new RuntimeException("')' expected");
            consume();
            if (currentToken().getType() != TokenType.OPEN_BRACE)
                throw new RuntimeException("'{' expected");
            consume();
            while (currentToken().getType() != TokenType.CLOSE_BRACE) {
                ((IfStatementNode)statementNode).getStatements().add(parseStatement());
            }
            consume();
        } else {
            throw new RuntimeException("Invalid statement");
        }

        return statementNode;
    }

    public ExpressionNode parseExpression_p0() {
        ExpressionNode expressionNodeA = parseExpression_p1();
        while(true) {
            if (currentToken().isBooleanOperator()) {
                var operator = currentToken().type;
                consume();
                ExpressionNode expressionNodeB = parseExpression_p1();
                expressionNodeA = new BinaryExpressionNode(expressionNodeA, expressionNodeB);
                ((BinaryExpressionNode)expressionNodeA).setOperator(operator);
            } else {
                return expressionNodeA;
            }
        }
    }

    public ExpressionNode parseExpression_p1() {
        ExpressionNode expressionNodeA = parseExpression_p2();
        while(true) {
            if (currentToken().type == TokenType.PLUS || currentToken().type == TokenType.MINUS) {
                var operator = currentToken().type;
                consume();
                ExpressionNode expressionNodeB = parseExpression_p2();
                expressionNodeA = new BinaryExpressionNode(expressionNodeA, expressionNodeB);
                ((BinaryExpressionNode)expressionNodeA).setOperator(operator);
            } else {
                return expressionNodeA;
            }
        }
    }

    public ExpressionNode parseExpression_p2() {
        ExpressionNode expressionNodeA;
        if (currentToken().type == TokenType.INTEGER_LITERAL || currentToken().type == TokenType.FLOAT_LITERAL) {
            expressionNodeA = new ValueExpressionNode(currentToken().getText());
            consume();
        } else if (currentToken().getType() == TokenType.IDENTIFIER) {
            expressionNodeA = new IdentifierExpressionNode(currentToken().getText());
            consume();
        } else {
            throw new RuntimeException("Expected literal or identifier type, got: " + currentToken().type);
        }
        while(true) {
            if (currentToken().type == TokenType.MULTIPLY || currentToken().type == TokenType.DIVIDE) {
                TokenType operator = currentToken().type;
                consume();
                ExpressionNode expressionNodeB;
                if (currentToken().type == TokenType.INTEGER_LITERAL || currentToken().type == TokenType.FLOAT_LITERAL) {
                    expressionNodeB = new ValueExpressionNode(currentToken().getText());
                } else if (currentToken().getType() == TokenType.IDENTIFIER) {
                    expressionNodeB = new IdentifierExpressionNode(currentToken().getText());
                } else {
                    throw new RuntimeException("Expected literal or identifier type, got: " + currentToken().type);
                }
                consume();
                expressionNodeA = new BinaryExpressionNode(expressionNodeA, expressionNodeB);
                ((BinaryExpressionNode) expressionNodeA).setOperator(operator);
            } else {
                return expressionNodeA;
            }
        }
    }

    private void consume() {
        index++;
    }

    @NonNull
    private Token currentToken() {
        return tokens.get(index);
    }

    @NonNull
    private Token nextToken() {
        return tokens.get(index + 1);
    }





}
