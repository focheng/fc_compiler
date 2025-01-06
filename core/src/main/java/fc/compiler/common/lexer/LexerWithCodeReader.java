package fc.compiler.common.lexer;

import fc.compiler.common.token.StringToken;

/**
 * @author FC
 */
@Deprecated
@FunctionalInterface
public interface LexerWithCodeReader {
	public StringToken scanToken(CodeReaderBase reader);
}
