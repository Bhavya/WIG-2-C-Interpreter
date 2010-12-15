package ca.uwaterloo.ece251.ast;

import java.util.List;

/** Represents one WiG service. */
public class Service implements ASTNode {
    List<HTML> htmls;
    List<Schema> schemas;
    List<Variable> variables;
    List<Function> functions;
    List<Session> sessions;

    public Service(List<HTML> htmls,
		   List<Schema> schemas,
		   List<Variable> variables,
		   List<Function> functions,
		   List<Session> sessions) {
	this.htmls = htmls;
	this.schemas = schemas;
	this.variables = variables;
	this.functions = functions;
	this.sessions = sessions;
    }

    public String toString() {
	Util.indent(2);
	String rv = String.format("service {\n%s%s%s%s%s}\n",
				  Util.lines(htmls), 
				  Util.lines(schemas), 
				  Util.lines(variables), 
				  Util.lines(functions), 
				  Util.lines(sessions));
	Util.indent(-2);
	return rv;
    }

    public void accept(ASTVisitor v) {
	v.enter(this);
	for (HTML h : htmls) h.accept(v);
	for (Schema s : schemas) s.accept(v);
	for (Variable var : variables) var.accept(v);
	for (Function f : functions) f.accept(v);
	for (Session s : sessions) s.accept(v);
	v.leave(this);
    }
}

