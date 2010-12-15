package ca.uwaterloo.ece251.ast;

import java.util.List;

/** Represents a WiG session. */
public class Session implements ASTNode {
    public Id id;
    public Stm body;

    public Session(Id id, Stm body) {
	this.id = id;
	this.body = body;
    }

    public String toString() {
	return String.format("session %s () %s",
			     id.toString(), body.toString());
    }

    public void accept(ASTVisitor v) {
	v.enter(this);
	body.accept(v);
	v.leave(this);
    }
}
