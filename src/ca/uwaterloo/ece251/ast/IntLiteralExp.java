package ca.uwaterloo.ece251.ast;

import java.util.List;

/** Literal for ints. */
public class IntLiteralExp extends Exp {
    int v;

    public IntLiteralExp(int v) { 
	this.v = v; 
    }

    public String toString() {
	return Integer.toString(this.v);
    }

    public void accept(ASTVisitor v) {
	v.enter(this);
	v.leave(this);
    }
}
