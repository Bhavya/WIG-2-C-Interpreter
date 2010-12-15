package ca.uwaterloo.ece251;
import org.antlr.runtime.*;
import java.io.*;
import java.util.Arrays;
import ca.uwaterloo.ece251.symbol.SymbolDefVisitor;
import ca.uwaterloo.ece251.symbol.SymbolUseVisitor;
import ca.uwaterloo.ece251.symbol.SymbolTable;
import ca.uwaterloo.ece251.type.TypeCheckingVisitor;
import ca.uwaterloo.ece251.codegen.CodeGenVisitor;

/** Main entry point. */
public class WIG {
    public static void main(String[] args) throws Exception {
	String fname = null, base = null, baseurl = null, out = null;
	boolean dumpSymbols = false, dumpVerbatim = false;

	while (true) {
	    if (args[0].equals("-symbol")) {
		dumpSymbols = true;
	    } else if (args[0].equals("-verbatim")) {
		dumpVerbatim = true;
	    } else if (args[0].equals("-baseurl") && args.length > 1) {
		baseurl = args[1];
		args = Arrays.copyOfRange(args, 1, args.length);
	    } else if (args[0].equals("-o") && args.length > 1) {
		out = args[1];
		args = Arrays.copyOfRange(args, 1, args.length);
	    } else
		break;
	    args = Arrays.copyOfRange(args, 1, args.length);
	}
	fname = args[0];

	if (fname.lastIndexOf('.') > 0)
	    base = fname.substring(0, fname.lastIndexOf('.'));
	else
	    base = fname;

	if (base.indexOf(File.separator) > 0)
	    base = base.substring(base.lastIndexOf(File.separator)+1);

	if (baseurl == null)
	    baseurl = "http://localhost/~plam/cgi-bin/"+base;

	if (out == null)
	    out = base+".c";

        ANTLRFileStream input = new ANTLRFileStream(fname);
        WigLexer lexer = new WigLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        WigParser parser = new WigParser(tokens);
	ca.uwaterloo.ece251.ast.Service s = parser.service();

	SymbolDefVisitor sdv = new SymbolDefVisitor();
	SymbolUseVisitor suv = new SymbolUseVisitor();
	s.accept(sdv); s.accept(suv);
	TypeCheckingVisitor tv = new TypeCheckingVisitor();
	s.accept(tv);

	if (Error.errors)
	    System.exit(-1);

	if (dumpSymbols)
	    System.out.println(SymbolTable.v().dump());
	else if (dumpVerbatim) 
	    System.out.print(s.toString());
	else {
	    try {
		PrintWriter fw = new PrintWriter(new FileWriter(new File(out)));
		CodeGenVisitor cv = new CodeGenVisitor(fw, base, baseurl, tv);
		s.accept(cv);
	    } catch (IOException e) {}
	}
    }
}
