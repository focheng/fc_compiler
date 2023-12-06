package fc.compiler.common.token;

/**
 * @author FC
 */
public interface TokenKind {
	// == EOF, Error ==
	public static final String EOF = "EOF";
	public static final String ERROR = "ERROR";


	// == white spaces ==
	public static final String WHITE_SPACES = "WHITE_SPACES";

	// -- line terminator --
	public static final String LINE_TERMINATOR = "LINE_TERMINATOR";

	// -- indent --
	public static final String INDENT = "INDENT";


	// == comments ==
	public static final String BLOCK_COMMENT = "BLOCK_COMMENT";
	public static final String LINE_COMMENT = "LINE_COMMENT";


	// == literals ==
	public static final String NUMBER_LITERAL = "NUMBER_LITERAL";
	public static final String INT_LITERAL = "INT_LITERAL";
	public static final String LONG_LITERAL = "LONG_LITERAL";
	public static final String FLOAT_LITERAL = "FLOAT_LITERAL";
	public static final String DOUBLE_LITERAL = "DOUBLE_LITERAL";
	public static final String STRING_LITERAL = "STRING_LITERAL";
	public static final String CHAR_LITERAL = "CHAR_LITERAL";

	// == separators (punctuactors) are formed from ASCII characters ==
	public static final String DOT = "DOT";
	public static final String COMMA = "COMMA";
	public static final String SEMICOLON = "SEMICOLON";
	public static final String COLON = "COLON";
	public static final String LEFT_PAREN = "LEFT_PAREN";
	public static final String RIGHT_PAREN = "RIGHT_PAREN";
	public static final String LEFT_BRACKET = "LEFT_BRACKET";
	public static final String RIGHT_BRACKET = "RIGHT_BRACKET";
	public static final String LEFT_BRACE = "LEFT_BRACE";
	public static final String RIGHT_BRACE = "RIGHT_BRACE";
	public static final String QUESTION = "QUESTION";
	public static final String AT = "AT";
	public static final String SINGLE_QUOTE = "SINGLE_QUOTE";
	public static final String DOUBLE_QUOTE = "DOUBLE_QUOTE";


	// == operators are formed from ASCII characters ==
	public static final String PLUS = "PLUS";
	public static final String PLUS_EQUAL = "PLUS_EQUAL";
	public static final String PLUS_PLUS = "PLUS_PLUS";
	public static final String MINUS = "MINUS";
	public static final String MINUS_EQUAL = "MINUS_EQUAL";
	public static final String MINUS_MINUS = "MINUS_MINUS";
	public static final String STAR = "STAR";
	public static final String STAR_EQUAL = "STAR_EQUAL";
	public static final String SLASH = "SLASH";
	public static final String SLASH_EQUAL = "SLASH_EQUAL";
	public static final String AMPERSAND = "AMPERSAND";
	public static final String AMPERSAND_EQUAL = "AMPERSAND_EQUAL";
	public static final String AMPERSAND_AMPERSAND = "AMPERSAND_AMPERSAND";
	public static final String BAR = "BAR";
	public static final String BAR_EQUAL = "BAR_EQUAL";
	public static final String BAR_BAR = "BAR_BAR";
	public static final String EQUAL = "EQUAL";
	public static final String EQUAL_EQUAL = "EQUAL_EQUAL";
	public static final String GT = "GT";
	public static final String GT_GT = "GT_GT";
	public static final String GT_EQUAL = "GT_EQUAL";
	public static final String GT_GT_EQUAL = "GT_GT_EQUAL";
	public static final String LT = "LT";
	public static final String LT_LT = "LT_LT";
	public static final String LT_EQUAL = "LT_EQUAL";
	public static final String LT_LT_EQUAL = "LT_LT_EQUAL";
	public static final String EXCLAMATION_MARK = "EXCLAMATION_MARK";
	public static final String EXCLAMATION_MARK_EQUAL = "EXCLAMATION_MARK_EQUAL";
	public static final String PERCENT = "PERCENT";
	public static final String PERCENT_EQUAL = "PERCENT_EQUAL";
	public static final String TILDE = "TILDE";
	public static final String TILDE_EQUAL = "TILDE_EQUAL";
	public static final String CARET = "CARET";
	public static final String CARET_EQUAL = "CARET_EQUAL";

	// == identifier ==
	public static final String IDENTIFIER = "IDENTIFIER";

	// == keywords are formed from ASCII characters ==
	// -- reserved keywords --

	// -- contextual keywords --

	// == special ==
	public static final String ELLIPSIS = "ELLIPSIS";   // "..." in java.


}
