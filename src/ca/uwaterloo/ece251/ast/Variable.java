package ca.uwaterloo.ece251.ast;

import java.util.List;

/** Represents a variable (declaration). */
public class Variable {
    public Type t;
    public List<Id> identifiers;

    public Variable(Type t, List<Id> identifiers) {
	this.t = t; this.identifiers = identifiers;
    }

    public String toString() {
	return String.format("%s %s;", t.toString(),
			     Util.commaSeparated(identifiers));
    }

    public void accept(ASTVisitor v) {
	v.enter(this);
	t.accept(v);
	v.leave(this);
    }
}
