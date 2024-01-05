package fc.compiler.common.lexer;

import fc.compiler.common.token.Token;
import fc.compiler.common.token.TokenKind;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.function.Predicate;

import static fc.compiler.common.token.TokenKind.IDENTIFIER;

/**
 * @author FC
 */
@Getter @Setter @Accessors(fluent= true)
public class IdentifierLexer extends LexerBase {
	private Predicate<Character> isIdentifierStart = IdentifierLexer::isIdentifierStartDefault;
	private Predicate<Character> isIdentifierPart  = IdentifierLexer::isIdentifierPartDefault;
	private boolean caseSensitive = true;


	@Override
	public Token scan(CodeReader reader) {
		if (!reader.accept(isIdentifierStart)) {
			return lexError(reader, "Invalid identifier starting character.");
		}

		while (reader.accept(isIdentifierPart)) ;

		String lexeme = reader.lexeme();
		String key = caseSensitive ? lexeme : lexeme.toUpperCase();
		String kind = TokenKind.reservedKeywords.getOrDefault(key, IDENTIFIER);
		Token token = new Token(kind, reader.position).lexeme(lexeme);
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
