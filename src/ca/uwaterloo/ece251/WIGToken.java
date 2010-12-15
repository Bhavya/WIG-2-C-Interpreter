package ca.uwaterloo.ece251;
import org.antlr.runtime.*;
import java.io.*;

/** Main entry point. */
public class WIGToken {
    public static void main(String[] args) throws Exception {
        ANTLRFileStream input = new ANTLRFileStream(args[0]);
        WigLexer lexer = new WigLexer(input);
	Token token;
	while ((token = lexer.nextToken()) != Token.EOF_TOKEN) {
	    System.out.println("Token: "+token.getText()+"("+token.getType()+")");
	}
    }
}
