package ca.uwaterloo.ece251.ast;

import java.util.List;

/** Binary operations. */
public class BinopExp extends Exp {
    public enum Binop {
	EQ("=="), NEQ("!="), LT("<"), GT(">"), LE("<="), GE(">="),
	OR("||"), AND("&&"), 
	TIMES("*"), DIV("/"), MOD("%"),
	TUPLE_FROM("<<"), 
	PLUS("+"), MINUS("-");

	private final String t;
	Binop(String t) { this.t = t; }
	public String toString() { return t; }
    };

    public Binop op;
    public Exp left, right;

    public BinopExp(Binop op, Exp left, Exp right) {
	this.op = op; this.left = left; this.right = right;
    }

    public String toString() {
	String lop = left.toString(), rop = right.toString();
	if (Precedence.p(left).ordinal() > Precedence.p(this).ordinal())
	    lop = "(" + left + ")";
	if (Precedence.p(right).ordinal() > Precedence.p(this).ordinal())
	    rop = "(" + right + ")";
	return lop + " " + op.toString() + " " + rop;
    }

    public void accept(ASTVisitor v) {
	v.enter(this);
	left.accept(v); right.accept(v);
	v.leave(this);
    }
}
