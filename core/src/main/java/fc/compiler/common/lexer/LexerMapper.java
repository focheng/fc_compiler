package fc.compiler.common.lexer;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * Map between character and lexer.
 * @author FC
 */
@Deprecated
public class LexerMapper {
	@Getter @Setter
	protected LexerWithCodeReader defaultLexer;
	protected LexerWithCodeReader[] asciiLexers = new LexerWithCodeReader[128];         // for ASCII characters.
	protected Map<Character, LexerWithCodeReader> extendedLexers = new HashMap<>();  // for Unicode characters.

	public LexerWithCodeReader getLexer(Character ch) {
		if (ch < 128) {
			return asciiLexers[ch];
		} else {
			return extendedLexers.get(ch);
		}
	}

	public void mapLexer(Character ch, LexerWithCodeReader lexer) {
		if (ch < 128) {
			asciiLexers[ch] = lexer;
		} else {
			extendedLexers.put(ch, lexer);
		}
	}
}
