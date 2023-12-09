package fc.compiler.common.parser;

import fc.compiler.common.ast.AstNode;
import fc.compiler.common.ast.Expression;
import fc.compiler.common.ast.Statement;
import fc.compiler.common.ast.statement.ExpressionStatement;
import fc.compiler.common.ast.statement.ForStatement;
import fc.compiler.common.ast.statement.IfStatement;
import fc.compiler.common.lexer.CodeReader;
import fc.compiler.common.lexer.Lexer;
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
@Slf4j @Getter @Setter @Accessors(chain = true)
public class ParserBase implements Parser {
	protected Map<String, Parser> registry;
	protected CodeReader reader;

	@Override
	public AstNode parse(Lexer mainLexer, CodeReader reader) {
		Token token = mainLexer.scan(reader);
		Parser parser = registry.get(token.getKind());
		if (parser != null) {
			AstNode node = parser.parse(mainLexer, reader);
			return node;
		}
		return syntaxError(mainLexer, reader, "No registered parser for token kind " + token.getKind());
	}

	protected static AstNode syntaxError(Lexer mainLexer, CodeReader reader, String message) {
		log.error(message);
		return null;
	}

	public static Statement parseStatement(Lexer mainLexer, CodeReader reader) {
		return null;
	}

	public static Expression parseExpression(Lexer mainLexer, CodeReader reader) {
		return null;
	}

	public static Statement parseIfStatement(Lexer mainLexer, CodeReader reader) {
		accept(IF);
		Expression expr = parseExpression(mainLexer, reader);
		Statement thenStatement = parseStatement(mainLexer, reader);
		Statement elseStatement = parseStatement(mainLexer, reader);
		return new IfStatement(expr, thenStatement, elseStatement);
	}

	public static Statement parseForStatementCStyle(Lexer mainLexer, CodeReader reader) {
		accept(FOR);
		accept(LEFT_PAREN);
		Statement initializer = parseVariableDeclaration(mainLexer, reader);
		accept(SEMICOLON);
		Expression condition = parseExpression(mainLexer, reader);
		accept(SEMICOLON);
		Statement update = new ExpressionStatement().setExpression(parseExpression(mainLexer, reader));
		accept(RIGHT_PAREN);
		Statement statement = parseStatement(mainLexer, reader);

		ForStatement stmt = new ForStatement();
		stmt.setInitializer(initializer);
		stmt.setCondition(condition);
		stmt.setUpdate(update);
		stmt.setStatement(statement);
		return stmt;
	}

	private static Statement parseVariableDeclaration(Lexer mainLexer, CodeReader reader) {
		return null;
	}

	public static boolean accept(String tokenKind) {
//		if (token.getKind() == tokenKind) {
//			nextToken();
//			return true;
//		} else {
//			syntaxError("accept(): " + kind + " is expected. but token " + token + " is parsed");
//			return false;
//		}
		return true;
	}
}
