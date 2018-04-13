package Parser.Nodes;

import Errors.Error;
import Errors.SyntaxError;
import Tokenizer.TokenReader;
import Compiler.*;
import Tokenizer.Tokens.EOFToken;
import Types.Type;


public class Program extends ASTNode {
    private ASTNode block;

    public Program() {
        block = null;
    }

    public void setBlock(ASTNode block) {
        this.block = block;
    }

    public ASTNode getBlock() {
        return block;
    }

    @Override
    public String getVSR(int indentDepth, CompilerState cs) {
        StringBuilder str = new StringBuilder();
        if (block != null) {
            str.append(block.getVSR(0, cs));
        }
        return str.toString();
    }

    @Override
    public String getASTR(int indentDepth, CompilerState cs) {
        StringBuilder str = new StringBuilder();
        if (block != null) {
            str.append(block.getASTR(0, cs));
        }
        return str.toString();
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        if (block != null) {
            str.append(block);
            str.append("\n");
        }
        return str.toString();
    }

    /**
     * Function to be called to parse a program. This will parse multiple statements.
     *
     * @return true if the parse was successful, false otherwise
     */
    public static ASTNode parse(CompilerState cs, SymbolTable st) {
        TokenReader tr = cs.getTr();
        Program program = new Program();
        program.setBlock(Block.parse(cs, st));

        if (EOFToken.isToken(tr.peek())) {
            tr.read();
        }
        else {
            cs.addError(new SyntaxError(tr.read(), "EOF"));
        }
        return program;
    }

    public Type getNodeType(CompilerState cs) {
        if (getType() == null && block != null) {
            setType(block.getNodeType(cs));
        }
        return getType();
    }

    public ASTNode foldConstants() {
        if (block != null) {
            block = block.foldConstants();
        }
        return this;
    }

    public Object getValue() {
        return null;
    }

    public Location getLocation() {
        return null;
    }

    public boolean isAssignable() {
        return false;
    }
}
