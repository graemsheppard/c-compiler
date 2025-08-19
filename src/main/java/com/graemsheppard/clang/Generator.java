package com.graemsheppard.clang;

import com.graemsheppard.clang.enums.ControlType;
import com.graemsheppard.clang.enums.Register;
import com.graemsheppard.clang.enums.TokenType;
import com.graemsheppard.clang.instructions.*;
import com.graemsheppard.clang.instructions.operands.MemoryOperand;
import com.graemsheppard.clang.instructions.operands.Operand;
import com.graemsheppard.clang.instructions.operands.RegisterOperand;
import com.graemsheppard.clang.nodes.*;

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
            "Program exited with code: "
    };

    /**
     * Names of native functions, they should follow have a corresponding file
     * in resources/nativeFunctions
     */
    private static final String[] NATIVE_FUNCTIONS = {
        "exit",
        "itoa",
        "print"
    };

    /**
     * Maps a variable name to its offset from the frame pointer it was declared in
     */
    private HashMap<String, VariableLocation> variables;

    /**
     * Maps a scope name to its scope, should always contain at least 1 scope "_global_"
     */
    private SortedMap<String, Scope> scopes;

    /**
     * The frame pointer offset in bytes
     */
    private int framePointer;

    /**
     * The stack pointer offset in bytes
     */
    private int stackPointer;

    /**
     * The root of the AST
     */
    private final ProgramNode root;

    /**
     * Keeps track of the number of ifStatements for branch naming
     */
    private int ifStatementCount = 0;

    /**
     * Keeps track of the number of whileStatements for branch naming
     */
    private int whileStatementCount = 0;

    public Generator (ProgramNode programRoot) {
        root = programRoot;
    }

    /**
     * Initialized required variables and starts the generation from the root node
     * @return A string representing the compiled program
     */
    public String generate() {
        framePointer = 0;
        stackPointer = framePointer;
        variables = new HashMap<>();
        scopes = new TreeMap<>();
        scopes.put("_global_", new Scope("_global_", framePointer));
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
            res.append("\tstr_").append(i).append(": \tdb '").append(BUILT_IN_STRINGS[i]).append("', \t10\n");
        }
        // Text section for global declarations
        res.append("""
            section .text
                global _main
            _main:
                mov     rbp,    rsp
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

        // Push 0 because the program proceeds into exit section, subtract 8 because RIP is not pushed
        res.append(new PushInstruction(0));
        res.append(new SubInstruction(Register.RSP, 8));

        // Built in subroutines
        Arrays.stream(NATIVE_FUNCTIONS)
                .map(NativeFunction::new)
                .map(NativeFunction::toString)
                .forEach(res::append);
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
            var locator = new VariableLocation(declarationNode.getIdentifier(), stackPointer - framePointer, currentScope());
            variables.put(declarationNode.getIdentifier(), locator);
            currentScope().getVariables().add(declarationNode.getIdentifier());
        } else if (node instanceof ArrayDeclarationStatementNode arrayNode) {
            // Allocate stack space assume 8 byte type for now
            int spaceReq = 8 * arrayNode.getSize();
            int loc = stackPointer - framePointer - 8;
            stackPointer -= spaceReq;
            res.add(new SubInstruction(Register.RSP, spaceReq));
            var locator = new VariableLocation(arrayNode.getIdentifier(), loc, currentScope());
            variables.put(arrayNode.getIdentifier(), locator);
            currentScope().getVariables().add(arrayNode.getIdentifier());
        } else if (node instanceof ExitStatementNode exitStatementNode) {
            res.addAll(generateExpression(exitStatementNode.getExpressionNode()));
            res.add(new CallInstruction("exit"));
        } else if (node instanceof IfElseStatementNode ifStatementNode) {
            List<String> scopeList = ifStatementNode.getScopes(ifStatementCount++);
            String endLabel = scopeList.get(scopeList.size() - 1);
            for (int i = 0; i < ifStatementNode.getParts().size(); i++) {
                var fragment = ifStatementNode.getParts().get(i);
                var scope = scopeList.get(i);
                res.add(new LabelInstruction(scope));
                // Generate the expression within the if statement's ()
                if (fragment.getType() != ControlType.ELSE) {
                    res.addAll(generateExpression(fragment.getCondition()));
                    res.add(generatePop(Register.RAX));
                    res.add(new TestInstruction(Register.RAX, Register.RAX));
                    // Jump to next scope if comparison fails
                    res.add(new JzInstruction(scopeList.get(i + 1)));
                }

                // Store the location of the stack pointer
                stackPointer -= 8;
                int stackLoc = stackPointer - framePointer;
                res.add(new SubInstruction(Register.RSP, 8));
                res.add(new MovInstruction(new MemoryOperand(Register.RSP), new RegisterOperand(Register.RSP)));
                scopes.put(scope, new Scope(scope, framePointer));

                // Generates the statements within the current scope
                for (StatementNode statementNode : fragment.getBody()) {
                    res.addAll(generateStatement(statementNode));
                }

                // Return stack to where it was at the start of the scope
                res.add(new MovInstruction(Register.RSP, new MemoryOperand(Register.RBP, stackLoc)));
                res.add(generatePop(Register.RSP));
                stackPointer += 8;
                res.add(new AddInstruction(Register.RSP, 8));

                // Jump to the end label
                if (fragment.getType() != ControlType.ELSE) {
                    res.add(new JmpInstruction(endLabel));
                }

                // Clean up out-of-scope variables and return SP to previous location
                for (String variableName : currentScope().getVariables()) {
                    variables.remove(variableName);
                }

                scopes.remove(scopes.lastKey());
            }
            res.add(new LabelInstruction(endLabel));

        } else if (node instanceof WhileStatementNode whileNode) {
            String scopeName = "while_" + whileStatementCount++;
            String endLabel = "end_" + scopeName;

            // Store the location of the stack pointer
            stackPointer -= 8;
            int stackLoc = stackPointer - framePointer;
            res.add(new SubInstruction(Register.RSP, 8));
            res.add(new MovInstruction(new MemoryOperand(Register.RSP), new RegisterOperand(Register.RSP)));
            res.add(new LabelInstruction(scopeName));
            scopes.put(scopeName, new Scope(scopeName, framePointer));
            // Evaluate condition and conditional jump to end
            res.addAll(generateExpression(whileNode.getCondition()));
            res.add(generatePop(Register.RAX));
            res.add(new TestInstruction(Register.RAX, Register.RAX));
            res.add(new JzInstruction(endLabel));

            // Generate the body
            for (var stmt : whileNode.getBody()) {
                res.addAll(generateStatement(stmt));
            }
            // Return stack to where it was at the beginning of the loop
            res.add(new MovInstruction(Register.RSP, new MemoryOperand(Register.RBP, stackLoc)));
            res.add(new JmpInstruction(scopeName));

            res.add(generatePop(Register.RSP));
            for (var variable : currentScope().getVariables()) {
                variables.remove(variable);
            }
            scopes.remove(scopeName);
            // End label to jump to
            res.add(new LabelInstruction(endLabel));
            stackPointer += 8;
            res.add(new AddInstruction(Register.RSP, 8));
        } else if (node instanceof AssignmentStatementNode assignmentStatementNode) {
            // If the variable is out of scope it will have been removed from the map already
            if (!variables.containsKey(assignmentStatementNode.getIdentifier()))
                throw new RuntimeException("Variable used before it was declared: " + assignmentStatementNode.getIdentifier());

            if (assignmentStatementNode.getOffset() != null) {
                res.addAll(generateExpression(assignmentStatementNode.getExpression()));
                res.addAll(generateExpression(assignmentStatementNode.getOffset()));
                res.add(new MovInstruction(Register.RBX, 8));
                res.add(new IMulInstruction(Register.RAX, Register.RBX));
                res.add(generatePush(Register.RAX));
                int variableLoc = variables.get(assignmentStatementNode.getIdentifier()).getLocationRelativeTo(framePointer);
                res.add(new LeaInstruction(new RegisterOperand(Register.RAX), new MemoryOperand(Register.RBP, variableLoc)));
                res.add(generatePop(Register.RBX));
                res.add(new SubInstruction(Register.RAX, Register.RBX));
                res.add(generatePop(Register.RBX));
                res.add(new MovInstruction(new MemoryOperand(Register.RAX), new RegisterOperand(Register.RBX)));
            } else {
                // Pop the value from the expression and move it onto the stack at the variable's location
                res.addAll(generateExpression(assignmentStatementNode.getExpression()));
                res.add(generatePop(Register.RAX));
                int variableLoc = variables.get(assignmentStatementNode.getIdentifier()).getLocationRelativeTo(framePointer);
                res.add(new MovInstruction(new MemoryOperand(Register.RBP, variableLoc), new RegisterOperand(Register.RAX)));
            }
        } else if(node instanceof ReturnStatementNode returnNode) {
            res.addAll(generateExpression(returnNode.getExpression()));
            res.add(generatePop(Register.RAX));
        } else if (node instanceof FunctionDeclarationStatementNode functionNode) {
            String scopeName = functionNode.getIdentifier();
            res.add(new JmpInstruction("end_" + scopeName));
            res.add(new LabelInstruction(scopeName));
            res.addAll(createStackFrame(scopeName, functionNode.getParams().size() + 1));

            // Allocate space for params and increment stack size, assuming caller will provide the correct args
            // The extra 8 bytes is to account for the return address which is pushed on call
            int argOffset = 8 * functionNode.getParams().size() + 8;
            for (var param : functionNode.getParams()) {
                currentScope().getVariables().add(param.getIdentifier());
                var varLoc = new VariableLocation(param.getIdentifier(), argOffset, currentScope());
                variables.put(param.getIdentifier(), varLoc);
                argOffset -= 8;
            }

            for (var stmt : functionNode.getBody()) {
                res.addAll(generateStatement(stmt));
            }

            res.addAll(destroyStackFrame(functionNode.getParams().size() + 1));
            res.add(new RetInstruction());
            res.add(new LabelInstruction("end_" + scopeName));

        } else {
            res.addAll(generateExpression((ExpressionNode)node));
            // Since all expressions push to the stack and this expression result is not used pop
            res.add(generatePop(Register.RAX));
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
        } else if (node instanceof DereferenceExpressionNode derefNode) {
            res.addAll(generateExpression(derefNode.getExpression()));
            res.add(generatePop(Register.RAX));
            res.add(new MovInstruction(Register.RAX, new MemoryOperand(Register.RAX)));
            res.add(generatePush(Register.RAX));
        } else if (node instanceof IdentifierExpressionNode identifierNode) {
            if (!variables.containsKey(identifierNode.getIdentifier()))
                throw new RuntimeException("Variable used before it was declared: " + identifierNode.getIdentifier());

            // Gets the variable based on the offset and pushes it onto the stack
            int variableLoc = variables.get(identifierNode.getIdentifier()).getLocationRelativeTo(framePointer);
            res.add(new MovInstruction(Register.RAX, new MemoryOperand(Register.RBP, variableLoc)));
            res.add(generatePush(Register.RAX));
        } else if (node instanceof ArrayReferenceExpressionNode arrayNode) {
            if (!variables.containsKey(arrayNode.getIdentifier()))
                throw new RuntimeException("Variable used before it was declared: " + arrayNode.getIdentifier());
            res.addAll(generateExpression(arrayNode.getExpression()));
            res.add(generatePop(Register.RAX));
            res.add(new MovInstruction(Register.RBX, 8));
            res.add(new IMulInstruction(Register.RAX, Register.RBX));

            // Gets the variable based on the offset
            int variableLoc = variables.get(arrayNode.getIdentifier()).getLocationRelativeTo(framePointer);
            res.add(new LeaInstruction(Register.RBX, new MemoryOperand(Register.RBP, variableLoc)));
            res.add(new SubInstruction(Register.RBX, Register.RAX));

            res.add(generatePushq(new MemoryOperand(Register.RBX, 0)));
        } else if (node instanceof AddressExpressionNode addressNode) {
            var identifierNode = addressNode.getIdentifier();
            if (!variables.containsKey(identifierNode.getIdentifier()))
                throw new RuntimeException("Variable used before it was declared: " + identifierNode.getIdentifier());
            // Gets the address based on the offset and pushes it onto the stack
            int variableLoc = variables.get(identifierNode.getIdentifier()).getLocationRelativeTo(framePointer);
            res.add(new LeaInstruction(Register.RAX, new MemoryOperand(Register.RBP, variableLoc)));
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
                case STAR -> res.add(new IMulInstruction(Register.RAX, Register.RBX));
                case FSLASH -> res.add(new DivInstruction(Register.RAX, Register.RBX));
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

            int numParams = functionNode.getParams().size();
            res.add(new CallInstruction(functionNode.getIdentifier()));
            res.add(new AddInstruction(Register.RSP, numParams * 8));
            stackPointer += numParams * 8;
            res.add(generatePush(Register.RAX));
        }
        return res;
    }

    /**
     * Generates a push instruction and increments the stackSize
     * @param register Register to push into the stack
     * @return the PushInstruction
     */
    private PushInstruction generatePush(Register register) {
        return generatePush(new RegisterOperand(register));
    }

    private PushInstruction generatePush(Operand operand) {
        stackPointer -= 8;
        return new PushInstruction(operand);
    }

    private PushqInstruction generatePushq(Register register) {
        return generatePushq(new RegisterOperand(register));
    }

    private PushqInstruction generatePushq(Operand operand) {
        stackPointer -= 8;
        return new PushqInstruction(operand);
    }

    /**
     * Generates a pop instruction and decrements the stackSize
     * @param register Register to pop into from the stack
     * @return the PopInstruction
     */
    private PopInstruction generatePop(Register register) {
        stackPointer += 8;
        return new PopInstruction(new RegisterOperand(register));
    }

    /**
     * Creates a stack frame and adds the scope, handles all offsetting of the compiler's framePointer and stackPointer
     * @param scopeName Name of the scope, a unique identifier
     * @param numParams Number of params a function takes, for functions has a minimum of 1 to account for RIP
     * @return The instructions that create the actual stack frame
     */
    private List<Instruction> createStackFrame(String scopeName, int numParams) {
        List<Instruction> res = new ArrayList<>(2);
        res.add(generatePush(Register.RBP));
        res.add(new MovInstruction(Register.RBP, Register.RSP));
        stackPointer -= 8 * numParams;
        framePointer = stackPointer;
        scopes.put(scopeName, new Scope(scopeName, framePointer));

        return res;
    }

    /**
     * Tears down a stack frame
     * @param numParams Number of params passed to the stack frame, for functions has a minimum of 1 (RIP)
     * @return The instructions that create the actual stack frame
     */
    private List<Instruction> destroyStackFrame(int numParams) {
        List<Instruction> res = new ArrayList<>(2);
        stackPointer = framePointer;
        stackPointer += 8 * numParams;

        res.add(new MovInstruction(Register.RSP, Register.RBP));
        res.add(generatePop(Register.RBP));

        for (String variable : currentScope().getVariables())
            variables.remove(variable);
        scopes.remove(scopes.lastKey());
        framePointer = currentScope().getFramePointer();

        return res;
    }


    /**
     * Gets the current scope from scopes
     * @return the Current scope
     */
    private Scope currentScope() {
        return scopes.get(scopes.lastKey());
    }

    private Scope containingScope() {
        var keys = scopes.keySet().stream().toList();
        int numKeys = keys.size();
        return scopes.get(keys.size() > 1 ? keys.get(numKeys - 2) : keys.get(0));
    }


}
