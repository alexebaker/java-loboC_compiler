package Tokenizer.Tokens;

import Compiler.Location;

public class LiteralToken extends Token {
    public LiteralToken(String token, Location loc) {
        super(token, loc);
    }

    /**
     * Checks if ch is a valid Literal Token
     *
     * @param str string to check if it is an Literal Token
     * @return true if ch is an Literal Token, false otherwise
     */
    public static boolean isToken(String str) {
        switch (str) {
            case "(":
            case ")":
            case "[":
            case "]":
            case "{":
            case "}":
            case ".":
            case ",":
            case ";":
            case ":":
            case "::":
            case "!":
            case "?":
            case "=":
            case "==":
            case "!=":
            case "<<":
            case ">>":
            case "<":
            case ">":
            case "<=":
            case ">=":
            case "&":
            case "&&":
            case "|":
            case "||":
            case "^":
            case "*":
            case "%":
            case "/":
            case "+":
            case "-":
            case "++":
            case "--":
                return true;
            default:
                return false;
        }
    }

    /**
     * Checks if token is a valid Literal Token
     *
     * @param token token to check if it is a Literal Token
     * @return true if token is a Literal Token, false otherwise
     */
    public static boolean isToken(Token token) {
        return LiteralToken.isToken(token.getValue());
    }

    public static boolean isDelim(String buf, int nextCh) {
        String nextStr = Character.toString((char) nextCh);
        return Token.isDelim(buf, nextCh) || !(LiteralToken.isToken(buf+nextStr) || Token.isComment(buf+nextStr));
    }
}
