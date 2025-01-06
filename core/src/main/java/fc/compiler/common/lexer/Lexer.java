package fc.compiler.common.lexer;

import fc.compiler.common.token.Token;

/**
 * Lexer interface.
 * @author FC
 */
@FunctionalInterface
public interface Lexer<T extends Token<?>> {
	/**
	 * Scan and return one Token object.
	 * @return Token object
	 */
	public T scanToken();

	default Position position() { return null; }
}
