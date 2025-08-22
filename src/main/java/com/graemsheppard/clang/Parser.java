package com.graemsheppard.clang;

import com.graemsheppard.clang.enums.ControlType;
import com.graemsheppard.clang.enums.DataType;
import com.graemsheppard.clang.enums.TokenType;
import com.graemsheppard.clang.fragments.IfElseFragment;
import com.graemsheppard.clang.nodes.*;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

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
     * Keeps track of variable names and types
     */
    private final SymbolTable symbolTable;

    private final Stack<String> scopes;

    private int scopeCount = 0;

    /**
     * @param tokens the list of tokens to parse
     */
    public Parser(List<Token> tokens, SymbolTable table) {
        this.tokens = tokens;
        this.symbolTable = table;
        this.scopes = new Stack<>();
        this.scopes.push("_global_");
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
        if (peekToken(0).getType() == TokenType.INT && peekToken(1).getType() == TokenType.IDENTIFIER && peekToken(2).getType() == TokenType.OPEN_PARENTHESIS) {
            String id = scanToken(1).getText();
            symbolTable.add(id, DataType.INTEGER, currentScope(), outerScopes());
            scanToken();
            List<DeclarationStatementNode> params = new ArrayList<>();
            while(peekToken(0).getType() != TokenType.CLOSE_PARENTHESIS) {
                params.add(parseDeclaration());
                if (peekToken(0).getType() != TokenType.COMMA && peekToken(0).getType() != TokenType.CLOSE_PARENTHESIS)
                    throw new RuntimeException("Unexpected token");
                if (peekToken(0).getType() == TokenType.COMMA)
                    scanToken();
            }
            scanToken();
            if (scanToken().getType() != TokenType.OPEN_BRACE)
                throw new RuntimeException("'{' expected");
            List<StatementNode> body = new ArrayList<>();
            createScope();
            while (peekToken(0).getType() != TokenType.CLOSE_BRACE) {
                body.add(parseStatement());
            }
            destroyScope();
            statementNode = new FunctionDeclarationStatementNode(id, params, body);
            scanToken();
        } else if (peekToken(0).getType() == TokenType.IDENTIFIER && (peekToken(1).getType() == TokenType.ASSIGN || peekToken(1).getType() == TokenType.OPEN_BRACKET)) {
            // Assignment statement, parse right side as expression
            String id = scanToken().getText();

            // Check if we are assigning an array value
            if (scanToken().getType() == TokenType.OPEN_BRACKET) {
                var offset = parseExpression_p0();
                scanToken(1);
                statementNode = new AssignmentStatementNode(id, parseExpression_p0(), offset);
            } else {
                statementNode = new AssignmentStatementNode(id, parseExpression_p0());
            }

            if (scanToken().getType() != TokenType.SEMICOLON)
                throw new RuntimeException("Unexpected token, ';' expected");
        } else if (peekToken(0).getType() == TokenType.RETURN) {
            scanToken();
            statementNode = new ReturnStatementNode(parseExpression_p0());
            if (scanToken().getType() != TokenType.SEMICOLON)
                throw new RuntimeException("Unexpected token, ';' expected");
        } else if (peekToken(0).getType() == TokenType.INT && peekToken(1).getType() == TokenType.IDENTIFIER) {
            // Declaration statement, parse right side as expression
            String type = scanToken().getText();
            String id = scanToken().getText();
            symbolTable.add(id, DataType.from(type), currentScope(), outerScopes());
            if (peekToken(0).getType() == TokenType.OPEN_BRACKET) {
                int size = Integer.parseInt(scanToken(1).getText());
                var dataType = DataType.from(type);
                statementNode = new ArrayDeclarationStatementNode(id, dataType, size);
                scanToken(1);
            } else if (peekToken(0).getType() == TokenType.ASSIGN) {
                scanToken();
                statementNode = new DeclarationStatementNode(id, parseExpression_p0());
                if (scanToken().getType() != TokenType.SEMICOLON)
                    throw new RuntimeException("Unexpected token, ';' expected");
            }
            else if (scanToken().getType() == TokenType.SEMICOLON)
                statementNode = new DeclarationStatementNode(id);
            else
                throw new RuntimeException("Unexpected token, ';' expected");
        } else if (peekToken(0).getType() == TokenType.INT && peekToken(1).getType() == TokenType.STAR && peekToken(2).getType() == TokenType.IDENTIFIER) {
            String id = scanToken(2).getText();
            symbolTable.add(id, DataType.INTEGER, currentScope(), outerScopes());
            if (peekToken(0).getType() == TokenType.ASSIGN) {
                scanToken();
                statementNode = new DeclarationStatementNode(id, parseExpression_p0(), true);
                if (scanToken().getType() != TokenType.SEMICOLON)
                    throw new RuntimeException("Unexpected token, ';' expected");
            }
            else if (scanToken().getType() == TokenType.SEMICOLON)
                statementNode = new DeclarationStatementNode(id, true);
            else
                throw new RuntimeException("Unexpected token, ';' expected");
        } else if (peekToken(0).getType() == TokenType.EXIT && peekToken(1).getType() == TokenType.OPEN_PARENTHESIS) {
            // Exit statement, parse inside parenthesis as expression
            scanToken(1);
            statementNode = new ExitStatementNode(parseExpression_p0());
            if (scanToken().getType() != TokenType.CLOSE_PARENTHESIS)
                throw new RuntimeException("')' expected");

            if (scanToken().getType() != TokenType.SEMICOLON)
                throw new RuntimeException("Unexpected token, ';' expected");

        } else if (peekToken(0).getType() == TokenType.IF && peekToken(1).getType() == TokenType.OPEN_PARENTHESIS) {
            // If statement, parse inside parenthesis as expression
            scanToken(1);
            var ifElseStmt = new IfElseStatementNode();
            var condition = parseExpression_p0();
            createScope();
            var mainIfFrag = new IfElseFragment(ControlType.IF, condition, currentScope());
            if (scanToken().getType() != TokenType.CLOSE_PARENTHESIS)
                throw new RuntimeException("')' expected");
            if (scanToken().getType() != TokenType.OPEN_BRACE)
                throw new RuntimeException("'{' expected");
            // Parse body of if statement as array of statements

            while (peekToken(0).getType() != TokenType.CLOSE_BRACE) {
                mainIfFrag.getBody().add(parseStatement());
            }
            destroyScope();

            ifElseStmt.getParts().add(mainIfFrag);
            scanToken();

            // Check for else if
            while(peekToken(0).getType() == TokenType.ELSE && peekToken(1).getType() == TokenType.IF && peekToken(2).getType() == TokenType.OPEN_PARENTHESIS) {
                scanToken(2);
                var elseCondition = parseExpression_p0();
                createScope();
                var frag = new IfElseFragment(ControlType.IF_ELSE, elseCondition, currentScope());

                if (scanToken().getType() != TokenType.CLOSE_PARENTHESIS)
                    throw new RuntimeException("')' expected");
                if (scanToken().getType() != TokenType.OPEN_BRACE)
                    throw new RuntimeException("'{' expected");

                while (peekToken(0).getType() != TokenType.CLOSE_BRACE) {
                    frag.getBody().add(parseStatement());
                }
                destroyScope();
                ifElseStmt.getParts().add(frag);
                scanToken();
            }

            // Check for else
            if (peekToken(0).getType() == TokenType.ELSE) {
                if (scanToken(1).getType() != TokenType.OPEN_BRACE)
                    throw new RuntimeException("'{' expected");
                createScope();
                var frag = new IfElseFragment(ControlType.ELSE, currentScope());
                while (peekToken(0).getType() != TokenType.CLOSE_BRACE) {
                    frag.getBody().add(parseStatement());
                }
                destroyScope();
                ifElseStmt.getParts().add(frag);
                scanToken();
            }
            statementNode = ifElseStmt;

        } else if (peekToken(0).getType() == TokenType.WHILE && peekToken(1).getType() == TokenType.OPEN_PARENTHESIS) {
            scanToken(1);
            var condition = parseExpression_p0();
            var body = new ArrayList<StatementNode>();

            if (scanToken().getType() != TokenType.CLOSE_PARENTHESIS)
                throw new RuntimeException("')' expected");
            if (scanToken().getType() != TokenType.OPEN_BRACE)
                throw new RuntimeException("'{' expected");
            createScope();
            while (peekToken(0).getType() != TokenType.CLOSE_BRACE) {
                body.add(parseStatement());
            }
            destroyScope();
            scanToken();
            statementNode = new WhileStatementNode(condition, body);
        } else {
            // Try to parse as an expression only as a fallback
            statementNode = parseExpression_p0();
            if (scanToken().getType() != TokenType.SEMICOLON)
                throw new RuntimeException("Unexpected token, ';' expected");
        }

        return statementNode;
    }

    public DeclarationStatementNode parseDeclaration() {
        String type = scanToken().getText();
        String id = scanToken().getText();
        symbolTable.add(id, DataType.from(type), currentScope(), outerScopes());
        if (peekToken(0).getType() == TokenType.ASSIGN) {
            scanToken();
            return new DeclarationStatementNode(id, parseExpression_p0());
        } else if (peekToken(0).getType() == TokenType.SEMICOLON || peekToken(0).getType() == TokenType.CLOSE_PARENTHESIS || peekToken(0).getType() == TokenType.COMMA) {
            return new DeclarationStatementNode(id);
        } else {
            throw new RuntimeException("Unexpected token");
        }
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
        ExpressionNode expressionNodeA = parseExpression_p3();
        while(true) {
            if (peekToken(0).getType() == TokenType.STAR || peekToken(0).getType() == TokenType.FSLASH) {
                TokenType operator = scanToken().getType();
                ExpressionNode expressionNodeB = parseExpression_p3();

                // Create expression node with both the left and right sides
                expressionNodeA = new BinaryExpressionNode(expressionNodeA, expressionNodeB);
                ((BinaryExpressionNode) expressionNodeA).setOperator(operator);
            } else {
                // No operator, just return the (Value|Identifier)Node
                return expressionNodeA;
            }
        }
    }

    public ExpressionNode parseExpression_p3() {
        if (peekToken(0).getType() == TokenType.STAR) {
            scanToken();
            return new DereferenceExpressionNode(parseExpression_p4());
        } else {
            return parseExpression_p4();
        }
    }

    public ExpressionNode parseExpression_p4() {
        ExpressionNode node;
        if (peekToken(0).getType() == TokenType.IDENTIFIER && peekToken(1).getType() == TokenType.OPEN_PARENTHESIS) {
            String id = scanToken().getText();
            FunctionCallExpressionNode functionNode = new FunctionCallExpressionNode(id, symbolTable.get(id, currentScope()).getType());
            scanToken();
            while (peekToken(0).getType() != TokenType.CLOSE_PARENTHESIS) {
                functionNode.getParams().add(parseExpression_p0());
                if (peekToken(0).getType() == TokenType.COMMA)
                    scanToken();
            }
            scanToken();
            node = functionNode;
        } else if (peekToken(0).getType() == TokenType.IDENTIFIER && peekToken(1).getType() == TokenType.OPEN_BRACKET) {
            String id = scanToken().getText();
            scanToken();
            ArrayReferenceExpressionNode arrayNode = new ArrayReferenceExpressionNode(id, parseExpression_p0());
            scanToken();
            node = arrayNode;
        } else {
            node = parseTerm();
        }
        return node;
    }

    public ExpressionNode parseTerm() {
        ExpressionNode term;
        if (peekToken(0).getType() == TokenType.INTEGER_LITERAL) {
            term = new ValueExpressionNode(Integer.parseInt(scanToken().getText()));
        } else if (peekToken(0).getType() == TokenType.FLOAT_LITERAL) {
            term = new ValueExpressionNode(Float.parseFloat(scanToken().getText()));
        } else if (peekToken(0).getType() == TokenType.IDENTIFIER) {
            String id = scanToken().getText();
            term = new IdentifierExpressionNode(id, symbolTable.get(id, currentScope()).getType());
        } else if (peekToken(0).getType() == TokenType.AMP && peekToken(1).getType() == TokenType.IDENTIFIER) {
            String id = scanToken(1).getText();
            term = new AddressExpressionNode(new IdentifierExpressionNode(id, symbolTable.get(id, currentScope()).getType()));
        } else {
            throw new RuntimeException("Expected literal or identifier type");
        }
        return term;

    }

    @NonNull
    private Token peekToken(int offset) {
        return tokens.get(index + offset);
    }

    @NonNull
    private Token scanToken() {
        return tokens.get(index++);
    }

    @NonNull
    private Token scanToken(int skip) {
        index += skip;
        return tokens.get(index++);
    }

    private String currentScope() {
        return scopes.peek();
    }

    private String outerScopes() {
        return String.join(",", scopes);
    }

    private void createScope() {
        scopes.push("s_" + scopeCount++);
    }

    private void destroyScope() {
        scopes.pop();
    }

}
