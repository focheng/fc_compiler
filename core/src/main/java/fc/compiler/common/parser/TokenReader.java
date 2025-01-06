package fc.compiler.common.parser;

import fc.compiler.common.token.Token;

/**
 * A bridge between Lexer and Parser just like CodeReader
 * @author FC
 */
@FunctionalInterface
public interface TokenReader<Kind> {
	public Token<Kind> nextToken();
}
