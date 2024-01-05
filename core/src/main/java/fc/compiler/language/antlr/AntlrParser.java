package fc.compiler.language.antlr;

import fc.compiler.common.ast.Expression;
import fc.compiler.common.ast.expression.*;
import fc.compiler.common.parser.ParserBase;
import fc.compiler.common.parser.ParserRegistry;
import fc.compiler.common.parser.TokenReader;
import fc.compiler.common.token.Token;
import fc.compiler.language.antlr.ast.AntlrCompilationUnit;
import fc.compiler.language.antlr.ast.QuantifiedExpression;
import fc.compiler.language.antlr.ast.Rule;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import static fc.compiler.common.token.TokenKind.*;
import static fc.compiler.language.antlr.AntlrKeywords.*;

/**
 * @author FC
 */
@Slf4j
public class AntlrParser extends ParserBase {
	public static ParserRegistry initRegistry() {
		ParserRegistry map = new ParserRegistry();

//		map.put(ERROR, ParserBase::ignore);

		map.put(LINE_TERMINATOR, ParserBase::ignore);
		map.put(WHITE_SPACES, ParserBase::ignore);
		map.put(LINE_COMMENT, ParserBase::ignore);

		map.put(IDENTIFIER, AntlrParser::onIdentifier);
		map.put(LEFT_PAREN, AntlrParser::onLeftParen);
//		map.put(LEFT_BRACKET, AntlrParser::onLeftBracket);
//		map.put(LEFT_BRACE, AntlrParser::onLeftBrace);

		return map;
	}

	public static AntlrCompilationUnit parseCompilationUnit(TokenReader reader, ParserRegistry registry) {
		AntlrCompilationUnit unit = new AntlrCompilationUnit();
		Token token = reader.optionalAnyOfAndReturn(LEXER, PARSER);
		if (token != null)
			unit.isLexer(token.kind() == LEXER);

		reader.accept(GRAMMAR);
		unit.name(parseIdentifier(reader, registry));
		reader.accept(SEMICOLON);

		List<Rule> rules = parseRules(reader, registry);
		unit.statements(rules);

		return unit;
	}

	public static List<Rule> parseRules(TokenReader reader, ParserRegistry registry) {
		List<Rule> rules = new ArrayList<>();
		while (reader.isKindNextTokens(IDENTIFIER, COLON)) {
			rules.add(parseRule(reader, registry));
		}
		return rules;
	}

	public static Rule parseRule(TokenReader reader, ParserRegistry registry) {
		Identifier name = parseIdentifier(reader, registry);
		reader.accept(COLON);
		List<Expression> choices = parseExpressionChoices(reader, registry);
		reader.accept(SEMICOLON);
		return new Rule().name(name).choices(choices);
	}

	public static List<Expression> parseExpressionChoices(TokenReader reader, ParserRegistry registry) {
		List<Expression> choices = parseExpressionListOneOrMore(reader, registry,
				BAR, AntlrParser::parseExpressionSequence);
		return choices;
	}

	public static CompositeExpression<Expression> parseExpressionSequence(TokenReader reader, ParserRegistry registry) {
		List<Expression> sequence = new ArrayList<>();
		while (!reader.isKindAnyOf(SEMICOLON, BAR, EOF, ERROR)) {
			sequence.add(parseExpression(reader, registry));
		}
		return new CompositeExpression<>().expressions(sequence);
	}

	public static Expression onIdentifier(TokenReader reader, ParserRegistry registry) {
		Token token = reader.acceptAnyOfAndReturn(IDENTIFIER);
		if (token == null) {
			return syntaxError(reader, "failed to parse identifier");
		}

		Identifier id = new Identifier().id(token.lexeme());
		return quantify(reader, registry, id);
	}

	public static Expression quantify(TokenReader reader, ParserRegistry registry,
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

	public static Expression onLeftParen(TokenReader reader, ParserRegistry registry) {
		reader.acceptAnyOf(LEFT_PAREN);
		Expression expr = parseBinaryExpression(reader, registry);  //
		reader.acceptAnyOf(RIGHT_PAREN);
		return quantify(reader, registry, new ParenthesizedExpression().expression(expr));
	}

	public static Expression onLeftBracket(TokenReader reader, ParserRegistry registry) {
		Expression expr = parseEnclosedExpression(reader, registry, LEFT_BRACKET, RIGHT_BRACKET);
		return quantify(reader, registry, new ParenthesizedExpression().expression(expr));
	}

	public static Expression onLeftBrace(TokenReader reader, ParserRegistry registry) {
		Expression expr = parseEnclosedExpression(reader, registry, LEFT_BRACE, RIGHT_BRACE);
		return quantify(reader, registry, new ParenthesizedExpression().expression(expr));
	}

	public static Expression parseBinaryExpression(TokenReader reader, ParserRegistry registry) {
		Expression expr = parsePrimaryExpression(reader, registry);
		Token t = reader.optionalAnyOfAndReturn(BAR);
		if (t == null)
			return expr;

		BinaryExpression binaryExpression = new BinaryExpression().leftOperand(expr);
		binaryExpression.operator(t.lexeme());
		binaryExpression.rightOperand(parsePrimaryExpression(reader, registry));
		return binaryExpression;
	}

	public static Expression parsePrimaryExpression(TokenReader reader, ParserRegistry registry) {
		if (reader.isKind(IDENTIFIER)) {
			return parseIdentifier(reader, registry);
		} else if (reader.isKind(STRING_LITERAL)) {
			Token t = reader.optionalAnyOfAndReturn(STRING_LITERAL);
			return new Literal<String>().value(t.lexeme());
		} else if (reader.isKind(NUMBER_LITERAL)) {
			Token t = reader.optionalAnyOfAndReturn(NUMBER_LITERAL);
			return new Literal<String>().value(t.lexeme());
		} else if (reader.isKind(LEFT_PAREN)) {
			return parseParenExpression(reader, registry);
		}
		return syntaxError(reader, "Unsupported primary expression");
	}
}
