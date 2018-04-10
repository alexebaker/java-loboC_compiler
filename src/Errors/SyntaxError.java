package Errors;

import Tokenizer.Tokens.Token;

public class SyntaxError extends Error {
    private String expected;

    public SyntaxError(Token token, String expected) {
        super(token);
        this.expected = expected;
    }

    public String getErrorMsg() {
        StringBuilder str = new StringBuilder("");
        str.append("Syntax Error! ");
        str.append("Line: ");
        str.append(getToken().getLoc().getLineCount());
        str.append(" Character: ");
        str.append(getToken().getLoc().getCharCount());
        str.append(" Reason: Found '");
        str.append(getToken());

        if (expected.length() > 0) {
            str.append("' but expected '");
            str.append(expected);
            str.append("'");
        }
        return str.toString();
    }
}
