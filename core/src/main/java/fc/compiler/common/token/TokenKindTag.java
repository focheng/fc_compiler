package fc.compiler.common.token;

/**
 * Tag for token kind.
 * @author FC
 */
//@FunctionalInterface
public interface TokenKindTag {
	boolean isEof(TokenKind tokenKind);
	boolean isError(TokenKind tokenKind);
	boolean isNewline(TokenKind tokenKind);
	boolean isWhiteSpace(TokenKind tokenKind);
	boolean isComment(TokenKind tokenKind);
	boolean isLiteral(TokenKind tokenKind);
	boolean isSeparator(TokenKind tokenKind);
	boolean isOperator(TokenKind tokenKind);
	boolean isIdentifier(TokenKind tokenKind);
	boolean isReservedKeyword(TokenKind tokenKind);
	boolean isContextualKeyword(TokenKind tokenKind);


}
