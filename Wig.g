grammar Wig;

@header {
package ca.uwaterloo.ece251;
import ca.uwaterloo.ece251.ast.*;
import java.util.LinkedList;
import java.util.List;
}
@lexer::header { package ca.uwaterloo.ece251; }
@lexer::members {
    boolean htmlMode = false;
    boolean inTag = false;
}

service returns [Service s]
		@init{ List<HTML> htmls = new LinkedList<HTML>();
		 List<Schema> schemas = new LinkedList<Schema>();
		 List<Variable> variables = new LinkedList<Variable>();
		 List<Function> functions = new LinkedList<Function>();
		 List<Session> sessions = new LinkedList<Session>();
		 }
		: 'service' '{' (h=html{htmls.add($h.h);})* (sc=schema{schemas.add($sc.sc);})* (v=variable{variables.add($v.v);})* 
		  (f=function{functions.add($f.f);})* (ss=session{sessions.add($ss.ss);})* '}' 
		  {$s = new Service(htmls, schemas, variables, functions, sessions);}
		;


html returns [HTML h]
		@init{
		List<HTMLBody> htmlbodies = new LinkedList<HTMLBody>();
		Id id = null;}
		: 'const' 'html' ID{id = new Id($ID.text);} '=' BEGIN_HTML 
		       (htmlbody{htmlbodies.add($htmlbody.hb);})* OPEN_SLASH END_HTML ';'
		       {$h = new HTML(id, htmlbodies);}
		;

htmlbody returns [HTMLBody hb]
		@init{
		List<Attribute> attrs = new LinkedList<Attribute>();
		List<HTMLBody> contents = new LinkedList<HTMLBody>();
		}
		: OPEN_TAG ID (attribute{attrs.add($attribute.a);})* CLOSE_TAG {$hb = new OpenHTMLBody(new Id($ID.text), attrs);}
    | OPEN_SLASH ID CLOSE_TAG {$hb = new CloseHTMLBody(new Id($ID.text));}
    | OPEN_TAG '[' ID ']' CLOSE_TAG {$hb = new PlugHTMLBody(new Id($ID.text));}
    | WHATEVER {$hb = new WhateverHTMLBody($WHATEVER.text);}
    | META {$hb = new MetaHTMLBody($META.text);}
    | OPEN_TAG 'input' (attribute{attrs.add($attribute.a);})* CLOSE_TAG {$hb = new InputHTMLBody(attrs);}
    | OPEN_TAG 'select' (attribute{attrs.add($attribute.a);})* CLOSE_TAG (hb2=htmlbody{contents.add($hb2.hb);})* OPEN_SLASH 'select' CLOSE_TAG
    {$hb = new SelectHTMLBody(attrs, contents);}
    ;
/* Handle these as just plain attributes! */
/*inputattr : 'name' '=' attr
          | 'type' '=' inputtype
          | attribute
;
inputtype : 'text' | 'radio'
;*/
attribute returns [Attribute a]
		: attr {$a = new KeyOnlyAttribute($attr.attr);}
		| at1=attr '=' at2=attr {$a = new KeyValueAttribute($at1.attr, $at2.attr);}
		;

attr returns [Attr attr] 
    : ID {$attr = new IdAttr(new Id($ID.text));}| STRING_LITERAL {$attr = new StringAttr($STRING_LITERAL.text.substring(1, $STRING_LITERAL.text.lastIndexOf('"')));}
    ;

schema returns [Schema sc]
		@init { List<WigField> fl = new LinkedList<WigField>(); } 
		: 'schema' ID '{' (f=field { fl.add($f.f); })* '}'
		  { $sc = new Schema(new Id($ID.text), fl); }
		;

field returns [WigField f] 
    : simpletype ID ';' {$f = new WigField($simpletype.st, new Id($ID.text));}
    ;

variable returns [Variable v]
    : type identifiers ';' {$v = new Variable($type.t, $identifiers.idents);}
    ;

identifiers returns [List<Id> idents]
		@init { List<Id> ids = new LinkedList<Id>(); }
		: id1=ID {ids.add(new Id($id1.text));} (',' id2=ID {ids.add(new Id($id2.text));})* {$idents = ids;}
		;

simpletype returns [SimpleType st]
    : 'int' {$st = new SimpleType(SimpleType.SimpleTypes.INT);}
		| 'bool'{$st = new SimpleType(SimpleType.SimpleTypes.BOOL);}| 'string'{$st = new SimpleType(SimpleType.SimpleTypes.STRING);}
		| 'void'{$st = new SimpleType(SimpleType.SimpleTypes.VOID);}
		;

type returns [Type t]
    : simpletype {$t = $simpletype.st;}| 'tuple' ID {$t = new TupleType(new Id($ID.text));}
    ;

function returns [Function f]
    : type ID '(' arguments? ')' compoundstm
		{$f = new Function($type.t, new Id($ID.text), $arguments.argtypes, $arguments.argids, $compoundstm.cs);}
		;
		
arguments returns [List<Type> argtypes, List<Id> argids]
		@init { List<Type> types = new LinkedList<Type>(); 
		List<Id> ids = new LinkedList<Id>(); }
		: arg1=argument{types.add($arg1.t); ids.add($arg1.i);} (',' arg2=argument{types.add($arg2.t); ids.add($arg2.i);})* 
		{$argtypes = types; $argids = ids;}
		;
		
argument returns[Type t, Id i]
    : type ID {$t = $type.t; $i = new Id($ID.text);}
    ;

session returns [Session ss]
    : 'session' ID '(' ')' compoundstm {$ss = new Session(new Id($ID.text), $compoundstm.cs);}
    ;

stm returns [Stm s]: ';' {$s = new EmptyStm();}
    | 'show' document receive ';' {$s = new ShowStm($document.d, $receive.receives);}
    | 'exit' document ';' {$s = new ExitStm($document.d);}
    | 'return' ';' {$s = new ReturnVoidStm();}
    | 'return' exp ';' {$s = new ReturnStm($exp.e);}
    | 'if' '(' exp ')' s2=stm (('else')=> 'else' s3=stm)? {$s = new IfStm($exp.e, $s2.s, $s3.s);}
    | 'while' '(' exp ')' s2=stm {$s = new WhileStm($exp.e, $s2.s);}
    | compoundstm {$s = $compoundstm.cs;} 
    | exp ';' {$s = new EvalStm($exp.e);}
    ;
    
document returns [Document d]: ID {$d = new BaseDocument(new Id($ID.text));}
    | 'plug' ID '[' plugs ']' {$d = new PlugDocument(new Id($ID.text), $plugs.plugids, $plugs.plugcontents);}
    ;

receive returns [List<Input> receives]: /* empty */ {$receives = null;}
    | 'receive' '[' inputs? ']' {$receives = $inputs.li;} /*WE MAY NEED SPECIAL HANDLING FOR inputs == null*/
    ;
compoundstm returns [CompoundStm cs]
		@init { List<Variable> variables = new LinkedList<Variable>(); 
		List<Stm> body = new LinkedList<Stm>();
		}
		: '{' (variable{variables.add($variable.v);})* (stm{body.add($stm.s);})* '}' {$cs = new CompoundStm(variables, body);}
		;
plugs returns [List<Id> plugids, List<Exp> plugcontents]
		@init { List<Id> ids = new LinkedList<Id>(); 
		List<Exp> contents = new LinkedList<Exp>();
		}
		: p1=plug{ids.add($p1.id); contents.add($p1.e);} (',' p2=plug{ids.add($p2.id); contents.add($p2.e);})*
		{$plugids = ids; $plugcontents = contents;}
		;

plug returns [Id id, Exp e]
    : ID '=' exp {$id = new Id($ID.text); $e = $exp.e;}
    ;

inputs returns [List<Input> li] 
    @init { $li = new LinkedList<Input>(); } 
    : i0=input { $li.add($i0.i); }  (',' in=input { $li.add($in.i); } )*
    ;

input returns [Input i]
    : lvalue '=' ID {$i = new Input($lvalue.l, new Id($ID.text));}
    ;

exp_atom returns [Exp e]
    : ID '(' exps? ')'
      {$e = new CallExp(new Id($ID.text), $exps.eps);} 
    | lvalue
      {$e = $lvalue.l;}
    | '(' exp ')' {$e=$exp.e;} 
    | INT_LITERAL       
      {$e = new IntLiteralExp(Integer.parseInt($INT_LITERAL.text));} 
    | STRING_LITERAL           
      {$e = new StringLiteralExp($STRING_LITERAL.text.substring(1, $STRING_LITERAL.text.lastIndexOf('"')));}
    | 'true' {$e = new BoolLiteralExp(true);} |'false' {$e = new BoolLiteralExp(false);}
    | 'tuple''{' fieldvalues? '}'
      {$e = new TupleLiteralExp($fieldvalues.ids, $fieldvalues.exps);}
    ;

exp_binary_operand returns [Exp e]
    : r=exp_atom
      {$e = $r.e;}
    | '!' r=exp_binary_operand   
      {$e = new UnopExp(UnopExp.Unop.NOT, $r.e);}
    | '-' r=exp_binary_operand    
      {$e = new UnopExp(UnopExp.Unop.NEG, $r.e);}
    ;
    
exp_ident_term returns [Exp e]
@init{TupleopExp.Tupleop op = null;}
  : l=exp_binary_operand {$e = $l.e;} 
  (('\\+' {op = TupleopExp.Tupleop.TUPLE_PLUS;}| '\\-' {op = TupleopExp.Tupleop.TUPLE_MINUS;} ) '(' r=identifiers ')'
  {$e = new TupleopExp(op, $e, $r.idents);})*
  ;
 
exp_shift_term returns [Exp e]
    : l=exp_ident_term {$e = $l.e;} ('<<' r=exp_ident_term {$e = new BinopExp(BinopExp.Binop.TUPLE_FROM, $e, $r.e);})*
    ;
    
exp_binary_factor returns [Exp e]
@init{BinopExp.Binop op = null;}
    : l=exp_shift_term {$e = $l.e;} (('*'{op = BinopExp.Binop.TIMES;}|'/'{op = BinopExp.Binop.DIV;}|'%'{op = BinopExp.Binop.MOD;})
    r=exp_shift_term {$e = new BinopExp(op, $e, $r.e);})* 
    ;

exp_binary_term returns [Exp e]
@init{BinopExp.Binop op = null;}
    : l=exp_binary_factor{$e = $l.e;} (('+' {op = BinopExp.Binop.PLUS;}| '-' {op = BinopExp.Binop.MINUS;}) 
    r=exp_binary_factor{$e = new BinopExp(op, $e, $r.e);})*
    ;   

exp_relation_op returns [Exp e]
@init{BinopExp.Binop op = null;}
    : l=exp_binary_term {$e = $l.e;} (('=='{op = BinopExp.Binop.EQ;}|'!='{op = BinopExp.Binop.NEQ;}|'<'{op = BinopExp.Binop.LT;}
    |'>'{op = BinopExp.Binop.GT;}|'<='{op = BinopExp.Binop.LE;}|'>='{op = BinopExp.Binop.GE;})
    r=exp_binary_term {$e = new BinopExp(op, $e, $r.e);})*
    ;

exp_amp_term returns [Exp e]
    : l=exp_relation_op {$e = $l.e;} ('&&' r=exp_relation_op {$e = new BinopExp(BinopExp.Binop.AND, $e, $r.e);})*  
    ;

exp_or_term returns [Exp e]
    : l=exp_amp_term {$e = $l.e;} ('||' r=exp_amp_term {$e = new BinopExp(BinopExp.Binop.OR, $e, $r.e);})*
    ;
   
exp returns [Exp e] 
    : e1=exp_or_term {$e = $e1.e;}
    | l=lvalue '=' e2=exp {$e = new AssignExp($l.l, $e2.e);}
    ;
    
exps returns [List<Exp> eps] 
		@init { List<Exp> expressions = new LinkedList<Exp>(); } 
		: e1=exp {expressions.add($e1.e);}(',' e2=exp {expressions.add($e2.e);})*
		{$eps = expressions;}
		;
		
lvalue returns [Lvalue l] 
    : ID {$l = new Lvalue(null, new Id($ID.text));}
		| qualifier=ID '.' id=ID {$l = new Lvalue(new Id($qualifier.text), new Id($id.text));}
		;
		
fieldvalues returns [List<Id> ids, List<Exp> exps]
		@init { $ids = new LinkedList<Id>(); $exps = new LinkedList<Exp>();}
		:f1=fieldvalue{$ids.add($f1.id); $exps.add($f1.e);} (',' f2=fieldvalue{$ids.add($f2.id); $exps.add($f2.e);})*
		;
		
fieldvalue returns [Id id, Exp e]
    : ID '=' exp {$id = new Id($ID.text); $e = $exp.e;}
    ;

/*TOKENS:*/

ID : ('a'..'z'|'A'..'Z')('a'..'z'|'A'..'Z'|'0'..'9'|'_')*;
INT_LITERAL : '0'..'9'+;
STRING_LITERAL : '"' (~'"')* '"';
fragment NEWLINE: '\r'? '\n' {/*newline();*/};
WS : (' '|'\t'| NEWLINE)+ { $channel = HIDDEN; };
BEGIN_HTML : {!htmlMode}?=> '<html>' { htmlMode = true; inTag = false; };
OPEN_TAG : {htmlMode}?=> '<' { inTag = true; };
OPEN_SLASH : {htmlMode}?=> OPEN_TAG '/';
CLOSE_TAG : {htmlMode}?=> '>' { inTag = false; };
META : {htmlMode}?=> OPEN_TAG '!--' ( options { greedy=false; } : . )* '--' CLOSE_TAG;
WHATEVER : {htmlMode && !inTag}?=> (~('<'))*;
END_HTML : {htmlMode && inTag}?=> 'html' CLOSE_TAG { htmlMode = false; };
