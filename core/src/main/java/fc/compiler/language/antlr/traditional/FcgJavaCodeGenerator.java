package fc.compiler.language.antlr.traditional;

import fc.compiler.common.ast.AstNode;
import fc.compiler.common.ast.AstNodeVisitor;
import fc.compiler.common.ast.Expression;
import fc.compiler.common.ast.expression.Identifier;
import fc.compiler.common.ast.expression.ParenthesizedExpression;
import fc.compiler.common.ast.expression.StringLiteral;
import fc.compiler.common.util.StrUtils;
import fc.compiler.language.antlr.ast.*;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static fc.compiler.language.antlr.traditional.UniqueTokenKindFinder.*;

/**
 * @author FC
 */
@Setter @Accessors(fluent = true, chain = true)
public class FcgJavaCodeGenerator implements AstNodeVisitor<Void, AstNode, Object> {
	// -- options --
	private String packageName;

	// -- variables from antlr file --
	private String languageName;
	private boolean caseSensitive;

	// -- specific builders --
	private ParserBuilder pb = new ParserBuilder();
	private TokenKindBuilder kb = new TokenKindBuilder();
	// LexerBuilder lb;
	// VisitorBuilder vb;
	// AstNodeClassBuilder cb;
	private UniqueTokenKindFinder finder = new UniqueTokenKindFinder();

	// -- --
	private Map<String, Rule> rules = new HashMap<>();

	public void visit(AntlrCompilationUnit cu) {
		this.languageName = cu.name().id();
		pb.buildFileHeader("foo", languageName);
		cu.rules().forEach(rule -> rules.put(rule.name().id(), rule));
		finder.kb(kb).rules(rules);
		cu.rules().forEach(this::visit);
	}

	public void visit(Rule rule) {
		if (rule.fragment()) {
			// TODO:
		}
		if (rule.expression() == null) {
			pb.buildEmptyParseMethod(rule);
		} else {
			UniqueTokenKinds kinds = finder.getUniqueTokenKinds(rule.name().id());
			pb.buildParseMethodStart(rule, kinds.toString());
			visit(rule.expression());
			pb.buildParseMethodEnd(rule);
		}
	}

	public void visit(Expression expr) {
		switch (expr) {
			case Identifier                 node:   visit(node);    break;
			case StringLiteral              node:   visit(node);    break;
			case Alternatives               node:   visit(node);    break;
			case Sequence                   node:   visit(node);    break;
			case ParenthesizedExpression    node:   visit(node);    break;
			case QuantifiedExpression       node:   visit(node);    break;
			default:    break;
		}
	}

	/**
	 * cases:
	 * - only keywords/literal. idDivision : (IDENTIFICATION | ID) DIVISION '.'
	 *                          => acceptAnyToken(IDENTIFICATION, ID);
	 * - nested rules:          statement: ifStatement | forStatement | returnStatement
	 * - keywords & multiple:   thenStatement : NEXT SENTENCE | statement*
	 */
	public void visit(Alternatives alternatives) {
		if (isAllTokens(alternatives)) {
			pb.buildAcceptAnyToken(getAllTokens(alternatives));
		} else {
			alternatives.children().forEach(this::visit);
		}
	}

	public void visit(Sequence sequence) {
		sequence.children().forEach(this::visit);
	}

	public void visit(ParenthesizedExpression pe) {
		visit(pe.expression());
	}

	public void visit(QuantifiedExpression qe) {
		if (qe.expression() instanceof Identifier id) {
			visit(id, Quantifier.of(qe.quantifierType()), false);
		} else {
			System.out.println(qe.expression());
		}
	}

	public void visit(Identifier node) {
		visit(node, Quantifier.EXACTLY_ONE, false);
	}

	public void visit(Identifier node, Quantifier quantifier, boolean returnIdentifier) {
		if (rules.containsKey(node.id())) {
			if (quantifier.isMultiple()) {
				pb.buildStatementParseMultiples(node.id());
				pb.buildMethodParseMultiple(node.id());
			} else {
				pb.buildParseAndSet(node.id());
			}
		}
		// All uppercase id is lexer rule.
		else if (isLexerRule(node.id())) {
			if (quantifier.isOptional()) {
				pb.buildOptionalToken(node.id());
			} else {
				pb.buildAcceptToken(node.id());
			}
		} else if (returnIdentifier) {
			pb.buildParseReturnedIdentifier();
		} else {
			pb.buildParseIdentifier(node.id());
		}
	}

	public void visit(StringLiteral node) {
		visit(node, Quantifier.EXACTLY_ONE);
	}

	public void visit(StringLiteral node, Quantifier quantifier) {
		String kind = kb.getTokenKind(node.value());
		if (kind != null) {
			if (quantifier.isOptional()) {
				pb.buildOptionalToken(kind);
			} else {
				pb.buildAcceptToken(kind);
			}
		} else {
			pb.buildParseStringLiteral();
		}
	}

	@Override
	public Void visit(AstNode node2, Object o) {
		switch (node2) {
			case Identifier             node:   visit(node);   break;
			case StringLiteral          node:   visit(node);   break;
			case Expression             node:   visit(node);   break;
			case Rule                   node:   visit(node);   break;
			case AntlrCompilationUnit   node:   visit(node);   break;
			default:    break;
		}
		return null;
	}

	/**
	 * "COMMA", "PROGRAM_ID". except "IDENTIFIER"
	 */
	protected boolean isLexerRule(String ruleName) {
		return StrUtils.isAllUpperCase(ruleName) && !"IDENTIFIER".equals(ruleName);
	}

	private boolean isAllTokens(Alternatives alternatives) {
		return alternatives.children().stream().allMatch(expr ->
				expr instanceof Identifier id && !rules.containsKey(id.id())
						|| (expr instanceof StringLiteral sl && kb.getTokenKind(sl.value()) != null)
		);
	}

	private String getAllTokens(Alternatives alternatives) {
		return alternatives.children().stream().map(expr -> {
			if (expr instanceof Identifier id && !rules.containsKey(id.id())) {
				return id.id();
			} else if (expr instanceof StringLiteral stringLiteral) {
				String tokenKind = kb.getTokenKind(stringLiteral.value());
				if (tokenKind != null)
					return tokenKind;
			}
			return "";
		}).collect(Collectors.joining(", "));
	}

	@Override
	public String toString() {
		return pb.toString();
	}
}
