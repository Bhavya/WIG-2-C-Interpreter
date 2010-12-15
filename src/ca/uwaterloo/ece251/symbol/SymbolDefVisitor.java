package ca.uwaterloo.ece251.symbol;

import ca.uwaterloo.ece251.ast.*;
import ca.uwaterloo.ece251.Error;
import java.util.Iterator;

/** Creates a Symbol for each defined name.
 * Throws an error in the event of a multiply-defined name. */
public class SymbolDefVisitor extends DefaultVisitor {
	private SymbolTable st;
    public SymbolDefVisitor() {
		st = SymbolTable.v();
    }

    /* Introduce HTML h into symbol table. */
    public void enter(HTML h) {
    	if(st.findHTML(h.id) != null){
    		Error.error(Error.S08 + h.id.toString()); //multiply-defined html;
    		return;
    	}
    	st.insert(h);
    	
    }
    
    /* Introduce Function f into symbol table. */
    public void enter(Function f){
    	if(st.findFunction(f.id) != null){
    		Error.error(Error.S10 + f.id.toString()); //multiply-defined function;
    		return;
    	}
    	
    	st.insert(f);
    	st.enterScope(f);
    	Iterator<Id> iter_id = f.argIds.iterator();
    	Iterator<Type> iter_type =  f.argTypes.iterator();
    	while(iter_id.hasNext() && iter_type.hasNext()){
    		st.insert(iter_type.next(), iter_id.next());
    	}
    }
    
    /* */
    public void leave(Function f){
    	st.leaveScope();
    }
    
    /* Introduce Function f into symbol table. */
    public void enter(Variable v){
    	Id ident;
    	Iterator<Id> iter = v.identifiers.iterator();
    	while(iter.hasNext()){
    		ident = iter.next();
    		if(st.findLocal(ident) != null){
    			Error.error(Error.S07 + ident.toString()); //multiply-defined variable
    			continue;
    		} else {
    			st.insert(v.t, ident);
    		}
    	}
    }
    
    /* Introduce Function f into symbol table. */
    public void enter(Schema s){
    	if(st.findSchema(s.id) != null){
    		Error.error(Error.S05 + s.id.toString()); //multiply-defined schema
    		return;
    	}
    	st.enterScope(s);
    	st.insert(s);
    }
    
    public void leave(Schema s){
    	st.leaveScope();
    }
    
    public void enter(WigField f){
    	if(st.findLocal(f.id) != null){
    		Error.error(Error.S06 + f.id.toString()); //multiply-define field name
    		return;
    	}
    		
    	st.insert(f.t, f.id);
    }
    
    /* Introduce Function f into symbol table. */
    public void enter(Session s){
    	if(st.findSession(s.id) != null){
    		Error.error(Error.S09 + s.id.toString()); //multiply-defined session
    		return;
    	}
    	//st.enterScope(s);
    	st.insert(s);
    }
    
    public void leave(Session s){
    	//st.leaveScope();
    }
    
    
    public void enter(CompoundStm stm){
    	st.enterScope(stm);
    }
    
    public void leave(CompoundStm stm){
    	st.leaveScope();
    }
}
