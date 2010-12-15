package ca.uwaterloo.ece251.ast;

import java.util.List;
import java.util.Collections;

/** Represents a WiG function. */
public class Function implements ASTNode {
    public Type returnType;
    public Id id;
    public List<Type> argTypes;
    public List<Id> argIds;
    public Stm body;

    public Function(Type returnType, Id id, List<Type> argTypes,
		    List<Id> argIds, Stm body) {
	this.returnType = returnType;
	this.id = id;
	this.argTypes = argTypes != null ? argTypes : Collections.EMPTY_LIST;
	this.argIds = argIds != null ? argIds : Collections.EMPTY_LIST;
	this.body = body;
    }

    public String toString() {
	return String.format("%s %s (%s) %s",
			     returnType.toString(), id.toString(), 
			     argTypes != null ? 
			     Util.interleave(argTypes, argIds, " ", ", ") : "",
			     body.toString());
    }

    public void accept(ASTVisitor v) {
	v.enter(this);
	body.accept(v);
	returnType.accept(v);
	for (Type t : argTypes) t.accept(v);
	v.leave(this);
    }
}
