package ca.uwaterloo.ece251.ast;

import java.util.List;
import java.util.Collections;

/** Represents a show statement. */
public class ShowStm extends Stm {
    public Document d;
    public List<Input> receives;

    public ShowStm(Document d, List<Input> receives) {
	this.d = d; 
	this.receives = (receives != null) ? receives : Collections.EMPTY_LIST;
    }

    public String toString() {
	return String.format("show %s%s;",
			     d.toString(), 
			     receives == Collections.EMPTY_LIST ? "" : 
			     " receive ["+Util.commaSeparated(receives)+"]");
    }

    public void accept(ASTVisitor v) {
	v.enter(this);
	d.accept(v);
	for (Input i : receives) i.accept(v);
	v.leave(this);
    }
}

