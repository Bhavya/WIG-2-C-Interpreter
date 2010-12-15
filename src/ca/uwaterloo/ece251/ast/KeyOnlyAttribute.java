package ca.uwaterloo.ece251.ast;

/** Represents either a key-only attribute or a key/value attribute. */
public class KeyOnlyAttribute extends Attribute {
    Attr attr;

    public KeyOnlyAttribute(Attr attr) { this.attr = attr; }
    public String toString() { return attr.toString(); }

    public void accept(ASTVisitor v) {
	v.enter(this);
	attr.accept(v);
	v.leave(this);
    }
}
