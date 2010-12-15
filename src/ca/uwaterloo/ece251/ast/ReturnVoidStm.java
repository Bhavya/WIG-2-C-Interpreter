package ca.uwaterloo.ece251.ast;

/** Represents a return-void statement. */
public class ReturnVoidStm extends Stm {
    public ReturnVoidStm() { }
    public String toString() { return "return;"; }

    public void accept(ASTVisitor v) {
	v.enter(this);
	v.leave(this);
    }
}

