package ca.uwaterloo.ece251.codegen;

import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;

import ca.uwaterloo.ece251.ast.*;
import ca.uwaterloo.ece251.ast.BinopExp.Binop;
import ca.uwaterloo.ece251.symbol.*;
import ca.uwaterloo.ece251.type.TypeCheckingVisitor;

public class CodeGenVisitor extends DefaultVisitor
{
    PrintWriter w;
    String base;
    String url;
    TypeCheckingVisitor tv;
    Session currentSession;
    int labelCount = 0;
    
    
    private Map<Exp, Type> exp_type_def;
    private SymbolTable st;
    public CodeGenVisitor(PrintWriter w, String base, String url, TypeCheckingVisitor tv) {
	this.w = w; this.base = base; this.url = url; this.tv = tv; this.exp_type_def = tv.getTypes();
	this.st = SymbolTable.v();
    }

    public void enter(Service s) {
	w.println("#include <stdio.h>");
	w.println("#include <stdbool.h>");
	w.println("#include <string.h>");
	w.println("#include <stdlib.h>");
	w.println("#include <time.h>");
	w.println("#include \"runwig.h\"");
	w.println();
	w.println("char *url;");
	w.println("char *sessionid;");
	w.println("int pc;");
	w.println("FILE *f;");
	w.println("#define SLEN 80");
	w.println();
    }
    public void leave(Service s) {
	w.close();
    }

    /** Emit a functi4on header for a function producing the given HTML.
     * Function takes the plug ids as arguments (each of type char *). 
     * e.g. 'void output_Total(char *total) {' */
    public void enter(HTML h) {
    	w.print("void output_" + h.id + "(");
    	List<Id> plugs = h.plugs();
    	Iterator<Id> arg_iter = plugs.iterator();
    	Id arg;
    	
    	if(arg_iter.hasNext()){
    		arg = arg_iter.next();
    		w.print("char * " + arg.toString());
    	}else{
    		w.print(")\n{\n"); //no paramteters
    		return;
    	}
    	
    	while(arg_iter.hasNext()){
    		arg = arg_iter.next();
    		w.print(", char * " + arg.toString());
    		
    	}
		w.println(")\n{\n"); 
    }
    /** Emit a printf statement with the contents of HTMLBody b. 
     * e.g. 'printf("<br>");' */
    public void enter(OpenHTMLBody b) {
    	w.println("printf(\"" + b.toString() +"\");");
    	/*if(b.attrs != null){
    		Iterator<Attribute> attr_iter = b.attrs.iterator();
    		Attribute attr;
    		while(attr_iter.hasNext()){
    			attr = attr_iter.next();
    			w.print(" " + attr.toString());
    			
    		}
    	}*/
    	//OpenHTMLBody already has an appropriate toString method!
    }
    /** Emit a printf statement with the contents of HTMLBody b. 
     * e.g. 'printf("</p>");' */
    public void enter(CloseHTMLBody b) {
    	w.println("printf(\"" + b.toString() +"\");");
    }
    /** Emit a printf statement with the escaped contents of HTMLBody b. 
     * You may bail out if b.contents is whitespace-only. 
     * Util.escape() is useful here.
     * e.g. 'printf("Welcome!\n");' */
    public void enter(WhateverHTMLBody b) {
    	if(!b.contents.equals(" ")){
    		String output = Util.escape(b.contents.toString());
    		String doublecheck = output;
    		doublecheck = doublecheck.replace("\n",""); //slight hack that does a check to get rid of empty output
    		doublecheck = doublecheck.replace(" ","");
    		if(doublecheck.length() > 2){
    			w.println("printf(\"" + output +"\");");
    		}
    	}
    	//TODO: WILL NEED SOME MORE WORK
    }
    /** Same as Whatever, except that you should call toString() on b. */
    public void enter(InputHTMLBody b) {
    	if(!b.toString().equals(" ")){
    		String output = Util.escape(b.toString());
    		w.println("printf(\"" + output +"\\n\");");
    	}
    }
    /** Emit a print statement for the given plug. 
     * e.g. printf("%s", total); */
    public void enter(PlugHTMLBody b) {
    	String content = b.id.toString();
    	w.println("printf(\"%s\", " + content + ");");
    }

    /* Emit code to finish writing the HTML function. */
    public void leave(HTML h) {
    	w.println("}\n");
    }

    /** Emit boilerplate code to begin a session. */
    public void enter(Session s) {
	currentSession = s;

	printVariableDeclarations(s);

	String sessionName = s.id.toString();
	w.println("int main() {");

	Map<Id, Type> scope = st.getScope(s.body);
	for (Id i : scope.keySet()) {
	    if (isString(scope.get(i)))
		w.println("  "+qualifiedVariableName(i)+" = malloc(SLEN);");
	}

	w.println("  srand48(time(0L));");
	w.println("  parseFields();");
	w.println("  url = \""+url+"\";");
	w.println("  sessionid = getenv(\"QUERY_STRING\");");
	w.println();
	w.println("  if (!sessionid || !*sessionid || strcmp(sessionid, \""+sessionName+"\")==0)");
	w.println("    goto start_"+sessionName+";");
	w.println("  if (strncmp(sessionid, \""+sessionName+"$\","+
		  (sessionName.length()+1)+")==0)");
	w.println("    goto restart_"+sessionName+";");
	w.println("  printf(\"Content-type: text/html\\n\\n\");");
	w.println("  printf(\"<title>Illegal Request</title>\\n\");");
	w.println("  printf(\"<h1>Illegal Request: %s</h1>\\n\",sessionid);");
	w.println("  exit(1);");
	w.println();
	w.println("start_"+sessionName+":");
	w.println("  sessionid = randomString(\""+sessionName+"\",20);");
	// body of session follows
    }

    /** Generate a qualified name for s,
	e.g. local_Contribute_i. 
    * This helps with disambiguation in the presence of functions and
    * compound statements, which we're not implementing. */
    private String qualifiedVariableName(Id s) {
	return "local_"+currentSession.id+"_"+s.toString();
    }

    /** Emit variable declarations for the variables in Session s.
     * Hint: call getScope() with s's body. */
    private void printVariableDeclarations(Session s) {
    	Map<Id, Type> session_scope = st.getScope(s.body);
    	
    	Set<Entry<Id, Type>> id_type_set = session_scope.entrySet();
    	for(Entry<Id, Type> pair : id_type_set){
    		if(pair.getValue().equals(TypeCheckingVisitor.INT)){
    			w.println("int " + qualifiedVariableName(pair.getKey()) + ";");
    		}else if(pair.getValue().equals(TypeCheckingVisitor.STRING)){
    			w.println("char * " + qualifiedVariableName(pair.getKey()) + ";");
    		}else if(pair.getValue().equals(TypeCheckingVisitor.BOOL)){
    			w.println("bool " + qualifiedVariableName(pair.getKey()) + ";");
    		}else if(pair.getValue().equals(TypeCheckingVisitor.VOID)){
    			//NO VOID type variable
    		}
    	}
    }

    /** Emit code to evaluate a given experssion.
     * Use the ExpVisitor to generate this code, to keep the
     * main CodeGenVisitor simpler. */
    public void leave(EvalStm s) {
	w.println("  /* "+s.toString()+" */");
        w.print("  ");
	ExpVisitor ev = new ExpVisitor();
	s.c.accept(ev);
	w.print(ev.rv.toString());
	w.println(";");
    }

    /** Emit code to show an HTML (first half). */
    public void enter(ShowStm s) {
    w.println("  /* show "+s.d.toString()+"... */");
    w.println("  printf(\"Content-type: text/html\\n\\n\");");
	w.println("  printf(\"<form method=\\\"POST\\\" action=\\\"%s?%s\\\">\\n\",url,sessionid);");
    }

    /** Emit a call to the function we declared earlier to output an HTML (no plugs). 
     * e.g. output_Welcome(); */
    public void enter(BaseDocument s) {
    	w.println("output_" + s.id + "();");
    }

    /** Emit a call to the function we declared earlier to output an HTML
     * (plugs).  Iterate over plugContents. Don't forget that you have:
     * 1) Util.commaSeparated and 2) a handy ExpVisitor to emit code
     * for the plug contents. 
     * You should also wrap ints with an itoa() call.
     * e.g. output_Total(itoa(getGlobalInt("global_tiny_amount"))); */
    public void enter(PlugDocument d) {
    	w.print("output_" + d.id + "(");
    	Iterator<Exp> plug_iter = d.plugContents.iterator();
    	Type exp_type;
    	Exp plug_content;
    	while(plug_iter.hasNext()){
    		ExpVisitor ev = new ExpVisitor();
    		plug_content = plug_iter.next();
    		exp_type = exp_type_def.get(plug_content);
    		plug_content.accept(ev);
    		if(exp_type.equals(TypeCheckingVisitor.INT)){
    			w.print("itoa(" + ev.rv.toString() + ")");
    		}else if(exp_type.equals(TypeCheckingVisitor.STRING)){
    			w.print(ev.rv.toString());
    		}
    		if(plug_iter.hasNext())
    			w.print(", ");
    	}
    	w.println(");");
    	
    }

    /* Emit code to show an HTML (second half: receives).
     * For each receive, fill in the local variable with the 
     * value you get from the CGI arguments, provided by getField.
     * Wrap in atoi() for integer variables.
     * i.e. local_Contribute_i = atoi(getField("contribution")); */
    public void leave(ShowStm s) {
	w.println("  printf(\"<p><input type=\\\"submit\\\" value=\\\"continue\\\">\\n\");");
	w.println("  printf(\"</form>\\n\");");
	saveVariables();

	w.println("  /* ... receive ["+Util.commaSeparated(s.receives)+"]; */ ");
	// write more code here
	
		for(Input input : s.receives){
			Id local_variable_name = input.lhs.id;
			w.print(qualifiedVariableName(local_variable_name));
			w.print(" = ");
			if(st.findInScope(local_variable_name, currentSession.body).equals(TypeCheckingVisitor.INT)){
				//WRAP in ATOI if the local variable is of int type:
				w.println("atoi(getField(\"" + input.rhs.toString() + "\"));");
			}if(st.findInScope(local_variable_name, currentSession.body).equals(TypeCheckingVisitor.STRING)){
				w.println("getField(\"" + input.rhs.toString() + "\");");
			}
		}
    }

    /** Boilerplate code before an exit statement. */
    public void enter(ExitStm s) {
        w.println("  /* "+s.toString()+" */");
	w.println("  printf(\"Content-type: text/html\\n\\n\");");
    }

    /** Boilerplate code after an exit statement. */
    public void leave(ExitStm s) {
	w.println("  exit(0);");
    }

    /* Note: does not support else clauses. */
    public void enter(IfStm s) {
	w.println("  /* if ("+s.c.toString()+") */");
	ExpVisitor ev = new ExpVisitor();
	s.c.accept(ev);
	w.println("  if ("+ev.rv+") {");
    }

    public void leave(IfStm s) {
	w.println("  }");
    }

    public void enter(WhileStm s) {
	w.println("  /* while ("+s.c.toString()+") */");
	ExpVisitor ev = new ExpVisitor();
	s.c.accept(ev);
	w.println("  while ("+ev.rv+") {");
    }

    public void leave(WhileStm s) {
	w.println("  }");
    }

    public void enter(CompoundStm s) {
	// need not implement
    }

    public void enter(ReturnStm s) {
	// need not implement
    }

    public void enter(ReturnVoidStm s) {
	// need not implement
    }

    /** Boilerplate code. Note that it also emits code to restore
     * variables, including the PC. */
    public void leave(Session s) {
	w.println("restart_"+s.id+":");
	w.println("  f = fopen(sessionid, \"r\");");
	w.println("  fscanf(f, \"%i\\n\",&pc);");
	restoreVariablesHelper();
	for (int i = 1; i <= labelCount; i++) {
	    w.println("  if (pc=="+i+") goto "+s.id+"_"+i+";");
	}
        w.println("}");
    }

    /** Emit boilerplate code around saving variables. */
    private void saveVariables() {
	w.println("  f = fopen(sessionid,\"w\");");
	w.println("  fprintf(f, \""+(labelCount+1)+"\\n\");");
	saveVariablesHelper();
	w.println("  fclose(f);");
	w.println("  exit(0);");
	labelCount++;
	w.println(currentSession.id+"_"+labelCount+":");
    }

    /** Emit code to actually save the variables.
     * Use fprintf with %i for ints and bools and %s for strings.
     * The symbol table tells you the names and types of the variables.
     * e.g. fprintf(f, "%i\n", local_Contribute_i); */
    private void saveVariablesHelper() {
    	Set<Entry<Id, Type>> local_variables = st.getScope(currentSession.body).entrySet();
    	Iterator<Entry<Id, Type>> entry_iter = local_variables.iterator();
    	Entry<Id, Type> variable;
    	
    	while(entry_iter.hasNext()){
    		variable = entry_iter.next();
    		w.print("fprintf(f, ");
    		if(variable.getValue().equals(TypeCheckingVisitor.INT) || variable.getValue().equals(TypeCheckingVisitor.BOOL)){
    			w.print("\"%i\\n\"," + qualifiedVariableName(variable.getKey()) + ");");
    		}else if(variable.getValue().equals(TypeCheckingVisitor.STRING)){
    			w.print("\"%s\\n\"," + qualifiedVariableName(variable.getKey()) + ");");
    		}
    		w.println();
    	}
    }
    /** Emit code to restore the variables.
     * Mostly the converse of saveVariablesHelper().
     * Note that this is like register spilling.
     * Don't forget that you should & scanf args if they're ints,
     *  but not strings.
     * e.g. fscanf(f, "%i\n", &local_Contribute_i);*/
    private void restoreVariablesHelper() {
    	Set<Entry<Id, Type>> local_variables = st.getScope(currentSession.body).entrySet();
    	Iterator<Entry<Id, Type>> entry_iter = local_variables.iterator();
    	Entry<Id, Type> variable;
    	
    	while(entry_iter.hasNext()){
    		variable = entry_iter.next();
    		w.print("  fscanf(f, ");
    		if(variable.getValue().equals(TypeCheckingVisitor.INT) || variable.getValue().equals(TypeCheckingVisitor.BOOL)){
    			w.print("\"%i\\n\",&" + qualifiedVariableName(variable.getKey()) + ");");
    		}else if(variable.getValue().equals(TypeCheckingVisitor.STRING)){
    			w.print("\"%s\\n\"," + qualifiedVariableName(variable.getKey()) + ");");
    		}
    		w.println();
    	}
    
    }

    /** Helper class to emit code for expressions. */
    class ExpVisitor extends DefaultVisitor {
	/** Buffer for the expression being emitted. */
	StringBuffer rv = new StringBuffer();
	/** When silence is nonempty, don't emit anything. */
	Stack silence = new Stack();

	/** Shh! */
	public void enter(AssignExp e) {
	    silence.push(new Object());
	}

	/** Create code for the assignment expression. 
	 * This is a bit tricky. I recommend that you
	 * create new ExpVisitors for both the lhs and rhs,
	 * and add the resulting strings together with an "=" sign 
	 * in between.
	 * However, there is one special case: if you are assigning to a
	 * _global_ variable (not in the curent scope), then you
	 * have to call putGlobalInt() instead of using the = sign.
	 * e.g. local_Contribute_i = local_Contribute_i + 1;
	 * e.g. putGlobalInt("global_tiny_amount", local_Contribute_i + 1); */
	public void leave(AssignExp e) {
		silence.pop();
		if(silence.isEmpty()){
		Map<Id, Type> session_scope = st.getScope(currentSession.body);
	    ExpVisitor rhs_visitor = new ExpVisitor();
	    e.rhs.accept(rhs_visitor);
		if(session_scope.containsKey(e.lhs.id)){
			if(session_scope.get(e.lhs.id).equals(TypeCheckingVisitor.INT) || session_scope.get(e.lhs.id).equals(TypeCheckingVisitor.BOOL)){
				rv.append(qualifiedVariableName(e.lhs.id) + " = ");
				rv.append(rhs_visitor.rv);
			}else if(session_scope.get(e.lhs.id).equals(TypeCheckingVisitor.STRING)){
				rv.append("strcpy(" + qualifiedVariableName(e.lhs.id) + "," +  rhs_visitor.rv + ")");
			} 
		}else{//lhs is a global variable
			if(st.findInScope(e.lhs.id, currentSession.body).equals(TypeCheckingVisitor.INT)){
				rv.append("putGlobalInt(\"global_" + base + "_" + e.lhs+ "\", " +  rhs_visitor.rv +")");
			}else if(st.findInScope(e.lhs.id, currentSession.body).equals(TypeCheckingVisitor.STRING)){
				rv.append("putGlobalString(\"global_" + base + "_" + e.lhs+ "\", " +  rhs_visitor.rv +")");
			}else if(st.findInScope(e.lhs.id, currentSession.body).equals(TypeCheckingVisitor.BOOL)){
				//
			}
		}
		}
	}

	/** Shh! */
	public void enter(BinopExp e) {
	    silence.push(new Object());
	}
	/** Emit code for the binary expression.
	 * Similar to leave(assignExp), except that you have to
	 * account for precedence.
	 * See the toString() method of BinopExp for hints. 
	 */
	public void leave(BinopExp e) {
		silence.pop();
		if(silence.isEmpty()){
		ExpVisitor lhs_visitor = new ExpVisitor();
		e.left.accept(lhs_visitor);
	    ExpVisitor rhs_visitor = new ExpVisitor();
	    e.right.accept(rhs_visitor);
	    
	    String lop = lhs_visitor.rv.toString();
	    String rop = rhs_visitor.rv.toString();
	    
		if (Precedence.p(e.left).ordinal() > Precedence.p(e).ordinal())
			lop = "(" + lhs_visitor.rv.toString() + ")";
		if (Precedence.p(e.right).ordinal() > Precedence.p(e).ordinal())
		    rop = "(" + rhs_visitor.rv.toString() + ")";
	    
	    if(exp_type_def.get(e.left).equals(TypeCheckingVisitor.INT) || exp_type_def.get(e.left).equals(TypeCheckingVisitor.BOOL))
	    	rv.append(lop + " " + e.op.toString() + " " + rop);
	    else if(exp_type_def.get(e.left).equals(TypeCheckingVisitor.STRING)){
	    	if(e.op == Binop.EQ){
	    		rv.append("strcmp(" + lop + "," + rop + ")==0" );
	    	}else if(e.op == Binop.GE){
	    		rv.append("strcmp(" + lop + "," + rop + ")>=0" );
	    	}else if(e.op == Binop.GT){
	    		rv.append("strcmp(" + lop + "," + rop + ")>0" );
	    	}else if(e.op == Binop.LE){
	    		rv.append("strcmp(" + lop + "," + rop + ")<=0" );
	    	}else if(e.op == Binop.LT){
	    		rv.append("strcmp(" + lop + "," + rop + ")<0" );
	    	}else if(e.op == Binop.NEQ){
	    		rv.append("strcmp(" + lop + "," + rop + ")!=0" );
	    	}else if(e.op == Binop.PLUS){
	    		rv.append("strcat(" + lop + "," + rop + ")" );
	    	}
	    }else{
	    	System.err.println("SOMETHING's WRONG WITH YOUR BINOP CODE GEN CODE");
	    }
		}
	}
	public void enter(CallExp ce) {
	    // do not implement
	}
	public void enter(TupleopExp te) {
	    // do not implement
	}

	/** Shh! */
	public void enter(UnopExp ue) {
	    silence.push(new Object());
	}
	/** Write the unary expression, preceded by its operator. 
	 * Similar to BinopExp. */
	public void leave(UnopExp ue) {
		silence.pop();
		if(silence.isEmpty()){
		ExpVisitor exp_visitor = new ExpVisitor();
		ue.accept(exp_visitor);
		
		String op = exp_visitor.rv.toString();
		if (Precedence.p(ue.e).ordinal() > Precedence.p(ue).ordinal())
			op = "(" + exp_visitor.rv.toString() + ")";
		switch(ue.op){
			case NEG:
				rv.append("-" + op);
			case NOT:
				rv.append("!" + op);
		}}
	}

	/** Emit name of variable lv. If lv exists in the
	 * local scope, call qualifiedVariableName. Otherwise
	 * it is a global. 
	 * e.g. local_Contribute_i, getGlobalInt("global_tiny_amount") */
	public void leave(Lvalue lv) {
		if(silence.isEmpty()){
		Map<Id, Type> local_scope = st.getScope(currentSession.body);
		if(local_scope.containsKey(lv.id))
			rv.append(qualifiedVariableName(lv.id));
		else{
			Type t = st.findInScope(lv.id, currentSession.body);
			if(t.equals(TypeCheckingVisitor.INT)){
				rv.append("getGlobalInt(" + "\"global_" + base + "_" + lv.id + "\")");
			}else if(t.equals(TypeCheckingVisitor.STRING)){
				rv.append("getGlobalString(" + "\"global_" + base + "_" + lv.id + "\")");
			}else{
				System.err.println("Code gen doesn't handle global vairables of type other than int and string");
			}
			}
		}
	}
	/** Emit boolean literal. */
	public void leave(BoolLiteralExp be) {
	    if (silence.isEmpty())
		rv.append(be.v ? "true" : "false");
	}
	/** Emit int literal. */
	public void leave(IntLiteralExp ie) {
	    if (silence.isEmpty())
		rv.append(ie);
	}
	/** Emit string literal. */
	public void leave(StringLiteralExp se) {
	    if (silence.isEmpty())
		rv.append(se);
	}

	public void leave(TupleLiteralExp te) {
	    // do not implement
	}
    }

    /** Helper function returning true if t is an int or bool. */
    private boolean isInt(Type t) {
	return t instanceof SimpleType && 
	    (((SimpleType)t).t == SimpleType.SimpleTypes.INT ||
	     ((SimpleType)t).t == SimpleType.SimpleTypes.BOOL);
    }
    /** Helper function returning true if t is a string. */
    private boolean isString(Type t) {
	return t instanceof SimpleType && 
	    ((SimpleType)t).t == SimpleType.SimpleTypes.STRING;
    }
}
