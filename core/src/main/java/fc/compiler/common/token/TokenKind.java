package fc.compiler.common.token;

import fc.compiler.common.lexer.Position;
import fc.compiler.language.pli.PliTokenKind;

import java.util.HashMap;

/**
 * @author FC
 */
public interface TokenKind {
	default public boolean isAnyOf(TokenKind... kinds) {
		for (TokenKind kind : kinds) {
			if (this == kind)
				return true;
		}
		return false;
	}

	default public boolean isNoneOf(TokenKind... kinds) {
		for (TokenKind kind : kinds) {
			if (this == kind)
				return false;
		}
		return true;
	}
//	default public Token<TokenKind> newToken(String lexeme, Position position, HashMap<String, Object> attributes) {
//		return newToken(this, lexeme, position, attributes);
//	}
//	default public Token<TokenKind> newToken(String lexeme, Position position) {
//		return newToken(this, lexeme, position, null);
//	}
//	default public Token<TokenKind> newToken(String lexeme) {
//		return newToken(this, lexeme, null, null);
//	}
}
