package fc.compiler.common.token;

import fc.compiler.common.lexer.Position;
import fc.compiler.language.pli.PliToken;

import java.util.HashMap;

/**
 * @author FC
 */
@FunctionalInterface
public interface TokenFactory {
	public Token<TokenKind> newToken(TokenKind TokenKind, String lexeme, Position position, HashMap<String, Object> attributes);

	default public Token<TokenKind> newToken(TokenKind TokenKind, String lexeme, Position position) {
		return newToken(TokenKind, lexeme, position, null);
	}
	default public Token<TokenKind> newToken(TokenKind TokenKind, String lexeme) {
		return newToken(TokenKind, lexeme, null, null);
	}
	default public Token<TokenKind> newToken(TokenKind TokenKind) {
		return newToken(TokenKind, null, null, null);
	}
}
