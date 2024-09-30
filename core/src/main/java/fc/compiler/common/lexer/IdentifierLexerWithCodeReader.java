package fc.compiler.common.lexer;

import fc.compiler.common.token.StringToken;
import fc.compiler.common.token.StringTokenKind;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.function.Predicate;

import static fc.compiler.common.token.StringTokenKind.IDENTIFIER;

/**
 * @author FC
 */
@Getter @Setter @Accessors(fluent= true)
public class IdentifierLexerWithCodeReader extends LexerWithCodeReaderBase {
	private Predicate<Character> isIdentifierStart = IdentifierLexerWithCodeReader::isIdentifierStartDefault;
	private Predicate<Character> isIdentifierPart  = IdentifierLexerWithCodeReader::isIdentifierPartDefault;
	protected boolean caseSensitive = true;


	@Override
	public StringToken scanToken(CodeReaderBase reader) {
		if (!reader.optionalChar(isIdentifierStart)) {
			return lexError(reader, "Invalid identifier starting character.");
		}

		while (reader.optionalChar(isIdentifierPart)) ;

		String lexeme = reader.lexeme();
		String key = caseSensitive ? lexeme : lexeme.toUpperCase();
		String kind = StringTokenKind.reservedKeywords.getOrDefault(key, IDENTIFIER);
		StringToken token = new StringToken(kind, lexeme, reader.position);
		return token;
	}

	public static boolean isIdentifierStartDefault(char ch) {
		return ('a' <= ch && ch <= 'z') || ('A' <= ch && ch <= 'Z');
	}

	public static boolean isIdentifierPartDefault(char ch) {
		return ('a' <= ch && ch <= 'z') || ('A' <= ch && ch <= 'Z')
				|| ('0' <= ch && ch <= '9') || ch == '_';
	}
}
