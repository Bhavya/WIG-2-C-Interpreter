package ca.uwaterloo.ece251.ast;

import java.util.List;
import java.util.Collections;

/** Literal for tuples. */
public class TupleLiteralExp extends Exp {
    public List<Id> fields;
    public List<Exp> values;

    public TupleLiteralExp(List<Id> fields, List<Exp> values) {
	this.fields = fields != null ? fields : Collections.EMPTY_LIST; 
	this.values = values != null ? values : Collections.EMPTY_LIST; 
    }

    public String toString() {
	StringBuffer sb = new StringBuffer("tuple");
	if (fields != null && values != null) 
	    sb.append(" {"+Util.interleave(fields, values, "=", ", ")+"}");
	else
	    sb.append(" {}");
	return sb.toString();
    }

    public void accept(ASTVisitor v) {
	v.enter(this);
	for (Exp val : values) val.accept(v);
	v.leave(this);
    }
}
