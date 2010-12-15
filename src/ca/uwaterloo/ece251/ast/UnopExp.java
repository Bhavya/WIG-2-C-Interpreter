package ca.uwaterloo.ece251.ast;

import java.util.List;

/** Unary operations. */
public class UnopExp extends Exp {
    public enum Unop {
        NOT("!"), NEG("-");

	private final String t;
	Unop(String t) { this.t = t; }
	public String toString() { return t; }
    };

    public Unop op;
    public Exp e;

    public UnopExp(Unop op, Exp e) {
	this.op = op; this.e = e;
    }

    public String toString() {
	String es = e.toString();
	if (Precedence.p(e).ordinal() > Precedence.p(this).ordinal())
	    es = "(" + es + ")";
	return op.toString() + es;
    }

    public void accept(ASTVisitor v) {
	v.enter(this);
	e.accept(v);
	v.leave(this);
    }
}
