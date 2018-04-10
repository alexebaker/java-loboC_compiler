package Parser;

import Compiler.CompilerState;
import Parser.Nodes.ASTNode;
import Parser.Nodes.Program;
import Tokenizer.*;

public class TokenParser {
    CompilerState cs;

    public TokenParser(CompilerState cs) {
        this.cs = cs;
    }

    public ASTNode parse() {
        return Program.parse(cs, null);
    }

    public static void main(String[] argv) {
        if (argv.length > 1) {
            System.err.println("Too many arguments, can only give 0 or 1 argument.");
            System.exit(1);
        }

        System.setProperty("line.separator", "\n");
        CompilerState cs = new CompilerState();

        if (argv.length == 1) {
            cs = new CompilerState(argv[0]);
        }

        cs.getIO().close();
        System.exit(0);
    }
}
