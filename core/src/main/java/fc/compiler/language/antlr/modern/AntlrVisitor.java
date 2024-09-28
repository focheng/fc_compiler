package fc.compiler.language.antlr.modern;

import fc.compiler.common.ast.AstNode;
import fc.compiler.common.ast.AstNodeVisitor;
import fc.compiler.common.ast.expression.Identifier;
import fc.compiler.common.ast.expression.Literal;
import fc.compiler.language.antlr.ast.*;

/**
 * @author FC
 */
public interface AntlrVisitor<P> extends AstNodeVisitor<Void, AstNode, P> {
	@Override
	default Void visit(AstNode that, P p) {
		if (that == null) return null;
		switch (that) {
			case AntlrCompilationUnit node:     visit(node, p);	break;
			case Rule node:                     visit(node, p);	break;
			case Sequence node:                 visit(node, p);	break;
			case Alternatives node:             visit(node, p);	break;
			case QuantifiedExpression node:     visit(node, p);	break;
			case Identifier node:               visit(node, p);	break;
			case Literal node:                  visit(node, p);	break;
			default: break;
		}
		return null;
	}

	default Void visit(AntlrCompilationUnit node, P p) { node.statementList().forEach(rule -> visit(rule, p) ); return null; }
	default Void visit(Rule rule, P p)          { visit(rule.expression(), p); return null; }

	default Void visit(Sequence node, P p)      { node.children().forEach(expr -> visit(expr, p) ); return null; }
	default Void visit(Alternatives node, P p)  { node.children().forEach(expr -> visit(expr, p) ); return null; }
	default Void visit(QuantifiedExpression node, P p)      { visit(node.expression(), p); return null; }

	default Void visit(Identifier node, P p)    {return null;}
	default Void visit(Literal node, P p)       {return null;}

	// -- helper methods --

	static boolean isOptional(String quantifiedType) {
		return "?".equals(quantifiedType) || "*".equals(quantifiedType);
	}

	static boolean canHaveMultiple(String quantifiedType) {
		return "+".equals(quantifiedType) || "*".equals(quantifiedType);
	}


	static String toQuantifiedType(Object p) {
		String quantifiedType = p instanceof String ? (String) p : null;
		return quantifiedType;
	}

}
