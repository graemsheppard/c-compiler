package com.graemsheppard.clang;

import com.graemsheppard.clang.enums.TokenType;

import java.util.ArrayList;
import java.util.List;

public class Tokenizer {

    /**
     * The text of the input file
     */
    private final String input;

    /**
     * @param input The text of the input file
     */
    public Tokenizer(String input) {
        this.input = input;
    }

    /**
     * Tokenizes the input
     * @return a list of Tokens
     */
    public List<Token> tokenize() {
        int cursor = 0;
        List<Token> tokens = new ArrayList<>(100);
        while (cursor < input.length()) {
            char current = input.charAt(cursor);
            if (Character.isWhitespace(current)) {
                cursor++;
                continue;
            }

            //<editor-fold desc="Delimiters">
            if (current == '(') {
                tokens.add(new Token(TokenType.OPEN_PARENTHESIS));
                cursor++;
                continue;
            }

            if (current == ')') {
                tokens.add(new Token(TokenType.CLOSE_PARENTHESIS));
                cursor++;
                continue;
            }

            if (current == '{') {
                tokens.add(new Token(TokenType.OPEN_BRACE));
                cursor++;
                continue;
            }

            if (current == '}') {
                tokens.add(new Token(TokenType.CLOSE_BRACE));
                cursor++;
                continue;
            }

            if (current == ';') {
                tokens.add(new Token(TokenType.SEMICOLON));
                cursor++;
                continue;
            }

            if (current == ',') {
                tokens.add(new Token(TokenType.COMMA));
                cursor++;
                continue;
            }
            //</editor-fold>

            //<editor-fold desc="Operators">
            if (current == '=' && input.charAt(cursor + 1) == '=') {
                tokens.add(new Token(TokenType.EQ));
                cursor+=2;
                continue;
            }

            if (current == '<' && input.charAt(cursor + 1) == '=') {
                tokens.add(new Token(TokenType.LEQ));
                cursor+=2;
                continue;
            }

            if (current == '>' && input.charAt(cursor + 1) == '=') {
                tokens.add(new Token(TokenType.GEQ));
                cursor+=2;
                continue;
            }

            if (current == '!' && input.charAt(cursor + 1) == '=') {
                tokens.add(new Token(TokenType.NEQ));
                cursor+=2;
                continue;
            }

            if (current == '>') {
                tokens.add(new Token(TokenType.GT));
                cursor++;
                continue;
            }

            if (current == '<') {
                tokens.add(new Token(TokenType.LT));
                cursor++;
                continue;
            }

            if (current == '=') {
                tokens.add(new Token(TokenType.ASSIGN));
                cursor++;
                continue;
            }

            if (current == '+') {
                tokens.add(new Token(TokenType.PLUS));
                cursor++;
                continue;
            }

            if (current == '-') {
                tokens.add(new Token(TokenType.MINUS));
                cursor++;
                continue;
            }

            if (current == '*') {
                tokens.add(new Token(TokenType.STAR));
                cursor++;
                continue;
            }

            if (current == '/') {
                tokens.add(new Token(TokenType.FSLASH));
                cursor++;
                continue;
            }

            if (current == '&') {
                tokens.add(new Token(TokenType.AMP));
                cursor++;
                continue;
            }

            //</editor-fold>

            //<editor-fold desc="Literals">

            if (Character.isDigit(current)) {
                StringBuilder tokenStringBuilder = new StringBuilder();
                tokenStringBuilder.append(current);
                int subCursor = cursor + 1;
                char subCurrent = input.charAt(subCursor);
                while (Character.isDigit(subCurrent) && subCursor < input.length()) {
                    tokenStringBuilder.append(subCurrent);
                    subCurrent = input.charAt(++subCursor);
                }
                if (Character.isLetter(subCurrent)) {
                    throw new RuntimeException("Integer literal may only contain numeric characters");
                }

                tokens.add(new Token(TokenType.INTEGER_LITERAL, tokenStringBuilder.toString()));
                cursor = subCursor;
                continue;
            }
            //</editor-fold>

            //<editor-fold desc="Keywords"
            if (Character.isLetter(current)) {
                StringBuilder tokenStringBuilder = new StringBuilder();
                tokenStringBuilder.append(current);
                int subCursor = cursor + 1;
                char subCurrent = input.charAt(subCursor);
                while (Character.isLetterOrDigit(subCurrent) && subCursor < input.length()) {
                    tokenStringBuilder.append(subCurrent);
                    subCurrent = input.charAt(++subCursor);
                }
                String tokenString = tokenStringBuilder.toString();
                if (tokenString.equals(TokenType.INT.getText())) {
                    tokens.add(new Token(TokenType.INT));
                } else if (tokenString.equals(TokenType.FLOAT.getText())) {
                    tokens.add(new Token(TokenType.FLOAT));
                } else if (tokenString.equals(TokenType.BOOL.getText())) {
                    tokens.add(new Token(TokenType.BOOL));
                } else if (tokenString.equals(TokenType.EXIT.getText())) {
                    tokens.add(new Token(TokenType.EXIT));
                } else if (tokenString.equals(TokenType.IF.getText())) {
                    tokens.add(new Token(TokenType.IF));
                } else if (tokenString.equals(TokenType.ELSE.getText())) {
                    tokens.add(new Token(TokenType.ELSE));
                } else if (tokenString.equals(TokenType.RETURN.getText())) {
                    tokens.add(new Token(TokenType.RETURN));
                } else if (tokenString.equals(TokenType.WHILE.getText())) {
                    tokens.add(new Token(TokenType.WHILE));
                } else {
                    tokens.add(new Token(TokenType.IDENTIFIER, tokenString));
                }
                cursor = subCursor;
                continue;
            }
            //</editor-fold>

            // Unhandled
            cursor++;
        }
        tokens.add(new Token(TokenType.EOF));
        return tokens;
    }
}
