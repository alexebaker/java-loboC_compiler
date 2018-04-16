import Compiler.LCC;
import Compiler.CompilerState;

/**
 * Main class called to compile the input
 */
public class Main {
    public static void main(String[] argv) {
        if (argv.length > 2) {
            System.err.println("Too many arguments, can only give 0 or 1 argument.");
            System.exit(1);
        }

        System.setProperty("line.separator", "\n");
        CompilerState cs;

        if (argv.length == 1) {
            cs = new CompilerState(argv[0]);
        }
        else if (argv.length == 2) {
            cs = new CompilerState(argv[0], argv[1]);
        }
        else {
            cs = new CompilerState();
        }

        LCC lcc = new LCC(cs);
        lcc.compile();

        //cs.printBOTLPIF();

        if (cs.getErrors().size() == 0) {
            cs.writeAsm();
        }
        else if (cs.getErrors().size() > 9) {
            System.exit(10);
        }

        cs.printErrors();
        cs.getIO().close();
        System.exit(cs.getErrors().size());
    }
}
