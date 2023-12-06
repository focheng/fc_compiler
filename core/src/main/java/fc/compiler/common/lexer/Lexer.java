package fc.compiler.common.lexer;

import fc.compiler.common.token.Token;

/**
 * @author FC
 */
@FunctionalInterface
public interface Lexer {
	public Token scan(CodeReader reader);

//	default boolean isIdentifierStart(char ch) { return Character.isLetter(ch); }
//	default boolean isIdentifierPart(char ch) { return Character.isLetterOrDigit(ch) || ch == '_'; }
//	default boolean isDigit(char ch) { return '0' <= ch && ch <= '9'; }
//	default boolean isStringLiteralStart(char ch) { return ch == '\"'; }
//	default boolean isCharLiteralQuotation(char ch) { return ch == '\''; }

	@FunctionalInterface interface IsIdentifierStart { boolean is(char ch); }
	@FunctionalInterface interface IsIdentifierPart { boolean is(char ch); }
	@FunctionalInterface interface IsDigit { boolean is(char ch); }
	@FunctionalInterface interface IsStringLiteralStart { boolean is(char ch); }
	@FunctionalInterface interface IsCharLiteralQuotation { boolean is(char ch); }
}
