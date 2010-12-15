package ca.uwaterloo.ece251.type;

import ca.uwaterloo.ece251.ast.*;
import ca.uwaterloo.ece251.ast.BinopExp.Binop;
import ca.uwaterloo.ece251.symbol.SymbolTable;
import ca.uwaterloo.ece251.Error;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.Stack;
import java.util.List;
import java.util.LinkedList;

/** This visitor checks that all sub-Exps have the right type for
 * their containing Exp, and that Exps have the right type when
 * evaluated. This includes checking function calls, and that tuple
 * accesses are to tuples with the correct types, as well as checking
 * that the tuple plus and minus operators have the right fields in
 * their results. */
public class TypeCheckingVisitor extends DefaultVisitor {
    public static SimpleType VOID = new SimpleType(SimpleType.SimpleTypes.VOID);
    public static SimpleType BOOL = new SimpleType(SimpleType.SimpleTypes.BOOL);
    public static SimpleType INT = new SimpleType(SimpleType.SimpleTypes.INT);
    public static SimpleType STRING = new SimpleType(SimpleType.SimpleTypes.STRING);

    Map<Exp, Type> types = new HashMap<Exp, Type>();
    private SymbolTable st;
    
    public TypeCheckingVisitor() {
    	st = SymbolTable.v();
    }

    /** Propagate the type of an Lvalue. */
    public void leave(Lvalue e) {
    	Type t;
    	if(e.qualifier == null){
    		t = st.findInScope(e.id, currentScope.peek());
    		types.put(e, t);
    		return;
    	}else{
    		t = st.findInScope(e.qualifier, currentScope.peek());
    		if(!(t instanceof TupleType)){
    			Error.error("[T16]: attempt to dereference non-tuple variable");
    		}
    		TupleType tt = (TupleType) t;
    		Schema sc = st.findSchema(tt.id);
    		Iterator<WigField> sc_fields_iter = sc.fields.iterator();
    		WigField field;
    		while(sc_fields_iter.hasNext()){
    			field = sc_fields_iter.next();
    			if(field.id.equals(e.id)){
    				types.put(e, field.t);
    				return;
    			}
    		}
    		Error.error("[T15]: undefined field " + e.id.toString() +" in schema " + e.qualifier);
    	}
    	
    	
    }

    /** Propagate boolean type. */
    public void leave(BoolLiteralExp e) {
    	types.put(e, BOOL);
    }

    /** Propagate int type. */
    public void leave(IntLiteralExp e) {
    	types.put(e, INT);
    }

    /** Propagate string type. */
    public void leave(StringLiteralExp e) {
    	types.put(e, STRING);
    }

    /** Propagate any schema with the fields of the given <code>TupleLiteralExp</code>. */
    public void leave(TupleLiteralExp e) {
    	List<WigField> trial_fields = new LinkedList<WigField>();
    	Iterator<Id> id_iter = e.fields.iterator();
    	Iterator<Exp> exp_iter = e.values.iterator();
    	
    	Id ident;
    	Exp exp;
    	Type t;
    	while(id_iter.hasNext() && exp_iter.hasNext()){
    		ident = id_iter.next();
    		exp = exp_iter.next();
    		t = types.get(exp);
    		trial_fields.add(new WigField(t, ident));
    	}
    	
    	
    	Schema trial_schema = new Schema(null, trial_fields);
    	Schema sc;
    	Iterator<Schema> iter = st.schemas().iterator(); 	
    	while(iter.hasNext()){
    		sc = iter.next();
    		if(sc.fields.equals(trial_schema.fields)){
    			types.put(e, new TupleType(sc.id));
    			return;
    		}
    	}
    	Error.error(Error.T17 + e.toString());
    	return;
    }

    public void leave(UnopExp e) {
    	Type t;
    	switch(e.op){
    	case NEG:
    		if(types.get(e.e).equals(INT)){
    			t = INT;
    			types.put(e,t);
    		} else {
    			Error.error(Error.T02 + e); //neg on non-int exp
    		}
    		break;
    	case NOT:
    		if(types.get(e.e).equals(BOOL)){
    			t = BOOL;
    			types.put(e,t);
    		} else {
    			Error.error(Error.T01 + e); //not on non-bool exp    	
    		}
    		break;
    	}
    }

    public void leave(TupleopExp e) {
	TupleType t = (TupleType)types.get(e.left);
	Set<Schema> schemas = SymbolTable.v().schemas();
	boolean found = false;
	List<WigField> fs = new LinkedList<WigField>();
	Schema ns = SymbolTable.v().findSchema(t.id);

	switch (e.op) {
	case TUPLE_PLUS: 
	    /* Construct a tuple type from LHStype, 
	     * keeping ids on the right. Find it in the symbol table. */
	    for (Id id : e.right) {
		for (WigField ff : ns.fields) {
		    if (ff.id.equals(id))
			fs.add(ff);
		}
	    }
	    for (Schema s : schemas) {
		if (s.fields.equals(fs)) {
		    t = new TupleType(s.id); 
		    found = true;
		}
	    }
	    if (!found) Error.error("[T18]: tuple operation "+e+" gives nonexistent schema.");
	    break;
	case TUPLE_MINUS:
	    /* Construct a tuple type from LHStype, 
	     * dropping ids on the right. Find it in the symbol table. */
	    // similar to TUPLE_PLUS.
		 fs.addAll(ns.fields);
		 for (Id id : e.right) {
			for (WigField ff : ns.fields) {
					if (ff.id.equals(id)) //same as plus except you reject the id
					fs.remove(ff);			  
			}
		 }
	
		 for (Schema s : schemas) {
			if (s.fields.equals(fs)) {
				t = new TupleType(s.id); 
				found = true;
			}
		 }
		 if (!found) Error.error("[T18]: tuple operation "+e+" gives nonexistent schema.");
			    
	    break;
	}
	types.put(e, t);
	return;
    }

    public void leave(CallExp e) {
    	Type t;
    	List<Exp> tArgs = e.args; //args from expression
    	Function f = st.findFunction(e.fname);
    	Exp te; //used for each expression in tArgs
    	Type ft; //used for each type in function parameters
    	Id fi; //used for each id in function paramaters
    	int match = 0;
    	
    	if(f == null){ return; }
    	
    	if(!(tArgs.size() == f.argIds.size())){
    		Error.error(Error.T04 + " " + e.fname.toString()); //unequal arg types
    		return;
    	}
    	
		Iterator<Exp> tArgs_iter = tArgs.iterator();
		Iterator<Type> argTypes_iter = f.argTypes.iterator();
		Iterator<Id> argIds_iter = f.argIds.iterator();	
		
    	while(tArgs_iter.hasNext()){
    		te = tArgs_iter.next();
    		ft = argTypes_iter.next();
    		fi = argIds_iter.next();
    		
   			if(!types.get(te).equals(ft)){
   				Error.error("[T03]: arg type for arg " + fi.toString() + " in call to " + f.id.toString() + " doesn't match: expected " + ft.toString() + ", got " + types.get(te).toString());
   				return;
   			}
   		} 
    	types.put(e, f.returnType);		   	
    	return;
    }

    public void leave(BinopExp e) {
    	Type t;
    	BinopExp.Binop op = e.op;
    	boolean validExp = false;
    	
    	if(op.equals(BinopExp.Binop.TUPLE_FROM)){
    			if(types.get(e.left) == null || types.get(e.right) == null){
	    			Error.error(Error.T16 + e.left.toString());
	    			return;
    			}
    				
    			else if(!((types.get(e.left) instanceof TupleType) && types.get(e.right) instanceof TupleType)){ 
	    			Error.error(Error.T16 + e.left.toString());
	    			return;
	    		}
	    		else {
	    			Set<Schema> schemas = SymbolTable.v().schemas();
	    			List<WigField> ns_fields = new LinkedList<WigField>();
	    			Schema left = st.findSchema(((TupleType)types.get(e.left)).id);
	    			Schema right = st.findSchema(((TupleType)types.get(e.right)).id);
	    			
	    			for(WigField ff : left.fields){
	    				ns_fields.add(ff);
	    			}
	    			
	    			for(WigField ff : right.fields){
	    				if(!ns_fields.contains(ff))
	    					ns_fields.add(ff);
	    			}
	    			
	    			for( Schema s : schemas){
	    				if(ns_fields.equals(s.fields)){
	    					types.put(e, new TupleType(s.id));
	    					return;
	    				}
	    			}
	    			Error.error(Error.T17 + "expression " + e.toString());
	    			return;
	    		}
    	}
    	else if(op.equals(Binop.AND) || op.equals(Binop.OR)){
    		//possible error non-boolean types for E
    		Type l_type = types.get(e.left);
    		Type r_type = types.get(e.right);
    		if(l_type == null || r_type == null){
    			Error.error(Error.T05 + e.toString());
    			return;
			}
    		if(!l_type.equals(BOOL) || !r_type.equals(BOOL)){
    			Error.error(Error.T05 + e.toString());
    			return;
			}
    		types.put(e, BOOL);
    		return;
    	}
    	else if(op.equals(Binop.EQ) || op.equals(Binop.NEQ)){
    		//unequal arg types for E
    		Type l_type = types.get(e.left);
    		Type r_type = types.get(e.right);
    		if(l_type == null || r_type == null){
    			Error.error("[T04] Unequal arg types for " + e.toString());
    			return;
			}
    		if(!l_type.equals(r_type)){
    			Error.error("[T04] Unequal arg types for" + e.toString());
    			return;
			}
    		types.put(e, BOOL);
    		return;
    	}
    	else if(op.equals(Binop.PLUS) || op.equals(Binop.LE) || op.equals(Binop.LT)
    			|| op.equals(Binop.GT) || op.equals(Binop.GE)){
    		//common for both string and int, T07
    		Type l_type = types.get(e.left);
    		Type r_type = types.get(e.right);
    		if(l_type == null || r_type == null){
    			Error.error(Error.T07 + e.toString());
    			return;
			}
    		if(l_type.equals(STRING)){
    			if(!r_type.equals(STRING)){
    			Error.error(Error.T07 + e.toString());
    			return;
    			}
    		}else if(l_type.equals(INT)){
    			if(!r_type.equals(INT)){
        			Error.error(Error.T07 + e.toString());
        			return;
        			}
    		}
    		
    		if(op.equals(Binop.PLUS)){
    			types.put(e, l_type);
    			return;
    		}
    		types.put(e, BOOL);
    		return;
    	}else if(op.equals(Binop.MINUS) || op.equals(Binop.MOD) 
    			|| op.equals(Binop.TIMES) || op.equals(Binop.DIV)){
    		Type l_type = types.get(e.left);
    		Type r_type = types.get(e.right);
    		if(l_type == null || r_type == null){
    			Error.error(Error.T06 + e.toString());
    			return;
			}
    		if(!l_type.equals(INT) || !r_type.equals(INT)){
    			Error.error(Error.T06 + e.toString());
    			return;
			}
    		types.put(e, INT);
    		return;
    	}
    }

    /** Check that LHS and RHS have the same type. */
    public void leave(AssignExp e) {
    	Type t;    	
    	Type tempt = st.findInScope(e.lhs.id, currentScope.peek());    	
    	if(tempt!=null && types.get(e.rhs)!=null){
    		if(types.get(e.rhs).equals(tempt)){
    			t = types.get(e.rhs);
    			types.put(e, t);
    		} else   
    		Error.error(Error.T09 + e); //nonequal types   	
    	}
    }

    /** Check that <code>s</code> occurs inside a <code>Function</code>
     * and that the returned value has the proper type. */
    public void leave(ReturnStm s) {
    	Stack<ASTNode> stack_capture = (Stack<ASTNode>) currentScope.clone();
    	ASTNode node;
    	Function f = null;
    	boolean within_function_block = false;
    	while(!stack_capture.empty()){
    		node = stack_capture.pop();
    		if(node instanceof Function){
    			within_function_block = true;
    			f = (Function)node;
    			break;
    		}
    	}
    	if(!within_function_block){
    		Error.error(Error.T14); //return outside the function error
    		return;
    	}
    	
    	Type exp_type = types.get(s.e);
    	if(!f.returnType.equals(exp_type))
    		Error.error(Error.T10 + s.e); //mismatched return type
    }

    /** Check that <code>s</code> occurs inside a <code>Function</code>. */
    public void leave(ReturnVoidStm s) {
    	Stack<ASTNode> stack_capture = (Stack<ASTNode>) currentScope.clone();
    	ASTNode node;
    	Function f = null;
    	boolean within_function_block = false;
    	while(!stack_capture.empty()){
    		node = stack_capture.pop();
    		if(node instanceof Function){
    			within_function_block = true;
    			f = (Function)node;
    			break;
    		}
    	}
    	
    	if(!within_function_block){
    		Error.error(Error.T14); //return outside the function error
    		return;
    	}
    	
    	if(!f.returnType.equals(VOID))
    		Error.error(Error.T11); //RETURNING VOID from non-void function
    }

    /** Check that the if-condition is boolean. */
    public void leave(IfStm s) {
    	if(types.get(s.c) == null){
    		Error.error(Error.T12 + s.c.toString());
    		return;
    	}
    	if(!types.get(s.c).equals(BOOL))
    		Error.error(Error.T12 + s.c.toString());
    }

    /** Check that the while-condition is boolean. */
    public void leave(WhileStm s) {
    	if(types.get(s.c) == null){
    		Error.error(Error.T12 + s.c.toString());
    		return;
    	}
    	if(!types.get(s.c).equals(BOOL))
    		Error.error(Error.T13 + s.c.toString());
    }

    /** Check that expressions have non-void simple types. */
    public void leave(PlugDocument d) {
    	Type t;
    	for(Exp e : d.plugContents){
    		if((t = types.get(e))!=null){
	    		if(t.equals(BOOL) || t.equals(INT) || t.equals(STRING)){
	    			continue;
	    		}
	    		Error.error(Error.T08 + e.toString());
	    	} else {
	    			Error.error(Error.T08 + e.toString());
	    	}
    	}
    }    
    // Scope operations.
    Stack<ASTNode> currentScope = new Stack<ASTNode>();

    /** Record the current scope so that we can query it in the symbol
     * table. */
    public void enter(CompoundStm s) {
    	currentScope.push(s);
    }
    /** Leave a scope. */
    public void leave(CompoundStm s) {
    	currentScope.pop();
    }
    public void enter(Function f){
    	currentScope.push(f);
    }
    public void leave(Function f){
    	currentScope.pop();
    }
    public void enter(Schema s){
    	currentScope.push(s);
    }
    
    public void leave(Schema s){
    	currentScope.pop();
    }

	public Map<Exp, Type> getTypes() {
		return types;
	}
}
