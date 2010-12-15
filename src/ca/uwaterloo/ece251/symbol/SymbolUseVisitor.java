package ca.uwaterloo.ece251.symbol;

import ca.uwaterloo.ece251.ast.*;
import ca.uwaterloo.ece251.Error;

import java.util.Iterator;
import java.util.Stack;

/** This just checks that every variable we use has a definition. */
public class SymbolUseVisitor extends DefaultVisitor {
	private SymbolTable st;
	private Stack<ASTNode>	current_ast_scopes;
	
    public SymbolUseVisitor() {
    	st = SymbolTable.v();
    	current_ast_scopes = new Stack<ASTNode>();
    	current_ast_scopes.push(st.getGlobal());
    }
    
    /*FUNCTIONS TO KEEP TRACK OF THE CURRENT SCOPE */
    public void enter(Schema s){
    	current_ast_scopes.push(s);
    }
    public void leave(Schema s){
    	current_ast_scopes.pop();
    }
    public void enter(Function f){
    	current_ast_scopes.push(f);
    }
    public void leave(Function f) {
    	current_ast_scopes.pop();
    }
    public void enter(CompoundStm stm) {
    	current_ast_scopes.push(stm);
    }
    public void leave(CompoundStm stm) {
    	current_ast_scopes.pop();
    }
    
    //Check for schema declartion
    public void enter(Variable v){
    	if(v.t instanceof TupleType){
    		TupleType t_type = (TupleType) v.t;
    		if(st.findSchema(t_type.id) == null)
    			Error.error(Error.S01 + t_type.id.toString());
    	}
    }
    
    /*ShowStm and ExitStm are the only statements that contains Documents which make reference to HTML*/
    public void enter(PlugDocument d){
    	HTML html = st.findHTML(d.id);
    	if(html == null)
    		Error.error(Error.S02 + d.id.toString());
    }
    
    /*ShowStm and ExitStm are the only statements that contains Documents which make reference to HTML*/
    public void enter(BaseDocument d){
    	HTML html = st.findHTML(d.id);
    	if(html == null)
    		Error.error(Error.S02 + d.id.toString());
    }
    
    
    public void enter(Lvalue l){
    	ASTNode current_node = current_ast_scopes.peek();
    	if(l.qualifier == null){
    		if(st.findInScope(l.id, current_node) == null)
    			Error.error(Error.S03 + l.id);
    	}
    	else{
    		if(st.findInScope(l.qualifier, current_node) == null)
    			Error.error(Error.S03 + l.qualifier);
    	}
    }
    
    public void enter(CallExp exp){
    	if(st.findFunction(exp.fname) ==null)
    		Error.error(Error.S04 + exp.fname.toString());
    }
}
