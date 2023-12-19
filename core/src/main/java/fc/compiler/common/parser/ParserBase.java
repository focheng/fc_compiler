package fc.compiler.common.parser;

import fc.compiler.common.ast.AstNode;
import fc.compiler.common.ast.Expression;
import fc.compiler.common.ast.Statement;
import fc.compiler.common.ast.statement.ExpressionStatement;
import fc.compiler.common.ast.statement.ForStatement;
import fc.compiler.common.ast.statement.IfStatement;
import fc.compiler.common.token.Token;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import static fc.compiler.common.token.TokenKind.*;

/**
 * @author FC
 */
@Slf4j @Getter @Setter @Accessors(fluent = true)
public class ParserBase implements Parser {
	protected TokenReader reader;

	@Override
	public AstNode parse(TokenReader reader, ParserRegistry registry) {
		Token token = reader.token();
		Parser parser = registry.get(token.kind());
		if (parser != null) {
			AstNode node = parser.parse(reader, registry);
			return node;
		}
		return syntaxError(reader, "No registered parser for token kind " + token.kind());
	}

	protected static AstNode syntaxError(TokenReader reader, String message) {
		log.error(message);
		return null;
	}

	protected static AstNode ignore(TokenReader reader, ParserRegistry registry) {
		reader.nextToken();
		return null;
	}

	public static Statement parseStatement(TokenReader reader, ParserRegistry registry) {
		throw new RuntimeException("Not implemented");
	}

	public static Expression parseExpression(TokenReader reader, ParserRegistry registry) {
		throw new RuntimeException("Not implemented");
	}

	public static Statement parseIfStatement(TokenReader reader, ParserRegistry registry) {
		reader.accept(IF);
		Expression expr = parseExpression(reader, registry);
		Statement thenStatement = parseStatement(reader, registry);
		Statement elseStatement = parseStatement(reader, registry);
		return new IfStatement(expr, thenStatement, elseStatement);
	}

	public static Statement parseForStatementCStyle(TokenReader reader, ParserRegistry registry) {
		reader.accept(FOR);
		reader.accept(LEFT_PAREN);
		Statement initializer = parseVariableDeclaration(reader, registry);
		reader.accept(SEMICOLON);
		Expression condition = parseExpression(reader, registry);
		reader.accept(SEMICOLON);
		Statement update = new ExpressionStatement().expression(parseExpression(reader, registry));
		reader.accept(RIGHT_PAREN);
		Statement statement = parseStatement(reader, registry);

		ForStatement stmt = new ForStatement();
		stmt.initializer(initializer);
		stmt.condition(condition);
		stmt.update(update);
		stmt.statement(statement);
		return stmt;
	}

	private static Statement parseVariableDeclaration(TokenReader reader, ParserRegistry registry) {
		throw new RuntimeException("Not implemented");
	}

//	public static boolean accept(String tokenKind) {
//		if (token.kind() == tokenKind) {
//			nextToken();
//			return true;
//		} else {
//			syntaxError("accept(): " + kind + " is expected. but token " + token + " is parsed");
//			return false;
//		}
//	}
}
