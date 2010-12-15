package ca.uwaterloo.ece251.ast;

import java.util.List;
import java.util.HashSet;

/** Represents a schema. */
public class Schema implements ASTNode {
    public Id id;
    public List<WigField> fields;

    public Schema(Id id, List<WigField> fields) {
	this.id = id; this.fields = fields;
    }

    public String toString() {
	return String.format("schema %s { %s }",
			     id.toString(),
			     Util.join(fields, " "));
    }

    public void accept(ASTVisitor v) {
	v.enter(this);
	for (WigField f : fields) f.accept(v);
	v.leave(this);
    }

    public boolean equals(Object o) {
	if (!(o instanceof Schema)) return false;

	Schema ss = (Schema)o;

	if (id.equals(ss.id) || fields.equals(ss.fields))
	    return true;

	HashSet<WigField> fs = new HashSet<WigField>(), 
	    oss = new HashSet<WigField>();
	fs.addAll(fields); oss.addAll(ss.fields);
	return fs.equals(oss);
    }
}

