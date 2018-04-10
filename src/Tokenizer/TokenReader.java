package Tokenizer;

import java.nio.BufferOverflowException;
import java.nio.CharBuffer;

import Compiler.CompilerState;
import Compiler.Location;
import Tokenizer.Tokens.*;

public class TokenReader {
    private CompilerState cs;
    private boolean hasBufferedToken;
    private Token bufferedToken;
    private int bufferCapacity = 1024;

    public TokenReader(CompilerState cs) {
        this.cs = cs;
        this.bufferedToken = null;
        this.hasBufferedToken = false;
    }

    public Token read() {
        if (hasBufferedToken) {
            hasBufferedToken = false;
            return bufferedToken;
        }
        return getNextToken();
    }

    public Token peek() {
        if (!hasBufferedToken) {
            bufferedToken = getNextToken();
            hasBufferedToken = true;
        }
        return bufferedToken;
    }

    /**
     * Gets the next token from the input based on what is read from the compiler state
     * and returns the token.
     */
    private Token getNextToken() {
        int ch;
        boolean inComment = false;
        CharBuffer charBuffer = CharBuffer.allocate(bufferCapacity);
        Location loc = null;

        while (true) {
            ch = cs.getIO().read();

            // First, determine special cases that to not need to be tokenized
            if (EOFToken.isToken(ch)) {
                return new EOFToken(new Location(cs));
            }
            else if (ch == '\n') {
                inComment = false;
                continue;
            }
            else if (Character.isWhitespace(ch) || inComment) {
                continue;
            }
            else if (loc == null) {
                loc = new Location(cs);
            }

            try {
                charBuffer.append((char) ch);
            }
            catch (BufferOverflowException ex) {
                System.err.println("Token exceeded " + bufferCapacity + " bytes.");
                ex.printStackTrace();
                cs.getIO().close();
                System.exit(100);
            }

            String buf = new String(charBuffer.array()).trim();
            int nextCh = cs.getIO().peek();

            // Determine if the token in valid. The inside if statements determines
            // if the token needs to be returned
            if (Token.isComment(buf)) {
                inComment = true;
                charBuffer = CharBuffer.allocate(bufferCapacity);
                loc = null;
            }
            else if (LiteralToken.isToken(buf)) {
                // The last check for a number token handles the case when a '.' is a literal token and not part of a number
                if (LiteralToken.isDelim(buf, nextCh)) {
                    return new LiteralToken(buf, loc);
                }
            }
            else if (KeywordToken.isToken(buf)) {
                if (KeywordToken.isDelim(buf, nextCh)) {
                    return new KeywordToken(buf, loc);
                }
            }
            else if (IdentifierToken.isToken(buf)) {
                if (IdentifierToken.isDelim(buf, nextCh)) {
                    return new IdentifierToken(buf, loc);
                }
            }
            else if (NumberToken.isToken(buf)) {
                if (NumberToken.isDelim(buf, nextCh)) {
                    return new NumberToken(buf, loc);
                }
            }
            else {
                return new IllChrToken(buf, loc);
            }
        }
    }

    public Token recoverFromError() {
        while (true) {
            Token token = peek();
            if (EOFToken.isToken(token)) {
                return token;
            }
            else if (token.getValue().equals(";") || token.getValue().equals("}")) {
                return read();
            }
            read();
        }
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

        TokenReader tr = new TokenReader(cs);
        while (!EOFToken.isToken(tr.peek())) {
            cs.getIO().write(tr.read().getInfoStr());
        }
        cs.getIO().write(tr.read().getInfoStr());

        cs.getIO().close();
        System.exit(0);
    }
}
