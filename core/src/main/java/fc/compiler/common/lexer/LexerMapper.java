package fc.compiler.common.lexer;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * Map between character and lexer.
 * @author FC
 */
public class LexerMapper {
	@Getter @Setter
	protected Lexer defaultLexer;
	protected Lexer[] asciiLexers = new Lexer[128];         // for ASCII characters.
	protected Map<Character, Lexer> extendedLexers = new HashMap<>();  // for other characters especially unicode.

	public Lexer getLexer(Character ch) {
		if (ch < 128) {
			return asciiLexers[ch];
		} else {
			return extendedLexers.get(ch);
		}
	}

	public void mapLexer(Character ch, Lexer lexer) {
		if (ch < 128) {
			asciiLexers[ch] = lexer;
		} else {
			extendedLexers.put(ch, lexer);
		}
	}
}
