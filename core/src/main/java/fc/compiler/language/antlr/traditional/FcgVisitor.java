package fc.compiler.language.antlr.traditional;

import fc.compiler.common.ast.AstNode;
import fc.compiler.common.ast.AstNodeVisitor;
import fc.compiler.common.ast.Expression;
import fc.compiler.common.ast.expression.Identifier;
import fc.compiler.common.ast.expression.Literal;
import fc.compiler.common.ast.expression.ParenthesizedExpression;
import fc.compiler.common.ast.expression.StringLiteral;
import fc.compiler.common.util.StrUtils;
import fc.compiler.language.antlr.ast.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

/**
 * @author FC
 */
@Getter @Setter @Accessors(fluent = true, chain = true)
public class FcgVisitor<P> implements AstNodeVisitor<Void, AstNode, P> {
	protected Map<String, Rule> rules = new HashMap<>();

	@Override
	public Void visit(AstNode that, P p) {
		switch (that) {
			case AntlrCompilationUnit node:     visit(node, p);	break;
			case Rule node:                     visit(node, p);	break;
			case Sequence node:                 visit(node, p);	break;
			case Alternatives node:             visit(node, p);	break;
			case QuantifiedExpression node:     visit(node, p);	break;
			case Identifier node:               visit(node, p);	break;
			case StringLiteral node:            visit(node, p);	break;
			default: break;
		}
		return null;
	}

	public void visit(AntlrCompilationUnit cu, P p) {
		cu.rules().forEach(rule -> rules.put(rule.name().id(), rule));
		cu.rules().forEach(expr -> visit(expr, p));
	}

	public void visit(Rule rule, P p) {
		visit(rule.expression(), p);
	}

	public void visit(Expression expr, P p) {
		switch (expr) {
			case Identifier                 node:   visit(node, p);     break;
			case StringLiteral              node:   visit(node, p);     break;
			case Alternatives               node:   visit(node, p);     break;
			case Sequence                   node:   visit(node, p);     break;
			case ParenthesizedExpression    node:   visit(node, p);     break;
			case QuantifiedExpression       node:   visit(node, p);     break;
			default:                                                    break;
		}
	}

	public void visit(Alternatives alternatives, P p) {
		alternatives.children().forEach(expr -> visit(expr, p));
	}

	public void visit(Sequence sequence, P p) {
		sequence.children().forEach(expr -> visit(expr, p));
	}

	public void visit(ParenthesizedExpression pe, P p) {
		visit(pe.expression(), p);
	}

	public void visit(QuantifiedExpression qe, P p) {
		if (qe.expression() instanceof Identifier id) {
			visit(id, p, Quantifier.of(qe.quantifierType()), false);
		} else {
			System.out.println(qe.expression());
		}
	}

	public void visit(Identifier node, P p) {
		visit(node, p, Quantifier.EXACTLY_ONE, false);
	}

	public void visit(StringLiteral node, P p) {
		visit(node, p, Quantifier.EXACTLY_ONE);
	}

	public void visit(Identifier node, P p, Quantifier quantifier, boolean returnIdentifier) {
	}

	public void visit(StringLiteral node, P p, Quantifier quantifier) {
	}

	// -- common helpers --
	/**
	 * "COMMA", "PROGRAM_ID". except "IDENTIFIER"
	 */
	protected boolean isLexerRule(String ruleName) {
		return StrUtils.isAllUpperCase(ruleName) && !"IDENTIFIER".equals(ruleName);
	}

}
