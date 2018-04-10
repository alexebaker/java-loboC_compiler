package Errors;


import Tokenizer.Tokens.Token;
import Compiler.Location;

public class TypeError extends Error {
    private Location loc;
    private String msg;

    public TypeError() {
        this("", null);
    }

    public TypeError(String msg, Location loc) {
        this.msg = msg;
        this.loc = loc;
    }

    @Override
    public String getErrorMsg() {
        StringBuilder str = new StringBuilder("");
        str.append("Type Error! ");
        if (loc != null) {
            str.append("Line: ");
            str.append(loc.getLineCount());
            str.append(" Character: ");
            str.append(loc.getCharCount());
        }
        str.append(" Reason: ");
        str.append(msg);
        return str.toString();
    }
}
