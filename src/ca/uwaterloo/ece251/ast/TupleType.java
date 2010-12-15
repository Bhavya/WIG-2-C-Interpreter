package ca.uwaterloo.ece251.ast;
import ca.uwaterloo.ece251.symbol.SymbolTable;

public class TupleType extends Type {
    public Id id;

    public TupleType(Id id) { this.id = id; }
    public String toString() { return "tuple "+id.toString(); }

    public void accept(ASTVisitor v) {
	v.enter(this);
	v.leave(this);
    }

    public boolean equals(Object o) {
	if (!(o instanceof TupleType)) 
	    return false;

	TupleType tt = (TupleType) o;
	if (tt.id.equals(id))
	    return true;

	Schema s1 = SymbolTable.v().findSchema(id);
	Schema s2 = SymbolTable.v().findSchema(tt.id);
	return s1.equals(s2);
    }

    public int hashCode() {
	return id.hashCode();
    }
}
