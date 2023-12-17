package fc.compiler.common.lexer;

import fc.compiler.common.token.Token;

/**
 * @author FC
 */
@FunctionalInterface
public interface Lexer {
	public Token scan(CodeReader reader);
}
