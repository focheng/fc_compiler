package fc.compiler.language.pli;

/**
 * Tag on token kind.
 * @author FC
 */
public enum PliTokenKindTag {
	KEYWORD,
	COMMENT,
	LITERAL,
	UNARY_OPERATOR,
	BINARY_OPERATOR,
	TRINARY_OPERATOR,

	// language specific tags
	SPACE_FAMILY,
}
