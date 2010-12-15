package ca.uwaterloo.ece251.ast;

/** Class to compute precedence. */
public class Precedence {
    public enum P { // high to low
	BASE,
	UNARY,
	TPLUSMINUS,
	TLEFTSHIFT,
	TIMES,
	PLUS,
	COMPARISON,
	AND,
	OR,
	ASSIGN
    };

    /* This should be a Visitor pattern. */
    public static P p(Exp e) {
	if (e instanceof AssignExp)
	    return P.ASSIGN;

	if (e instanceof BinopExp) {
	    switch (((BinopExp)e).op) {
	    case OR:
		return P.OR;
	    case AND:
		return P.AND;
	    case EQ: case NEQ:
	    case LT: case GT:
	    case LE: case GE:
		return P.COMPARISON;
	    case PLUS: case MINUS:
		return P.PLUS;
	    case TIMES: case DIV: 
	    case MOD: 
		return P.TIMES;
	    case TUPLE_FROM:
		return P.TLEFTSHIFT;
	    }
	}

	if (e instanceof TupleopExp)
	    return P.TPLUSMINUS;

	if (e instanceof UnopExp)
	    return P.UNARY;

	if (e instanceof BoolLiteralExp || e instanceof IntLiteralExp || e instanceof StringLiteralExp || e instanceof TupleLiteralExp || e instanceof CallExp || e instanceof Lvalue)
	    return P.BASE;
	return null;
    }
}
