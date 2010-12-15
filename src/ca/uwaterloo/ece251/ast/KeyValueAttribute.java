package ca.uwaterloo.ece251.ast;

/** Represents a key/value attribute. */
public class KeyValueAttribute extends Attribute implements ASTNode {
    Attr key; Attr value;

    public KeyValueAttribute(Attr key, Attr value) { 
	this.key = key; this.value = value;
    }
    public String toString() { 
	return key.toString() + "=\"" + value.toString()+"\""; 
    }

    public void accept(ASTVisitor v) {
	v.enter(this);
	key.accept(v); value.accept(v);
	v.leave(this);
    }
}
