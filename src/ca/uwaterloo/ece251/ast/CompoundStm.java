package ca.uwaterloo.ece251.ast;

import java.util.List;

/** Represents a compound statement. */
public class CompoundStm extends Stm {
    List<Variable> variables;
    List<Stm> body;

    public CompoundStm(List<Variable> variables, List<Stm> body) {
	this.variables = variables;
	this.body = body;
    }

    public String toString() {
	Util.indent(2);
	String rv =
	    "{\n" + Util.lines(variables) + Util.lines(body);
	Util.indent(-2);
	return rv + Util.tab() + "}";
    }

    public void accept(ASTVisitor v) {
	v.enter(this);
	for (Variable var : variables) var.accept(v);
	for (Stm s : body) s.accept(v);
	v.leave(this);
    }
}
