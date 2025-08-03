package com.graemsheppard.camlang;

import com.graemsheppard.camlang.enums.TokenType;
import com.graemsheppard.camlang.nodes.*;
import lombok.Getter;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

public class Parser {

    /**
     * The list of tokens to parse
     */
    private final List<Token> tokens;

    /**
     * Current index in the tokens array
     */
    private int index;

    /**
     * @param tokens the list of tokens to parse
     */
    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }


    /**
     * Initializes variables needed by the parser (in case it is run multiple times)
     * @return The root of the generated AST
     */
    public ProgramNode parse() {
        index = 0;
        return parseProgram();
    }

    /**
     * Evaluates tokens looking for root node
     * @return The program root node with the AST generated
     */
    public ProgramNode parseProgram() {
        List<StatementNode> statementNodes = new ArrayList<>();
        while (peekToken(0).getType() != TokenType.EOF) {
            statementNodes.add(parseStatement());
        }
        return new ProgramNode(statementNodes);
    }

    /**
     * Evaluates the tokens looking for statements
     * @return A statement node with its child nodes
     */
    public StatementNode parseStatement() {
        StatementNode statementNode;
        Token currentToken = scanToken();
        if (currentToken.getType() == TokenType.INT && peekToken(0).getType() == TokenType.IDENTIFIER) {
            // Declaration statement, parse right side as expression
            String id = scanToken().getText();
            if (scanToken().getType() != TokenType.ASSIGN)
                throw new RuntimeException("Unexpected token, '=' expected.");
            statementNode = new DeclarationStatementNode(id, parseExpression_p0());
            if (scanToken().getType() != TokenType.SEMICOLON)
                throw new RuntimeException("Unexpected token, ';' expected");
        } else if (currentToken.getType() == TokenType.IDENTIFIER && peekToken(0).getType() == TokenType.ASSIGN) {
            // Assignment statement, parse right side as expression
            String id = currentToken.getText();
            scanToken();
            statementNode = new AssignmentStatementNode(id, parseExpression_p0());
            if (scanToken().getType() != TokenType.SEMICOLON)
                throw new RuntimeException("Unexpected token, ';' expected");
        } else if (currentToken.getType() == TokenType.EXIT && peekToken(0).getType() == TokenType.OPEN_PARENTHESIS) {
            // Exit statement, parse inside parenthesis as expression
            scanToken();
            statementNode = new ExitStatementNode(parseExpression_p0());
            if (scanToken().getType() != TokenType.CLOSE_PARENTHESIS)
                throw new RuntimeException("')' expected");

            if (scanToken().getType() != TokenType.SEMICOLON)
                throw new RuntimeException("Unexpected token, ';' expected");

        } else if (currentToken.getType() == TokenType.IF && peekToken(0).getType() == TokenType.OPEN_PARENTHESIS) {
            // If statement, parse inside parenthesis as expression
            scanToken();
            statementNode = new IfStatementNode(parseExpression_p0());
            if (scanToken().getType() != TokenType.CLOSE_PARENTHESIS)
                throw new RuntimeException("')' expected");
            if (scanToken().getType() != TokenType.OPEN_BRACE)
                throw new RuntimeException("'{' expected");
            // Parse body of if statement as array of statements
            while (peekToken(0).getType() != TokenType.CLOSE_BRACE) {
                ((IfStatementNode)statementNode).getStatements().add(parseStatement());
            }
            scanToken();
        } else {
            throw new RuntimeException("Invalid statement");
        }

        return statementNode;
    }

    /**
     * Evaluates the tokens looking for an expression of precedence 0 (lowest precedence)
     * @return an ExpressionNode
     */
    public ExpressionNode parseExpression_p0() {
        // Parse left side
        ExpressionNode expressionNodeA = parseExpression_p1();
        while(true) {
            if (peekToken(0).isBooleanOperator()) {
                var operator = scanToken().getType();
                // Parse right ide and create new node to hold both left and right expressions
                ExpressionNode expressionNodeB = parseExpression_p1();
                expressionNodeA = new BinaryExpressionNode(expressionNodeA, expressionNodeB);
                ((BinaryExpressionNode)expressionNodeA).setOperator(operator);
            } else {
                // No operator of this precedence level, return left side
                return expressionNodeA;
            }
        }
    }

    /**
     * Evaluates the tokens looking for an expression of precedence 1
     * @return an ExpressionNode
     */
    public ExpressionNode parseExpression_p1() {
        // Parse left side
        ExpressionNode expressionNodeA = parseExpression_p2();
        while(true) {
            if (peekToken(0).getType() == TokenType.PLUS || peekToken(0).getType() == TokenType.MINUS) {
                var operator = scanToken().getType();
                // Parse right side and create new node to hold both left and right expressions
                ExpressionNode expressionNodeB = parseExpression_p2();
                expressionNodeA = new BinaryExpressionNode(expressionNodeA, expressionNodeB);
                ((BinaryExpressionNode)expressionNodeA).setOperator(operator);
            } else {
                // No operator of this precedence level, return left side
                return expressionNodeA;
            }
        }
    }

    /**
     * Evaluates the tokens looking for an expression of the highest precedence
     * @return an ExpressionNode
     */
    public ExpressionNode parseExpression_p2() {
        ExpressionNode expressionNodeA;
        // Parse left side as an identifier or a literal
        if (peekToken(0).getType() == TokenType.INTEGER_LITERAL || peekToken(0).getType() == TokenType.FLOAT_LITERAL) {
            expressionNodeA = new ValueExpressionNode(scanToken().getText());
        } else if (peekToken(0).getType() == TokenType.IDENTIFIER) {
            expressionNodeA = new IdentifierExpressionNode(scanToken().getText());
        } else {
            throw new RuntimeException("Expected literal or identifier type");
        }
        while(true) {
            if (peekToken(0).getType() == TokenType.MULTIPLY || peekToken(0).getType() == TokenType.DIVIDE) {
                TokenType operator = scanToken().getType();
                ExpressionNode expressionNodeB;

                // Parse the right side as a value or identifier
                if (peekToken(0).getType() == TokenType.INTEGER_LITERAL || peekToken(0).getType() == TokenType.FLOAT_LITERAL) {
                    expressionNodeB = new ValueExpressionNode(scanToken().getText());
                } else if (peekToken(0).getType() == TokenType.IDENTIFIER) {
                    expressionNodeB = new IdentifierExpressionNode(scanToken().getText());
                } else {
                    throw new RuntimeException("Expected literal or identifier type");
                }

                // Create expression node with both the left and right sides
                expressionNodeA = new BinaryExpressionNode(expressionNodeA, expressionNodeB);
                ((BinaryExpressionNode) expressionNodeA).setOperator(operator);
            } else {
                // No operator, just return the (Value|Identifier)Node
                return expressionNodeA;
            }
        }
    }

    @NonNull
    private Token peekToken(int offset) {
        return tokens.get(index + offset);
    }

    @NonNull
    private Token scanToken() {
        return tokens.get(index++);
    }



}
