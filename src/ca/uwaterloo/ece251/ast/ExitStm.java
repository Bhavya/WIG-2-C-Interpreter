package ca.uwaterloo.ece251.ast;

/** Represents an exit statement, which displays a page and exits. */
public class ExitStm extends Stm {
    Document d;

    public ExitStm(Document d) {
	this.d = d;
    }

    public String toString() {
	return "exit "+d.toString()+";";
    }

    public void accept(ASTVisitor v) {
	v.enter(this);
	d.accept(v);
	v.leave(this);
    }
}
