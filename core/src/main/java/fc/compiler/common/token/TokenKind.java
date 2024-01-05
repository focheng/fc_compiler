package fc.compiler.common.token;

import java.util.HashMap;
import java.util.Map;

/**
 * @author FC
 */
public interface TokenKind {
	// == EOF, Error ==
	String EOF = "EOF";
	String ERROR = "ERROR";


	// == white spaces ==
	String WHITE_SPACES = "WHITE_SPACES";

	// -- line terminator --
	String LINE_TERMINATOR = "LINE_TERMINATOR";

	default boolean isWhiteSpaces(String tokenKind) {
		return tokenKind == WHITE_SPACES || tokenKind == LINE_TERMINATOR;
	}

	// -- indent --
	//String INDENT = "INDENT";


	// == comments ==
	String BLOCK_COMMENT = "BLOCK_COMMENT";
	String LINE_COMMENT = "LINE_COMMENT";
	String DOC_COMMENT = "DOC_COMMENT";

	default boolean isComment(String tokenKind) {
		return tokenKind == BLOCK_COMMENT
				|| tokenKind == LINE_COMMENT
				|| tokenKind == DOC_COMMENT;
	}


	// == literals ==
	String STRING_LITERAL = "STRING_LITERAL";
	String CHAR_LITERAL = "CHAR_LITERAL";
	String BOOLEAN_LITERAL = "BOOLEAN_LITERAL";
	String NUMBER_LITERAL = "NUMBER_LITERAL";
	String INT_LITERAL = "INT_LITERAL";
	String LONG_LITERAL = "LONG_LITERAL";
	String FLOAT_LITERAL = "FLOAT_LITERAL";
	String DOUBLE_LITERAL = "DOUBLE_LITERAL";


	// == separators (punctuactors) are formed from ASCII characters ==
	String DOT = ".";
	String COMMA = ",";
	String SEMICOLON = ";";
	String COLON = ":";
	String LEFT_PAREN = "(";
	String RIGHT_PAREN = ")";
	String LEFT_BRACKET = "[";
	String RIGHT_BRACKET = "]";
	String LEFT_BRACE = "{";
	String RIGHT_BRACE = "}";
	String QUESTION = "?";
	String AT = "@";
	String SINGLE_QUOTE = "SINGLE_QUOTE";
	String DOUBLE_QUOTE = "DOUBLE_QUOTE";


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


	// == identifier ==
	String IDENTIFIER = "IDENTIFIER";


	// == keywords are formed from ASCII characters ==
	public static Map<String, String> reservedKeywords = new HashMap<>();
	public static void register(String keyword, String tokenKind) {
		reservedKeywords.put(keyword, tokenKind);
	}

	// -- reserved keywords --
	String IF = "IF";
	String FOR = "FOR";

	// -- contextual keywords --


	// == special ==
	String ELLIPSIS             = "...";   // "..." in java.
}
