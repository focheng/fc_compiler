package fc.compiler.common.parser;

import fc.compiler.common.ast.AstNode;
import fc.compiler.common.ast.Expression;
import fc.compiler.common.ast.Statement;
import fc.compiler.common.ast.expression.*;
import fc.compiler.common.ast.statement.ExpressionStatement;
import fc.compiler.common.ast.statement.ForStatement;
import fc.compiler.common.ast.statement.IfStatement;
import fc.compiler.common.token.Token;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import static fc.compiler.common.token.TokenKind.*;

/**
 * Parser base class to be derived for specific language.
 *
 * Compilation-Unit: Statement* | Variable-Declaration* | Function-Declaration* | Type-Declaration*
 *
 * Statement:
 *        Expression-Statement
 *      | If-Statement
 *      | Switch-Statement
 *      | For-Statement
 *      | While-Statement
 *      | Do-While-Statement
 *      | Go-To-Statement
 *      | Return-Statement
 *      | Break-Statement
 *      | Continue-Statement
 *
 * Comma-Expression: Expression [, Expression]*
 * Expression:
 *        Assignment-Expression
 *      | Lambda-Expression
 * Assignment-Expression: Ternary-Expression | LeftHandSide AssignmentOperator Expression
 * Ternary-Expression: Binary-Expression ? Expression : Ternary-Expression
 * Binary-Expression:
 *      Logical-Or-Expression:	   Logical-And-Expression	 [ '||' Logical-And-Expression ]*
 *      Logical-And-Expression:	   Bitwise-Or-Expression	 [ '&&' Bitwise-Or-Expression  ]*
 *      Bitwise-Or-Expression:	   Bitwise-Xor-Expression	 [ '|'  Bitwise-Xor-Expression ]*
 *      Bitwise-Xor-Expression:	   Bitwise-And-Expression	 [ '^'  Bitwise-And-Expression ]*
 *      Bitwise-And-Expression:	   Equality-Expression		 [ '&'  Equality-Expression ]*
 *      Equality-Expression:	   Relational-Expression	 [ ('==' | '!=') Relational-Expression ]*
 *      Relational-Expression:	   Shift-Expression		     [ ('<' | '<=' | '>' | '>=') Shift-Expression ]*
 *      Shift-Expression:	       Additive-Expression		 [ ('<<' | '>>') Additive-Expression ]*
 *      Additive-Expression:	   Multiplicative-Expression [ ('+' | '-') Multiplicative-Expression ]*
 *      Multiplicative-Expression: Term			             [ ('*' | '/' | '%') Term ]*
 * Term: Unary-Expression | Type-Cast-Expression
 * Type-Cast-Expression: '(' Type-Expression ')' Term       # can be dealed as Unary-Expression.
 * Unary-Expression:
 *        Postfix-Expression
 *      | Unary-Plus-Expression:           '+'  Expression
 *      | Unary-Minus-Expression:          '-'  Expression
 *      | Logical-Negation-Expression:     '!'  Expression
 *      | Bitwise-Negation-Expression:     '!'  Expression
 *      | Preincrement-Expression:         '++' Expression
 *      | Predecrement-Expression:         '--' Expression
 * Postfix-Expression:
 *        Primary-Expression
 *      | Subscript-Expression:           Expression '[' Expression ']'   e.g. array[i]
 *      | Component-Selection-Expression: Expression '.' IDENTIFIER       e.g. str.size
 *      | Function-Call:                  Expression '(' Arguments ')'    e.g. print(1)
 *      | Postincrement-Expression:       Expression '++'                 e.g. i++
 *      | Postdecrement-Expression:       Expression '--'                 e.g. i--
 * Primary-Expression:
 *        Identifier
 *      | Literal
 *      | Parenthesized-Expression:     '(' Expression ')'
 *
 * @author FC
 */
@Slf4j @Getter @Setter @Accessors(fluent = true)
public class ParserBase implements Parser {
	@Override
	public AstNode parse(TokenReader reader, ParserRegistry registry) {
		throw new RuntimeException("Not Used/Implemented");
	}

	protected static AstNode syntaxError(TokenReader reader, String message) {
		log.error(message);
		return null;
	}

	protected static AstNode ignore(TokenReader reader, ParserRegistry registry) {
		reader.nextToken();
		return null;
	}

	public static AstNode parseAny(TokenReader reader, ParserRegistry registry) {
		Token token = reader.token();
		Parser parser = registry.get(token.kind());
		if (parser != null) {
			AstNode node = parser.parse(reader, registry);
			return node;
		}
		return syntaxError(reader, "No registered parser for token kind " + token.kind());
	}

	public static Statement parseStatement(TokenReader reader, ParserRegistry registry) {
		AstNode node = parseAny(reader, registry);
		if (node instanceof Statement statement) {
			return statement;
		}
		return null;
	}

	public static Statement parseIfStatement(TokenReader reader, ParserRegistry registry) {
		if (!reader.optionalAnyOf(IF)) return null;

		IfStatement statement = new IfStatement();
		Expression expr = parseExpression(reader, registry);
		Statement thenStatement = parseStatement(reader, registry);
		Statement elseStatement = parseStatement(reader, registry);
		return statement.condition(expr).thenStatement(thenStatement).elseStatement(elseStatement);
	}

	public static Statement parseForStatementCStyle(TokenReader reader, ParserRegistry registry) {
		reader.acceptAnyOf(FOR);
		reader.acceptAnyOf(LEFT_PAREN);
		Statement initializer = parseVariableDeclaration(reader, registry);
		reader.acceptAnyOf(SEMICOLON);
		Expression condition = parseExpression(reader, registry);
		reader.acceptAnyOf(SEMICOLON);
		Statement update = new ExpressionStatement().expression(parseExpression(reader, registry));
		reader.acceptAnyOf(RIGHT_PAREN);
		Statement statement = parseStatement(reader, registry);

		ForStatement stmt = new ForStatement();
		stmt.initializer(initializer);
		stmt.condition(condition);
		stmt.update(update);
		stmt.statement(statement);
		return stmt;
	}

	public static Statement parseVariableDeclaration(TokenReader reader, ParserRegistry registry) {
		throw new RuntimeException("Not implemented");
	}

	// == expressions ==

	public static Expression parseExpression(TokenReader reader, ParserRegistry registry) {
		AstNode node = parseAny(reader, registry);
		if (node instanceof Expression expression) {
			return expression;
		}
		return null;
	}

	public static CommaExpression parseCommaExpression(TokenReader reader, ParserRegistry registry) {
		List<Expression> expressions = new ArrayList<>();
		do {
			expressions.add(parseExpression(reader, registry));
		} while (reader.isKind(COMMA));
		return new CommaExpression().expressions(expressions);
	}

	public static String[] DEFAULT_COMPOUND_ASSIGNMENT_OPERATORS = {
			PLUS_EQUAL,
			MINUS_EQUAL,
			STAR_EQUAL,
			AMPERSAND_EQUAL,
			BAR_EQUAL,
			GT_GT_EQUAL,
			LT_LT_EQUAL,
			PERCENT_EQUAL,
			TILDE_EQUAL,
			CARET_EQUAL,
	};

	public static Expression parseAssignmentExpression(TokenReader reader, ParserRegistry registry) {
		Token operator = null;
		Expression lhs = parseTernaryExpression(reader, registry);
		if (reader.optionalAnyOf(EQUAL)) {
			Expression rhs = parseAssignmentExpression(reader, registry);
			return new Assignment(lhs, rhs);
		} else if ((operator = reader.optionalAnyOfAndReturn(DEFAULT_COMPOUND_ASSIGNMENT_OPERATORS)) != null) {
			Expression rhs = parseAssignmentExpression(reader, registry);
			return new CompoundAssignment(lhs, rhs, operator.lexeme());
		} else {
			return lhs;
		}
	}

	public static Expression parseTernaryExpression(TokenReader reader, ParserRegistry registry) {
		Expression expr = parseBinaryExpression(reader, registry);
		if (reader.optionalAnyOf(QUESTION)) {
			Expression trueExpr = parseExpression(reader, registry);
			reader.acceptAnyOf(COLON);
			Expression falseExpr = parseTernaryExpression(reader, registry);
			return new TernaryExpression().condition(expr).trueExpression(trueExpr).falseExpression(falseExpr);
		}
		return null;
	}

	public static Expression parseBinaryExpression(TokenReader reader, ParserRegistry registry) {
		Expression expr = parseUnaryExpression(reader, registry);
		// operator precedence
		// if has binary operator,
		//  return parseBinaryExpressionRest()
		return expr;
	}

	public static Expression parseBinaryExpressionRest(TokenReader reader, ParserRegistry registry,
	                                                    Expression lhs, String operator, int minimumPrecedence) {
		return null;
	}

	public static Expression parseUnaryExpression(TokenReader reader, ParserRegistry registry) {
		Expression expr = null;
		if (reader.isKindAnyOf(PLUS, MINUS, PLUS_PLUS, MINUS_MINUS, BAR, TILDE, LEFT_PAREN)) {
			expr = parsePrefixUnaryExpression(reader, registry);
		} else {
			expr = parsePrimaryExpression(reader, registry);
			expr = parsePostfixUnaryExpression(reader, registry, expr);
		}
		return expr;
	}

	public static Expression parsePrefixUnaryExpression(TokenReader reader, ParserRegistry registry) {
		Token prefix = reader.optionalAnyOfAndReturn(PLUS, MINUS, PLUS_PLUS, MINUS_MINUS, BAR, TILDE, LEFT_PAREN);
		if (prefix != null) {
			Expression expr = parseUnaryExpression(reader, registry);
			return new PrefixUnaryExpression().expression(expr).operator(prefix.lexeme());
		}
		return null;
	}

	public static Expression parsePostfixUnaryExpression(TokenReader reader, ParserRegistry registry, Expression expr) {
		for (;;) {
			Token prefix = reader.optionalAnyOfAndReturn(DOT, LEFT_PAREN, LEFT_BRACKET);
			if (prefix != null) {
				Expression expr2 = parseExpression(reader, registry);
				expr = new PostfixUnaryExpression().expression(expr2).operator(prefix.lexeme());
			} else {
				return expr;
			}
		}
	}

	public static Expression parseTypeCast(TokenReader reader, ParserRegistry registry) {
		reader.optionalAnyOfAndReturn(LEFT_PAREN);
		Expression typeExpr = parseUnaryExpression(reader, registry);
		reader.optionalAnyOfAndReturn(RIGHT_PAREN);
		Expression expr = parseUnaryExpression(reader, registry);
		return new TypeCast().expression(expr).type(typeExpr);
	}

	public static Expression parseComponentSelection(TokenReader reader, ParserRegistry registry,
	                                                  Expression expr) {
		Identifier id = parseIdentifier(reader, registry);
		return new ComponentSelect().expression(expr).identifier(id);
	}

	public static Expression parseFunctionArguments(TokenReader reader, ParserRegistry registry,
	                                                 Expression function) {
		List<Expression> arguments = parseArguments(reader, registry);
		return new FunctionCall().functionSelect(function).arguments(arguments);
	}

	public static List<Expression> parseArguments(TokenReader reader, ParserRegistry registry) {
		throw new RuntimeException("Not Implemented");
	}

	public static Expression parsePrimaryExpression(TokenReader reader, ParserRegistry registry) {
		Token t = null;
		Expression expr = null;
		if (reader.isKind(IDENTIFIER)) {
			expr = parseIdentifier(reader, registry);
		} else if ((t = reader.optionalAnyOfAndReturn(STRING_LITERAL)) != null) {
			expr = new Literal<String>().value(t.lexeme());
		} else if ((t = reader.optionalAnyOfAndReturn(NUMBER_LITERAL)) != null) {
			expr = new Literal<String>().value(t.lexeme());
		} else if (reader.isKind(LEFT_PAREN)) {
			expr = parseParenExpression(reader, registry);
		}
		return expr;
	}

	public static ParenthesizedExpression parseParenExpression(TokenReader reader, ParserRegistry registry) {
		reader.acceptAnyOf(LEFT_PAREN);
		Expression expr = parseExpression(reader, registry);
		reader.acceptAnyOf(RIGHT_PAREN);
		return new ParenthesizedExpression().expression(expr);
	}


	public static Identifier parseIdentifier(TokenReader reader, ParserRegistry registry) {
		Token token = reader.acceptAnyOfAndReturn(IDENTIFIER);
		if (token != null) {
			return new Identifier().id(token.lexeme());
		} else {
			syntaxError(reader, "failed to parse identifier");
			return null;
		}
	}

	public static Literal parseLiteral(TokenReader reader, ParserRegistry registry) {
		Token token = reader.acceptAnyOfAndReturn();
		if (token != null) {
			return new Literal<String>().value(token.lexeme());
		} else {
			syntaxError(reader, "failed to parse literal");
			return null;
		}
	}
}
