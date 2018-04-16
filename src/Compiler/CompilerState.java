package Compiler;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;
import Errors.Error;
import Parser.Nodes.ASTNode;
import Tokenizer.TokenReader;

/**
 * Contains the current state of the compiler at any given time
 */
public class CompilerState {
    private String inputPath;
    private String outputPath;
    private CompilerIO io;
    private TokenReader tr;
    private Vector<Error> errors;
    private ASTNode ast;


    public CompilerState() {
        this.inputPath = "<stdin>";
        this.outputPath = "<stdout>";
        this.io = new CompilerIO();
        this.tr = new TokenReader(this);
        this.errors = new Vector<>();
        this.ast = null;
    }

    public CompilerState(String inFile) {
        this();
        this.inputPath = inFile;

        try {
            this.io = new CompilerIO(new FileReader(inFile), System.out);
        }
        catch (IOException ex) {
            ex.printStackTrace();
            this.io.close();
            System.exit(100);
        }
    }

    public CompilerState(String inFile, String outFile) {
        this();
        this.inputPath = inFile;
        this.outputPath = outFile;

        try {
            this.io = new CompilerIO(new FileReader(inFile), new FileWriter(outFile));
        }
        catch (IOException ex) {
            ex.printStackTrace();
            this.io.close();
            System.exit(100);
        }
    }

    public Vector<Error> getErrors() {
        return errors;
    }

    public void addError(Error error) {
        errors.add(error);
    }

    public void setAST(ASTNode ast) {
        this.ast = ast.foldConstants();
    }

    public void printBOTLPIF() {
        if (ast != null) {
            io.write(ast.getBOTLPIF(this));
        }
    }

    public void printErrors() {
        for (Error error : errors) {
            System.err.println(error);
        }
    }

    public TokenReader getTr() {
        return tr;
    }

    /**
     * Gets the Compiler.CompilerIO object for reading and writing
     *
     * @return the Compiler IO object
     */
    public CompilerIO getIO() {
        return this.io;
    }

    /**
     * Path the the file or input being read from
     *
     * @return Path to input stream
     */
    public String getInputPath() {
        return this.inputPath;
    }

    public void writeAsm() {
        io.write(ast.getAsm(null, null, FallThrough.FALL_NEITHER));
    }
}
