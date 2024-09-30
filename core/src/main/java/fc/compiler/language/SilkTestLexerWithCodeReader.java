package fc.compiler.language;

import fc.compiler.common.lexer.CodeReaderBase;
import fc.compiler.common.lexer.LexerWithCodeReader;
import fc.compiler.common.token.StringToken;

/**
 * @author FC
 */
public class SilkTestLexerWithCodeReader implements LexerWithCodeReader {
	@Override
	public StringToken scanToken(CodeReaderBase reader) {
		reader.onStartToken();
		if (reader.isWhiteSpace()) {
			//reader.
		}
		return null;
	}
}
