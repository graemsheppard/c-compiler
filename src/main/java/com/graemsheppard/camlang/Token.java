package com.graemsheppard.camlang;

import com.graemsheppard.camlang.enums.TokenType;
import lombok.Getter;

public class Token {

    @Getter
    protected TokenType type;

    @Getter
    public String text;


    public Token(TokenType type) {
        this.text = type.getText();
        this.type = type;
    }

    public Token(TokenType type, String text) {
        this.type = type;
        this.text = text;
    }

    public boolean isBooleanOperator() {
        return this.type == TokenType.EQ
                || this.type == TokenType.NEQ
                || this.type == TokenType.GT
                || this.type == TokenType.LT
                || this.type == TokenType.GEQ
                || this.type == TokenType.LEQ;
    }

}
