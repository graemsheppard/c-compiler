package com.graemsheppard.camlang;

import com.graemsheppard.camlang.enums.Register;
import com.graemsheppard.camlang.enums.TokenType;
import com.graemsheppard.camlang.instructions.Instruction;
import com.graemsheppard.camlang.instructions.MovInstruction;
import com.graemsheppard.camlang.instructions.PopInstruction;
import com.graemsheppard.camlang.instructions.PushInstruction;
import com.graemsheppard.camlang.instructions.operands.MemoryOperand;
import com.graemsheppard.camlang.instructions.operands.RegisterOperand;
import com.graemsheppard.camlang.nodes.*;

import java.util.*;

public class Generator {

    private static final String[] BUILT_IN_STRINGS = {
            "Program exited with code"
    };

    private HashMap<String, Integer> variables;

    private SortedMap<String, Scope> scopes;

    private ProgramNode root;

    private int stackSize = 0;

    private int branchCount = 0;

    public Generator (ProgramNode programRoot) {
        root = programRoot;
    }

    public String generate() {
        variables = new HashMap<>();
        scopes = new TreeMap<>();
        scopes.put("_global_", new Scope("_global_", 0));
        return generateProgram(this.root);
    }

    private String generateProgram(ProgramNode node) {
        StringBuilder res = new StringBuilder();
        res.append("""
                default rel
            section .data
            """);
        for (int i = 0; i < BUILT_IN_STRINGS.length; i++) {
            res.append("\tstr_").append(i).append(": \tdb \"").append(BUILT_IN_STRINGS[i]).append("\", \t10\n");
        }
        res.append("""
            section .text
                global _main
            _main:
            """);
        for (StatementNode statement : node.getStatementNodes()) {
            res.append(generateStatement(statement));
        }
        res.append("""
                jmp     exit
            exit:
                mov     rax,    0x02000004
                mov     rdi,    1
                lea     rsi,    [str_0]
            """);
        res.append("\tmov \trdx, \t").append(BUILT_IN_STRINGS[0].length() + 1).append("\n");
        res.append("""
                syscall
                mov     rax,    0x02000001
                mov     rdi,    r10
                syscall
            """);
        return res.toString();
    }

    private List<Instruction> generateStatement(StatementNode node) {
        StringBuilder res = new StringBuilder();

        if (node instanceof DeclarationStatementNode declarationNode) {
            res.append(generateExpression(declarationNode.getExpression()));
            variables.put(declarationNode.getIdentifier(), stackSize);
            currentScope().getVariables().add(declarationNode.getIdentifier());
        } else if (node instanceof ExitStatementNode exitStatementNode) {
            res.append(generateExpression(exitStatementNode.getExpressionNode()));
            res.append(generatePop("r10"));
            res.append("\tcall \texit\n");
        } else if (node instanceof IfStatementNode ifStatementNode) {
            String scopeName = "if_" + branchCount;
            String branchName = getBranchName();
            scopes.put(scopeName, new Scope(scopeName, stackSize));

            res.append(generateExpression(ifStatementNode.getCondition()));
            res.append(generatePop("rax"));
            res.append("\tcmp \trax, \t1\n");
            res.append("\tjne \t").append(branchName).append("\n");
            for (StatementNode statementNode : ifStatementNode.getStatements()) {
                res.append(generateStatement(statementNode));
            }
            res.append(branchName).append(":\n");

            for (String variableName : currentScope().getVariables()) {
                variables.remove(variableName);
            }

            int stackOffset = stackSize - currentScope().getStackOffset();
            stackSize -= stackOffset;
            res.append("\tadd \trsp, \t").append(stackOffset * 8).append("\n");
            scopes.remove(scopes.lastKey());


        } else if (node instanceof AssignmentStatementNode assignmentStatementNode) {
            if (!variables.containsKey(assignmentStatementNode.getIdentifier()))
                throw new RuntimeException("Variable used before it was declared: " + assignmentStatementNode.getIdentifier());
            res.append(generateExpression(assignmentStatementNode.getExpression()));
            res.append(generatePop("rax"));

            int variableLoc = variables.get(assignmentStatementNode.getIdentifier());
            int realOffset = (stackSize - variableLoc) * 8;
            res.append("\tmov \t[rsp+").append(realOffset).append("], \trax\n");
        }

        return res.toString();
    }

    private List<Instruction> generateExpression(ExpressionNode node) {
        List<Instruction> res = new ArrayList<>();

        if (node instanceof ValueExpressionNode valueNode ) {
            res.add(new MovInstruction(Register.RAX, valueNode.getValue());
            res.add(generatePush(Register.RAX));
        } else if (node instanceof IdentifierExpressionNode identifierNode) {
            if (!variables.containsKey(identifierNode.getIdentifier()))
                throw new RuntimeException("Variable used before it was declared: " + identifierNode.getIdentifier());
            int variableLoc = variables.get(identifierNode.getIdentifier());

            int realOffset = (stackSize - variableLoc) * 8;
            res.add(new MovInstruction(Register.RAX, new MemoryOperand(Register.RSP, realOffset)));
            res.add("\tmov \trax, \t[rsp+").append(realOffset).append("]\n");
            res.append(generatePush("rax"));
        } else if (node instanceof BinaryExpressionNode binaryNode) {
            res.append(generateExpression(binaryNode.getLeftExpression()));
            res.append(generateExpression(binaryNode.getRightExpression()));

            res.append(generatePop("rbx"));
            res.append(generatePop("rax"));

            String instr = switch (binaryNode.getOperator()) {
                case MULTIPLY -> "imul";
                case PLUS -> "add";
                case DIVIDE -> "idiv";
                case MINUS -> "sub";
                case EQ, GEQ, LEQ, GT, LT, NEQ -> "cmp";

                default -> throw new RuntimeException("Unknown operator: " + binaryNode.getOperator().getText());
            };

            res.append("\t").append(instr).append(" \trax, \trbx \n");
            if (binaryNode.getOperator() == TokenType.EQ) {
                res.append("\tsete \tal\n");
                res.append("\tmovzx \trax, \tal\n");
            } else if (binaryNode.getOperator() == TokenType.GEQ) {
                res.append("\tsetge \tal\n");
                res.append("\tmovzx \trax, \tal\n");
            } else if (binaryNode.getOperator() == TokenType.LEQ) {
                res.append("\tsetle \tal\n");
                res.append("\tmovzx \trax, \tal\n");
            } else if (binaryNode.getOperator() == TokenType.GT) {
                res.append("\tsetg \tal\n");
                res.append("\tmovzx \trax, \tal\n");
            } else if (binaryNode.getOperator() == TokenType.LT) {
                res.append("\tsetl \tal\n");
                res.append("\tmovzx \trax, \tal\n");
            } else if (binaryNode.getOperator() == TokenType.NEQ) {
                res.append("\tsetne \tal\n");
                res.append("\tmovzx \trax, \tal\n");
            }
            res.append(generatePush("rax"));
        }

        return res.toString();
    }

    private String getBranchName() {
        return "br_" + branchCount++;
    }

    private PushInstruction generatePush(Register register) {
        stackSize++;
        return new PushInstruction(new RegisterOperand(register));
    }

    private PopInstruction generatePop(Register register) {
        stackSize--;
        return new PopInstruction(new RegisterOperand(register));
    }

    private Scope currentScope() {
        return scopes.get(scopes.lastKey());
    }


}
