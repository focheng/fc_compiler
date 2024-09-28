package fc.compiler.language;

import fc.compiler.common.lexer.CodeReaderBase;
import fc.compiler.common.lexer.Lexer;
import fc.compiler.common.token.Token;

/**
 * @author FC
 */
public class SilkTestLexer implements Lexer {
	@Override
	public Token scan(CodeReaderBase reader) {
		reader.onStartToken();
		if (reader.isWhiteSpace()) {
			//reader.
		}
		return null;
	}
}
