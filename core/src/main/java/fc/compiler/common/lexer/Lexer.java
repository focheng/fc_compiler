package fc.compiler.common.lexer;

import fc.compiler.common.token.Token;

/**
 * @author FC
 */
@FunctionalInterface
public interface Lexer<T extends Token<?>> {
	public T scanToken();
}
