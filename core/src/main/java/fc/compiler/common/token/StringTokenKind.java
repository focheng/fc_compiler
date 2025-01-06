package fc.compiler.common.token;

import java.util.HashMap;
import java.util.Map;

/**
 * Token kind.
 *  id      - unique id. Its type can be integer or string.
 *  name    -
 *  literal - the original string literal.
 *  parent  - parent kind. e.g. LITERAL is parent kind of STRING LITERAL.
 *  description - a detailed description.
 *
 *      id                 |   name         |  literal   |  parent | description
 *  -----------------------|----------------|------------|---------|------
 *    1  "<EOF>"           |   EOF          |     N/A    |         |
 *    35 "while"           |   WHILE        |   'while'  |         |
 *    41 "equal"           |   EQUAL        |     '='    |         |
 *    51 "LITERAL"         | LITERAL        |     N/A    |         |
 *    52 "STRING_LITERAL"  | STRING_LITERAL |     N/A    | LITERAL |
 *
 * @author FC
 */
@Deprecated
public interface StringTokenKind {
	public boolean is(String kindName);

	// == EOF, Error ==
	String EOF = "<EOF>";
	String ERROR = "<ERROR>";


	// == white spaces ==
	String WHITE_SPACES = "<WHITE_SPACES>";

	// -- line terminator --
	String LINE_TERMINATOR = "<LINE_TERMINATOR>";

	default boolean isWhiteSpaces(String tokenKind) {
		return tokenKind == WHITE_SPACES || tokenKind == LINE_TERMINATOR;
	}

	// -- indent --
	//String INDENT = "INDENT";


	// == comments ==
	String BLOCK_COMMENT = "<BLOCK_COMMENT>";
	String LINE_COMMENT = "<LINE_COMMENT>";
	String DOC_COMMENT = "<DOC_COMMENT>";

	default boolean isComment(String tokenKind) {
		return tokenKind == BLOCK_COMMENT
				|| tokenKind == LINE_COMMENT
				|| tokenKind == DOC_COMMENT;
	}


	// == literals ==
	String STRING_LITERAL   = "<STRING_LITERAL>";
	String CHAR_LITERAL     = "<CHAR_LITERAL>";
	String BOOLEAN_LITERAL  = "<BOOLEAN_LITERAL>";
	String NUMBER_LITERAL   = "<NUMBER_LITERAL>";
	String INT_LITERAL      = "<INT_LITERAL>";
	String LONG_LITERAL     = "<LONG_LITERAL>";
	String FLOAT_LITERAL    = "<FLOAT_LITERAL>";
	String DOUBLE_LITERAL   = "<DOUBLE_LITERAL>";


	// == separators (punctuators) are formed from ASCII characters ==
	String DOT              = ".";
	String COMMA            = ",";
	String SEMICOLON        = ";";
	String COLON            = ":";
	String LEFT_PAREN       = "(";
	String RIGHT_PAREN      = ")";
	String LEFT_BRACKET     = "[";
	String RIGHT_BRACKET    = "]";
	String LEFT_BRACE       = "{";
	String RIGHT_BRACE      = "}";
	String QUESTION         = "?";
	String AT               = "@";
	String SINGLE_QUOTE     = "'";
	String DOUBLE_QUOTE     = "\"";


	// == operators are formed from ASCII characters ==
	String PLUS                     = "+";
	String PLUS_EQUAL               = "+=";
	String PLUS_PLUS                = "++";
	String MINUS                    = "-";
	String MINUS_EQUAL              = "-=";
	String MINUS_MINUS              = "--";
	String STAR                     = "*";
	String STAR_EQUAL               = "*=";
	String SLASH                    = "/";
	String SLASH_EQUAL              = "/=";
	String AMPERSAND                = "&";
	String AMPERSAND_EQUAL          = "&=";
	String AMPERSAND_AMPERSAND      = "&&";
	String BAR                      = "|";
	String BAR_EQUAL                = "|=";
	String BAR_BAR                  = "||";
	String EQUAL                    = "=";
	String EQUAL_EQUAL              = "==";
	String GT                       = ">";
	String GT_EQUAL                 = ">=";
	String GT_GT                    = ">>";
	String GT_GT_EQUAL              = ">>=";
	String LT                       = "<";
	String LT_EQUAL                 = "<=";
	String LT_LT                    = "<<";
	String LT_LT_EQUAL              = "<<=";
	String EXCLAMATION_MARK         = "!";
	String EXCLAMATION_MARK_EQUAL   = "!=";
	String PERCENT                  = "%";
	String PERCENT_EQUAL            = "%=";
	String TILDE                    = "~";
	String TILDE_EQUAL              = "~=";
	String CARET                    = "^";
	String CARET_EQUAL              = "^=";

	// == special punctuator ==
	String ELLIPSIS                 = "...";   // "..." in java.


	// == identifier ==
	String IDENTIFIER = "<IDENTIFIER>";


	// == keywords are formed from ASCII characters ==
	public static Map<String, String> reservedKeywords = new HashMap<>();
	public static void register(String keyword, String tokenKind) {
		reservedKeywords.put(keyword, tokenKind);
	}

	// -- reserved keywords --
	String IF = "IF";
	String FOR = "FOR";

	// -- contextual keywords --


	public static interface Eof extends StringTokenKind {}
	public static interface Error extends StringTokenKind {}
	public static interface WhiteSpace extends StringTokenKind {}
	public static interface Newline extends StringTokenKind {}
	public static interface Comment extends StringTokenKind {}
	public static interface Separator extends StringTokenKind {}
	public static interface Operator extends StringTokenKind {}
	public static interface Literal extends StringTokenKind {}
	public static interface NumberLiteral extends Literal {}
	public static interface StringLiteral extends Literal {}
	public static interface Identifier extends StringTokenKind {}
	public static interface Keyword extends StringTokenKind {}
	public static interface ReservedKeyword extends Keyword {}
	public static interface ContextualKeyword extends StringTokenKind {}

}
