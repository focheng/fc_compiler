package fc.compiler.common.lexer;

import fc.compiler.common.token.Token;
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
	private Predicate<Character> isIdentifierStart;
	private Predicate<Character> isIdentifierPart;


	@Override
	public Token scan(CodeReader reader) {
		if (!reader.accept(isIdentifierStart)) {
			return lexError(reader, "Invalid identifier starting character.");
		}
		while (reader.accept(isIdentifierPart)) ;
		return new Token(IDENTIFIER, reader.position).lexeme(reader.lexeme());
	}
}
