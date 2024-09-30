package fc.compiler.common.token;

import fc.compiler.common.lexer.Position;

import java.util.HashMap;

/**
 * Token is the output of lexer and the input of parser.
 * Token consists of token kind and optional attribute values:
 * - token kind (named in dragon book) is the type of token. see TokenKind.
 * - lexeme is a sequence of characters in the source code that matches the pattern for a token
 *   and is identified by the lexer as an instance of that token.
 * - position in the source code.
 *      - line
 *      - character position in the line
 * - channel in Antlr. Hidden channel is not parsed by parser.
 *
 * @author FC
 */
public interface Token<Kind> {
	public Kind kind();
	public String lexeme();
	public Position position();
	public HashMap<String, Object> attributes();
}
