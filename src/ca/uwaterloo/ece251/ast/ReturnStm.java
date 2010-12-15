package ca.uwaterloo.ece251.ast;

/** Represents a return statement which returns something. */
public class ReturnStm extends Stm {
    public Exp e;

    public ReturnStm(Exp e) { 
	this.e = e;
    }

    public String toString() {
	return "return "+e.toString()+";";
    }

    public void accept(ASTVisitor v) {
	v.enter(this);
	e.accept(v);
	v.leave(this);
    }
}

