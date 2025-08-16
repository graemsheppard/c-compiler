package com.graemsheppard.clang.enums;

public enum TokenType {
    // Keywords
    IF("if"),
    ELSE("else"),
    WHILE("while"),
    FOR("for"),
    FUNCTION,
    RETURN("return"),
    EXIT("exit"),

    // Data types
    INT("int"),
    FLOAT("float"),
    STRING("string"),
    BOOL("bool"),

    // Operators
    PLUS("+"),
    MINUS("-"),
    STAR("*"),
    FSLASH("/"),
    ASSIGN("="),
    EQ("=="),
    GEQ(">="),
    LEQ("<="),
    GT(">"),
    LT("<"),
    NEQ("!="),
    AMP("&"),


    // Delimiters
    SEMICOLON(";"),
    COMMA(","),
    COLON(":"),
    OPEN_PARENTHESIS("("),
    CLOSE_PARENTHESIS(")"),
    OPEN_BRACE("{"),
    CLOSE_BRACE("}"),
    OPEN_BRACKET("["),
    CLOSE_BRACKET("]"),

    // Identifier
    IDENTIFIER,

    // Literals
    INTEGER_LITERAL,
    FLOAT_LITERAL,
    STRING_LITERAL,

    // Comments
    SINGLE_LINE_COMMENT,
    MULTI_LINE_COMMENT,

    // End of file
    EOF("\0");

    private String text;

    TokenType() {}

    TokenType(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }
}
