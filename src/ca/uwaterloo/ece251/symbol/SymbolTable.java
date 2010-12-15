package ca.uwaterloo.ece251.symbol;
import ca.uwaterloo.ece251.ast.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import java.util.Stack;
import java.util.Set;
import java.util.HashSet;

/** Symbol table. */
public class SymbolTable {
    private ASTNode GLOBAL = new CompoundStm(null, null);
    
    private SymbolTable() {
	enterScope(GLOBAL);
	//scopesForASTNode.put(GLOBAL, currentScopes);
    }

    private static SymbolTable instance = new SymbolTable();
    public static SymbolTable v() { return instance; }

    /* In WIG, htmls, functions, sessions, and schemas are top-level
     * definitions. */

    /* Functions and schemas live in separate namespaces, while
     * all variables live in the same namespace. 
     * This means that we allow a function and a variable to have the
     * same name. */

    Map<Id, HTML> htmls = new java.util.HashMap<Id, HTML>();
    Map<Id, Function> functions = new java.util.HashMap<Id, Function>();
    Map<Id, Schema> schemas = new java.util.HashMap<Id, Schema>();
    Map<Id, Session> sessions = new java.util.HashMap<Id, Session>();

    Stack<Map<Id, Type>> currentScopes = new Stack<Map<Id, Type>>();
    Map<ASTNode, Stack<Map<Id, Type>>> scopesForASTNode = 
	new HashMap<ASTNode, Stack<Map<Id, Type>>>();

    /** Add a new html into the global symbol table. */
    public void insert(HTML h) {
    	htmls.put(h.id, h);
    }

    /** Returns the HTML with the given name. */
    public HTML findHTML(Id id) {
    	return htmls.get(id);
    }

    /** Add a new session into the global symbol table. */
    public void insert(Session s) {
    	sessions.put(s.id, s);
    }

    /** Returns the session with the given name. */
    public Session findSession(Id id) {
    	return sessions.get(id);
    }

    /** Add a new function into the global symbol table. */
    public void insert(Function f) {
    	functions.put(f.id, f);
    }

    /** Returns the function with the given name. */
    public Function findFunction(Id id) {
    	return functions.get(id);
    }

    /** Returns the set of functions. */
    public Set<Function> functions() {
    	Set<Function> funcs = new HashSet<Function>();
    	Collection<Function> c = functions.values();
    	for(Function f: c){
    		funcs.add(f);
    	}
    	return funcs;
    }

    /** Add a new schema into the global symbol table.
     * Checks the schema for multiply-defined field names. */
    public void insert(Schema s) {
    	schemas.put(s.id, s);
    }

    /** Returns the set of schemas. */
    public Set<Schema> schemas() {
    	Set<Schema> sc = new HashSet<Schema>();
    	Collection<Schema> c = schemas.values();
    	for(Schema s : c){
    		sc.add(s);
    	}
    	return sc;
    }

    /** Returns the schema with the given name. */
    public Schema findSchema(Id id) {
    	return schemas.get(id);
    }

    /** Insert a variable into the current scope. */
    public void insert(Type t, Id v) {
    	currentScopes.peek().put(v, t);
    }

    /** Start a new scope and associate it with the given ASTNode. */
    public void enterScope(ASTNode s) {
    	Map<Id, Type> new_scope = new HashMap<Id, Type>();
    	currentScopes.push(new_scope);
    	Stack<Map<Id, Type>> scopes_for_ast = (Stack<Map<Id, Type>>) currentScopes.clone();
    	scopesForASTNode.put(s, scopes_for_ast);
    }

    /** Leave a scope. **/
	public void leaveScope() {
    	currentScopes.pop();
    }

    /** Returns the type of <code>id</code> if defined in the current scope,
     * else returns null. */
    public Type findLocal(Id id) { 
    	Stack<Map<Id, Type>> current_scope = (Stack<Map<Id, Type>>) currentScopes.clone();
    	Type retval = null;   	    	
    	Map<Id, Type> cs = current_scope.peek();
    	retval = cs.get(id);    			
    	return retval;
    }

    /** Returns the type of <code>id</code> if defined,
     * else returns null. */
    public Type find(Id id) { 
    	Map<Id, Type> scope;
    	Stack<Map<Id, Type>> cloned_scopes = (Stack<Map<Id, Type>>) currentScopes.clone();
    	while(!cloned_scopes.empty()){
    		scope = cloned_scopes.pop();
    		if(scope.containsKey(id))
    			return scope.get(id);
    	}
    	return null;
    }

    /** Returns the type of <code>id</code> if defined in the scope of
     * <code>cs</code>, else returns null. */
    public Type findInScope(Id id, ASTNode cs) {
    	Stack<Map<Id, Type>> node_scopes = (Stack<Map<Id, Type>>)scopesForASTNode.get(cs).clone();
    	Map<Id, Type> scope;
    	Type retval = null;
    	while(node_scopes.size()>0){
    		scope = node_scopes.pop();
    		if((retval = scope.get(id))!=null)
    			break;
    	}
    	return retval;
    }
    
    /** Returns all id/type variable mappings in scope of <code>cs</code>. */
    public Map<Id, Type> getScope(ASTNode cs) {
    	Stack<Map<Id, Type>> scopes = scopesForASTNode.get(cs);
    	//while(!scopes.isEmpty())
    	return scopes.peek();
    }

    void dumpScope(final ASTNode n, final StringBuffer sb) {
	Stack<Map<Id, Type>> s = scopesForASTNode.get(n);
	Map<Id, Type> m;

	// We always have the GLOBAL scope. If we're specifically supposed
	// to print it, then do so; otherwise we have one scope available.
	if (n == GLOBAL) {
	    sb.append("GLOBAL: "); 
	    m = s.get(0);
	}
	else {
	    if (n instanceof Function) {
		sb.append("Function "+((Function)n).id+": ");
	    } else if (n instanceof Session) {
		sb.append("Session "+((Session)n).id+": ");
		dumpScope(((Session)n).body, sb);
		return;
	    }

	    m = s.peek();
	}

	Id[] ids = m.keySet().toArray(new Id[0]);
	String[] outs = new String[ids.length];
	Arrays.sort(ids);
	
	for (int i = 0; i < ids.length; i++) {
	    outs[i] = ids[i] + ": " + m.get(ids[i]);
	}
	sb.append(Arrays.asList(outs));

	if (n != GLOBAL) {
	    n.accept(new DefaultVisitor() {
		    public void enter(CompoundStm s) {
			if (s != n)
			    dumpScope(s, sb); 
		    }
		});
	}
    }
    
    public ASTNode getGlobal(){
    	return GLOBAL;
    }

    public String dump() {
	StringBuffer sb = new StringBuffer();
	Id[] ha = htmls.keySet().toArray(new Id[0]);
	Id[] fa = functions.keySet().toArray(new Id[0]);
	Id[] sca = schemas.keySet().toArray(new Id[0]);
	Id[] sa = sessions.keySet().toArray(new Id[0]);
	Arrays.sort(ha); Arrays.sort(fa); Arrays.sort(sca);
	sb.append("HTMLs: "+Arrays.asList(ha)+"\n");
	sb.append("Functions: "+Arrays.asList(fa)+"\n");
	sb.append("Schemas: "+Arrays.asList(sca)+"\n");
	sb.append("Sessions: "+Arrays.asList(sa)+"\n");
	sb.append("\n");
	sb.append("List of scopes:\n");
	dumpScope(GLOBAL, sb); 
	sb.append("\n");
	for (Id f : fa) {
	    dumpScope(findFunction(f), sb);
	    sb.append("\n");
	}
	for (Id f : sa) {
	    dumpScope(findSession(f), sb);
	    sb.append("\n");
	}

	return sb.toString();
    }
}
