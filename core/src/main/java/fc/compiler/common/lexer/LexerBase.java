package fc.compiler.common.lexer;

import fc.compiler.common.token.Token;
import fc.compiler.language.antlr.traditional.FcgToken;
import fc.compiler.language.antlr.traditional.FcgTokenKind;

/**
 * @author FC
 */
public abstract class LexerBase<Kind, T extends Token<Kind>> extends CodeReaderBase implements Lexer<T> {
	public LexerBase(String code) {
		super(code);
	}

}
