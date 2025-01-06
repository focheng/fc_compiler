package fc.compiler.language.antlr.modern;

import fc.compiler.common.ast.expression.CompositeExpression;
import fc.compiler.common.ast.Expression;
import fc.compiler.common.ast.expression.*;
import fc.compiler.common.ast.expression.Identifier;
import fc.compiler.common.parser.StringTokenParserBase;
import fc.compiler.common.parser.StringTokenParserRegistry;
import fc.compiler.common.parser.StringTokenReader;
import fc.compiler.common.token.StringToken;
import fc.compiler.language.antlr.ast.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import static fc.compiler.common.token.StringTokenKind.*;

/**
 * Antlr Extension Parser.
 *  - Support attribute after rule name. e.g. NONNUMERICLITERAL [type=literal]
 *
 *     Usage               Notation
 *  +--------------------------------------+
 *     definition          '='
 *     concatenation       ,
 *     termination         ;
 *     alternation         |
 *     optional            [ ... ]
 *     repetition          { ... }
 *     grouping            ( ... )
 *     terminal string     " ... "
 *     terminal string     ' ... '
 *     comment             (* ... *)
 *     special sequence    ? ... ?
 *     exception           -
 *
 * @author FC
 */
@Slf4j
public class AntlrParser extends StringTokenParserBase {
	public static StringTokenParserRegistry initRegistry() {
		StringTokenParserRegistry map = new StringTokenParserRegistry();

//		map.put(ERROR, ParserBase::ignore);

		map.put(LINE_TERMINATOR, StringTokenParserBase::ignore);
		map.put(WHITE_SPACES, StringTokenParserBase::ignore);
		map.put(LINE_COMMENT, StringTokenParserBase::ignore);

		map.put(IDENTIFIER, AntlrParser::parsePostfixExpression);
		map.put(STRING_LITERAL, AntlrParser::parsePostfixExpression);
		map.put(NUMBER_LITERAL, AntlrParser::parsePostfixExpression);
		map.put(LEFT_PAREN, AntlrParser::parsePostfixExpression);
//		map.put(LEFT_BRACKET, AntlrParser::onLeftBracket);
//		map.put(LEFT_BRACE, AntlrParser::onLeftBrace);

		return map;
	}

	public static AntlrCompilationUnit parseCompilationUnit(StringTokenReader reader, StringTokenParserRegistry registry) {
		AntlrCompilationUnit unit = new AntlrCompilationUnit();
		StringToken token = reader.optionalAnyOfAndReturn(AntlrKeywords.LEXER, AntlrKeywords.PARSER);
		if (token != null)
			unit.isLexer(token.kind() == AntlrKeywords.LEXER);

		unit.name(parseGrammar(reader, registry));
		unit.statementList(parseRules(reader, registry));

		return unit;
	}

	private static Identifier parseGrammar(StringTokenReader reader, StringTokenParserRegistry registry) {
		reader.accept(AntlrKeywords.GRAMMAR);
		Identifier id = parseIdentifier(reader, registry);
		reader.accept(SEMICOLON);
		return id;
	}

	public static List<Rule> parseRules(StringTokenReader reader, StringTokenParserRegistry registry) {
		List<Rule> rules = new ArrayList<>();
		while (reader.isKindNextTokens(IDENTIFIER, COLON)) {
			rules.add(parseRule(reader, registry));
		}
		return rules;
	}

	public static Rule parseRule(StringTokenReader reader, StringTokenParserRegistry registry) {
		Identifier name = parseIdentifier(reader, registry);
		reader.accept(COLON);
		Expression choices = parseAlternativesExpression(reader, registry);
		reader.accept(SEMICOLON);
		return new Rule().name(name).expression(choices);
	}

	private static Expression simplifyRecursively(Expression expr) {
		Expression simplified = expr;
		while (true) {
			if (simplified != null && simplified instanceof CompositeExpression composite) {
				simplified = CompositeExpression.simplify(composite);
				if (simplified != composite) {
					continue;
				}
			}
			break;
		}
		return simplified;
	}

//	private static Expression simplify(CompositeExpression<Expression> composite) {
//		if (composite == null || composite.children() == null || composite.children().isEmpty())
//			return null;
//		else if (composite.children().size() == 1)
//			return composite.children().get(0);
//		else
//			return composite;
//	}

	public static Expression parseAlternativesExpression(StringTokenReader reader, StringTokenParserRegistry registry) {
		Alternatives alternatives = new Alternatives();
		List<Expression> children = parseExpressionListOneOrMore(reader, registry,
				BAR, AntlrParser::parseSequenceExpression);
		alternatives.children(children);
		return simplifyRecursively(alternatives);
	}

	public static Expression parseSequenceExpression(StringTokenReader reader, StringTokenParserRegistry registry) {
		Sequence sequence = new Sequence();
		List<Expression> children = new ArrayList<>();
		while (reader.isKindAnyOf(IDENTIFIER, LEFT_PAREN, LEFT_BRACKET, LEFT_BRACE, STRING_LITERAL, NUMBER_LITERAL)) {
			children.add(parseExpression(reader, registry));
		}
		sequence.children(children);
		return simplifyRecursively(sequence);
	}

	public static Expression parsePostfixExpression(StringTokenReader reader, StringTokenParserRegistry registry) {
		Expression expr = parsePrimaryExpression(reader, registry);
		return quantify(reader, registry, expr);
	}

	public static Expression quantify(StringTokenReader reader, StringTokenParserRegistry registry,
	                                  Expression expression) {
		if (reader.optional(QUESTION)) {
			return new QuantifiedExpression().quantifierType("?").expression(expression);
		} else if (reader.optional(PLUS)) {
			return new QuantifiedExpression().quantifierType("+").expression(expression);
		} else if (reader.optional(STAR)) {
			return new QuantifiedExpression().quantifierType("*").expression(expression);
		} else {
			return expression;
		}
	}

	public static Expression parsePrimaryExpression(StringTokenReader reader, StringTokenParserRegistry registry) {
		if (reader.isKind(IDENTIFIER)) {
			return parseIdentifier(reader, registry);
		} else if (reader.isKind(STRING_LITERAL)) {
			return parseLiteral(reader, registry);
		} else if (reader.isKind(NUMBER_LITERAL)) {
			return parseLiteral(reader, registry);
		} else if (reader.isKind(LEFT_PAREN)) {
			return parseEnclosedExpression(reader, registry,
					LEFT_PAREN, RIGHT_PAREN, AntlrParser::parseAlternativesExpression);
		} else {
			return syntaxError(reader, "Unsupported primary expression");
		}
	}

	public static ParenthesizedExpression parseParenExpression(StringTokenReader reader, StringTokenParserRegistry registry) {
		reader.acceptAnyOf(LEFT_PAREN);
		Expression expr = parseAlternativesExpression(reader, registry);  //
		reader.acceptAnyOf(RIGHT_PAREN);
		return new ParenthesizedExpression(expr);
	}

}
