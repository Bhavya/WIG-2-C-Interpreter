package ca.uwaterloo.ece251.ast;

public class SimpleType extends Type {
    // It occurs to me that this should actually be a Singleton. Oops.
    public enum SimpleTypes { 
	INT("int"), BOOL("bool"), STRING("string"), VOID("void");
	private final String n;
	SimpleTypes(String n) { this.n = n; }
	public String toString() { return n; }
    };
    public SimpleTypes t;

    public SimpleType(SimpleTypes t) {
	this.t = t;
    }

    public String toString() {
	return t.toString();
    }

    public void accept(ASTVisitor v) {
	v.enter(this);
	v.leave(this);
    }

    public boolean equals(Object o) {
	return (o instanceof SimpleType) &&
	    t == ((SimpleType)o).t;
    }
}
