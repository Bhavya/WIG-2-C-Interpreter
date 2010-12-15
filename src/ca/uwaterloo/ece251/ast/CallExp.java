package ca.uwaterloo.ece251.ast;

import java.util.List;
import java.util.Collections;

/** Function call. */
public class CallExp extends Exp {
    public Id fname;
    public List<Exp> args;

    public CallExp(Id fname, List<Exp> args) {
	this.fname = fname; 
	this.args = (args != null) ? args : Collections.EMPTY_LIST;
    }

    public String toString() {
	return fname.toString()+"("+
	    (args == null ? "" : Util.commaSeparated(args))
	    +")";
    }

    public void accept(ASTVisitor v) {
	v.enter(this);
	for (Exp arg : args) arg.accept(v);
	v.leave(this);
    }
}
