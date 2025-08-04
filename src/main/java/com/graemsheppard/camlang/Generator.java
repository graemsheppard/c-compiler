package com.graemsheppard.camlang;

import com.graemsheppard.camlang.enums.ControlType;
import com.graemsheppard.camlang.enums.Register;
import com.graemsheppard.camlang.enums.TokenType;
import com.graemsheppard.camlang.instructions.*;
import com.graemsheppard.camlang.instructions.operands.MemoryOperand;
import com.graemsheppard.camlang.instructions.operands.RegisterOperand;
import com.graemsheppard.camlang.nodes.*;

import java.util.*;

/**
 * Takes in a Program Root node and generates the list of instructions
 * Optimization takes place on the instruction list before they are converted to text
 */
public class Generator {

    /**
     * Strings that are needed by the compiler
     */
    private static final String[] BUILT_IN_STRINGS = {
            "Program exited with code"
    };

    /**
     * Maps a variable name to its stack offset
     */
    private HashMap<String, Integer> variables;

    /**
     * Maps a scope name to its scope, should always contain at least 1 scope "_global_"
     */
    private SortedMap<String, Scope> scopes;

    /**
     * The root of the AST
     */
    private final ProgramNode root;

    /**
     * Keeps track of the number of items on the stack,
     * should not be modified outside of the dedicated methods
     * generatePop and generatePush
     */
    private int stackSize = 0;

    /**
     * Keeps track of the number of ifStatements for branch naming
     */
    private int ifStatementCount = 0;

    public Generator (ProgramNode programRoot) {
        root = programRoot;
    }

    /**
     * Initialized required variables and starts the generation from the root node
     * @return A string representing the compiled program
     */
    public String generate() {
        variables = new HashMap<>();
        scopes = new TreeMap<>();
        scopes.put("_global_", new Scope("_global_", 0));
        return generateProgram(this.root);
    }

    /**
     * Inserts the boilerplate assembly and generates code for each statement
     * that is a direct descendant of the root node
     * @param node The root of the AST
     * @return A string representing the compiled program
     */
    private String generateProgram(ProgramNode node) {
        StringBuilder res = new StringBuilder();
        // Data section with required strings
        res.append("""
                default rel
            section .data
            """);
        for (int i = 0; i < BUILT_IN_STRINGS.length; i++) {
            res.append("\tstr_").append(i).append(": \tdb \"").append(BUILT_IN_STRINGS[i]).append("\", \t10\n");
        }
        // Text section for global declarations
        res.append("""
            section .text
                global _main
            _main:
            """);

        // Body of the main program
        List<Instruction> instructions = new ArrayList<>();
        for (StatementNode statement : node.getStatementNodes()) {
            instructions.addAll(generateStatement(statement));
        }

        // Add all the instructions to the main body
        for (int i = 0; i < instructions.size(); i++) {

            Instruction current = instructions.get(i);
            if (i >= instructions.size() - 1) {
                res.append(current.toString());
                break;
            }

            Instruction next = instructions.get(i + 1);

            // Remove any PUSH rn that is immediately followed by a POP rn where rn is the register
            if (current instanceof PushInstruction pushInstruction
                    && next instanceof PopInstruction popInstruction) {
                if (pushInstruction.getOperand() instanceof RegisterOperand reg1
                        && popInstruction.getOperand() instanceof RegisterOperand reg2) {
                    if (reg1.getRegister() == reg2.getRegister()) {
                        i++;
                        continue;
                    }
                }
            }

            res.append(current.toString());
        }

        // Built in subroutines
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

    /**
     * Builds a list of instructions based on the tree starting at the statement node
     * @param node The statement node
     * @return A list of instructions
     */
    private List<Instruction> generateStatement(StatementNode node) {
        List<Instruction> res = new ArrayList<>();

        if (node instanceof DeclarationStatementNode declarationNode) {
            res.addAll(generateExpression(declarationNode.getExpression()));

            // Adds the declared variable to the required maps
            variables.put(declarationNode.getIdentifier(), stackSize);
            currentScope().getVariables().add(declarationNode.getIdentifier());
        } else if (node instanceof ExitStatementNode exitStatementNode) {
            res.addAll(generateExpression(exitStatementNode.getExpressionNode()));
            res.add(generatePop(Register.R10));
            res.add(new CallInstruction("exit"));
        } else if (node instanceof IfElseStatementNode ifStatementNode) {
            List<String> scopeList = ifStatementNode.getScopes(ifStatementCount++);
            String endLabel = scopeList.get(scopeList.size() - 1);
            for (int i = 0; i < ifStatementNode.getParts().size(); i++) {
                var fragment = ifStatementNode.getParts().get(i);
                var scope = scopeList.get(i);
                scopes.put(scope, new Scope(scope, stackSize));
                res.add(new LabelInstruction(scope));

                // Generate the expression within the if statement's ()
                if (fragment.getType() != ControlType.ELSE) {
                    res.addAll(generateExpression(fragment.getCondition()));
                    res.add(generatePop(Register.RAX));
                    res.add(new TestInstruction(Register.RAX, Register.RAX));
                    // Jump to next scope if comparison fails
                    res.add(new JzInstruction(scopeList.get(i + 1)));
                }

                // Generates the statements within the current scope
                for (StatementNode statementNode : fragment.getBody()) {
                    res.addAll(generateStatement(statementNode));
                }

                // Jump to the end label
                if (fragment.getType() != ControlType.ELSE) {
                    res.add(new JmpInstruction(endLabel));
                }

                // Clean up out-of-scope variables and return SP to previous location
                for (String variableName : currentScope().getVariables()) {
                    variables.remove(variableName);
                }

                int stackOffset = stackSize - currentScope().getStackOffset();
                stackSize -= stackOffset;
                if (stackOffset != 0)
                    res.add( new AddInstruction(Register.RSP, stackOffset * 8));
                scopes.remove(scopes.lastKey());
            }
            res.add(new LabelInstruction(endLabel));

        } else if (node instanceof AssignmentStatementNode assignmentStatementNode) {
            // If the variable is out of scope it will have been removed from the map already
            if (!variables.containsKey(assignmentStatementNode.getIdentifier()))
                throw new RuntimeException("Variable used before it was declared: " + assignmentStatementNode.getIdentifier());

            // Pop the value from the expression and move it onto the stack at the variable's location
            res.addAll(generateExpression(assignmentStatementNode.getExpression()));
            res.add(generatePop(Register.RAX));
            int variableLoc = variables.get(assignmentStatementNode.getIdentifier());
            int realOffset = (stackSize - variableLoc) * 8;
            res.add(new MovInstruction(new MemoryOperand(Register.RSP, realOffset), new RegisterOperand(Register.RAX)));
        } else if (node instanceof FunctionDeclarationStatementNode functionNode) {
            String scopeName = functionNode.getIdentifier();
            scopes.put(scopeName, new Scope(scopeName, stackSize));
            res.add(new JmpInstruction("end_" + scopeName));
            res.add(new LabelInstruction(scopeName));
            for (var param : functionNode.getParams()) {
                res.addAll(generateStatement(param));
            }

            for (var stmt : functionNode.getBody()) {
                res.addAll(generateStatement(stmt));
            }

            // Clean up out-of-scope variables and return SP to previous location
            for (String variableName : currentScope().getVariables()) {
                variables.remove(variableName);
            }

            int stackOffset = stackSize - currentScope().getStackOffset();
            stackSize -= stackOffset;
            if (stackOffset != 0)
                res.add( new AddInstruction(Register.RSP, stackOffset * 8));
            scopes.remove(scopes.lastKey());
            res.add(new RetInstruction());
            res.add(new LabelInstruction("end_" + scopeName));

        }

        return res;
    }

    /**
     * Generates instructions for an expression and pushes the result onto the stack
     * @param node The expression node
     * @return A list of instructions
     */
    private List<Instruction> generateExpression(ExpressionNode node) {
        List<Instruction> res = new ArrayList<>();

        if (node instanceof ValueExpressionNode valueNode ) {
            res.add(new MovInstruction(Register.RAX, valueNode.getValue()));
            res.add(generatePush(Register.RAX));
        } else if (node instanceof IdentifierExpressionNode identifierNode) {
            if (!variables.containsKey(identifierNode.getIdentifier()))
                throw new RuntimeException("Variable used before it was declared: " + identifierNode.getIdentifier());

            // Gets the variable based on the offset and pushes it onto the stack
            int variableLoc = variables.get(identifierNode.getIdentifier());
            int realOffset = (stackSize - variableLoc) * 8;
            res.add(new MovInstruction(Register.RAX, new MemoryOperand(Register.RSP, realOffset)));
            res.add(generatePush(Register.RAX));
        } else if (node instanceof BinaryExpressionNode binaryNode) {

            // Generate both sides of the expression and pop their values into registers
            res.addAll(generateExpression(binaryNode.getLeftExpression()));
            res.addAll(generateExpression(binaryNode.getRightExpression()));

            res.add(generatePop(Register.RBX));
            res.add(generatePop(Register.RAX));

            // Generate the instruction(s) based on operator type
            TokenType operator = binaryNode.getOperator();
            switch (operator) {
                case MULTIPLY -> res.add(new IMulInstruction(Register.RAX, Register.RBX));
                case DIVIDE -> res.add(new DivInstruction(Register.RAX, Register.RBX));
                case PLUS -> res.add(new AddInstruction(Register.RAX, Register.RBX));
                case MINUS -> res.add(new SubInstruction(Register.RAX, Register.RBX));
                case EQ, GEQ, LEQ, NEQ, LT, GT -> {
                    res.add(new CmpInstruction(Register.RAX, Register.RBX));
                    res.add(switch (operator) {
                        case EQ -> new SeteInstruction(Register.AL);
                        case GEQ -> new SetgeInstruction(Register.AL);
                        case LEQ -> new SetleInstruction(Register.AL);
                        case NEQ ->  new SetneInstruction(Register.AL);
                        case LT -> new SetlInstruction(Register.AL);
                        case GT -> new SetgInstruction(Register.AL);
                        default -> throw new RuntimeException("Unknown operator: " + binaryNode.getOperator().getText());
                    });
                    res.add(new MovzxInstruction(Register.RAX, Register.AL));
                }
                default -> throw new RuntimeException("Unknown operator: " + binaryNode.getOperator().getText());
            }
            res.add(generatePush(Register.RAX));
        } else if (node instanceof FunctionCallExpressionNode functionNode) {
            for (var param : functionNode.getParams()) {
                res.addAll(generateExpression(param));
            }
            res.add(new CallInstruction(functionNode.getIdentifier()));
        }
        return res;
    }

    /**
     * Generates a push instruction and increments the stackSize
     * @param register Register to push into the stack
     * @return the PushInstruction
     */
    private PushInstruction generatePush(Register register) {
        stackSize++;
        return new PushInstruction(new RegisterOperand(register));
    }

    /**
     * Generates a pop instruction and decrements the stackSize
     * @param register Register to pop into from the stack
     * @return the PopInstruction
     */
    private PopInstruction generatePop(Register register) {
        stackSize--;
        return new PopInstruction(new RegisterOperand(register));
    }

    /**
     * Gets the current scope from scopes
     * @return the Current scope
     */
    private Scope currentScope() {
        return scopes.get(scopes.lastKey());
    }


}
