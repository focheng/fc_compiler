package fc.compiler.language.antlr.traditional;

import fc.compiler.common.ast.Expression;
import fc.compiler.common.ast.expression.*;
import fc.compiler.language.antlr.ast.*;

import java.util.ArrayList;
import java.util.List;

import static fc.compiler.language.antlr.traditional.FcgTokenKind.*;
import static fc.compiler.language.antlr.traditional.FcgTokenKindTag.*;

/**
 * @author FC
 */
public class FcgParser {
	protected FcgLexer lexer;
	protected FcgToken token;

	public FcgParser(FcgLexer lexer) {
		this.lexer = lexer;
		nextToken();
	}

	public AntlrCompilationUnit parseCompilationUnit() {
		AntlrCompilationUnit cu = new AntlrCompilationUnit();
		if (token.kind == LEXER || token.kind == PARSER) {
			cu.isLexer(token.kind == LEXER);
			nextToken();
		}

		acceptToken(GRAMMAR);
		Identifier id = parseIdentifier();
		acceptToken(SEMICOLON);
		cu.name(id);

		cu.statementList(parseRules());
		return cu;
	}

	private List<Rule> parseRules() {
		List<Rule> list = new ArrayList<>();
		while (token.kind != EOF) {
			Rule rule = parseRule();
			list.add(rule);
		}
		return list;
	}

	public Rule parseRule() {
		//parseRuleModifiers();
		Identifier name = parseIdentifier();
		//parseRuleArguments();
		//parseRuleReturn();
		//parseRuleThrow();
		acceptToken(COLON);
		Expression alternatives = parseAlternatives();
		acceptToken(SEMICOLON);
		//parseRuleSuffix();
		return new Rule().name(name).expression(alternatives);
	}

	protected Alternatives parseAlternatives() {
		Alternatives alternatives = new Alternatives();
		do {
			alternatives.add(parseSequence());
		} while (optionalToken(BAR));
		return alternatives;
	}

	protected Expression parseSequence() {
		Sequence sequence = new Sequence();
//		CompositeExpression<Expression> composite = new CompositeExpression<>();
		Expression element = null;
		while ((element = parseElement()) != null) {
			sequence.add(element);
		}
		return CompositeExpression.simplify(sequence);
	}

	protected Expression parseElement() {
		Expression expr = switch (token.kind) {
			case LEFT_PAREN     -> parseParenExpression();
			case STRING_LITERAL -> parseStringLiteral();
			case IDENTIFIER     -> parseIdentifier();
			default             -> null;
		};

		if (token.kind.isAnyOf(QUESTION, STAR, PLUS)) {
			QuantifiedExpression qe = new QuantifiedExpression().quantifierType(token.lexeme());
			nextToken();
			return qe.expression(expr);
		} else {
			return expr;
		}
	}

	protected Identifier parseIdentifier() {
		String lexeme = token.lexeme();
		acceptToken(IDENTIFIER);
		return Identifier.of(lexeme);
	}

	protected StringLiteral parseStringLiteral() {
		String lexeme = token.lexeme();
		acceptToken(STRING_LITERAL);
		return new StringLiteral(lexeme);
	}

	protected ParenthesizedExpression parseParenExpression() {
		acceptToken(LEFT_PAREN);
		Expression expr = parseAlternatives();
		acceptToken(RIGHT_PAREN);
		return new ParenthesizedExpression(expr);
	}

	protected Expression parseRuleModifiers() {
		if (optionalToken(FRAGMENT)) {
		}
		return null;
	}

	// -- token stream --

	FcgToken nextToken() {
		do {
			token = lexer.scanToken();
		} while (token.kind() == WHITE_SPACES
				|| token.kind() == NEW_LINE
				|| token.kind().tag() == COMMENT);
		return token;
	}

	FcgToken peekToken() {
		token = lexer.scanToken();
		return token;
	}

	boolean optionalToken(FcgTokenKind expected) {
		boolean accepted = token.kind() == expected;
		if (accepted)
			nextToken();
		return accepted;
	}

	boolean acceptToken(FcgTokenKind expected) {
		boolean accepted = token.kind() == expected;
		if (accepted)
			nextToken();
		else
			syntaxError("Token " + expected + " is expected");
		return accepted;
	}

	private void syntaxError(String hint) {
		logError(hint);
		nextToken();
	}

	private void logError(String hint) {
		System.out.println("syntax error: unsupported token " + token + " " + hint);
	}
}
