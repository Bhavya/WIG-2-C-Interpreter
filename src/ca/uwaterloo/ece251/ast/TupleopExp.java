package ca.uwaterloo.ece251.ast;

import java.util.List;

/** Tuple binary operations. */
public class TupleopExp extends Exp {
    public enum Tupleop {
	TUPLE_PLUS("\\+"), TUPLE_MINUS("\\-");

	private final String t;
	Tupleop(String t) { this.t = t; }
	public String toString() { return t; }
    };

    public Tupleop op;
    public Exp left;
    public List<Id> right;

    public TupleopExp(Tupleop op, Exp left, List<Id> right) {
	this.op = op; this.left = left; this.right = right;
    }

    public String toString() {
	String lop = left.toString();
	if (Precedence.p(left).ordinal() > Precedence.p(this).ordinal())
	    lop = "(" + left + ")";
	return lop + " " + op.toString() + " (" + Util.commaSeparated(right) + ")";
    }

    public void accept(ASTVisitor v) {
	v.enter(this);
	left.accept(v);
	v.leave(this);
    }
}
