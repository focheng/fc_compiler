package fc.compiler.language.pli;

import fc.compiler.common.token.TokenKind;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

import static fc.compiler.language.pli.PliOperatorPriority.*;
import static fc.compiler.language.pli.PliTokenKindTag.*;

/**
 * @author FC
 */
@Getter @Accessors(fluent = true, chain = true)
@NoArgsConstructor
public enum PliTokenKind implements TokenKind {
	// -- ERROR --
	ERROR_TOKEN,

	// -- EOF --
	EOF,

	// -- white space --
	WHITE_SPACES,
	NEW_LINE,

	// -- comments --
	BLOCK_COMMENT           (null, COMMENT),

	// -- literals --
	STRING_LITERAL,
	NUMBER_LITERAL,
	INTEGER_LITERAL,

	// -- delimiter  --
	COMMA		            (","),
	DOT		                ("."),
	SEMICOLON		        (";"),
	COLON                   (":"),
	LEFT_PAREN              ("("),
	RIGHT_PAREN             (")"),
	PERCENT                 ("%"),
	SINGLE_QUOTE            ("'"),
	DOUBLE_QUOTE            ("\""),

	LOCATOR_POINTER         ("->"),
	LOCATOR_HANDLE          ("=>"),

	// -- operators  --
	// arithmetic operators
	PLUS                    ("+",   BINARY_OPERATOR, INFIX_PLUS_MINUS),
	MINUS                   ("-",   BINARY_OPERATOR, INFIX_PLUS_MINUS),
	STAR                    ("*",   BINARY_OPERATOR, MULTIPLICATION_DIVISION),
	SLASH                   ("/",   BINARY_OPERATOR, MULTIPLICATION_DIVISION),
	STAR_STAR               ("**",  BINARY_OPERATOR, POWER),

	// comparison operators
	EQ                      ("=",   BINARY_OPERATOR, COMPIRSON),
	NOT_EQ                  ("?=", "^=",   BINARY_OPERATOR, COMPIRSON),
	LT                      ("<",   BINARY_OPERATOR, COMPIRSON),
	NOT_LT                  ("?<", "^<",   BINARY_OPERATOR, COMPIRSON),
	GT                      (">",   BINARY_OPERATOR, COMPIRSON),
	NOT_GT                  ("?>", "^>",   BINARY_OPERATOR, COMPIRSON),
	LE                      ("<=",   BINARY_OPERATOR, COMPIRSON),
	GE                      (">=",   BINARY_OPERATOR, COMPIRSON),

	// logical operators
	NOT                     ("?", "^", UNARY_OPERATOR, PREFIX_NOT),
	AND                     ("&",   BINARY_OPERATOR, BIT_AND),
	BAR                     ("|",   BINARY_OPERATOR, BIT_OR),

	BAR_BAR                 ("||",   BINARY_OPERATOR, CONCATENATION),

	// compound assignment operators
	PLUS_EQ                 ("+=",   BINARY_OPERATOR),
	MINUS_EQ                ("-=",   BINARY_OPERATOR),
	STAR_EQ                 ("*=",   BINARY_OPERATOR),
	SLASH_EQ                ("/=",   BINARY_OPERATOR),
	STAR_STAR_EQ            ("**=",   BINARY_OPERATOR),
	AND_EQ                  ("&=",   BINARY_OPERATOR),
	BAR_EQ                  ("|=",   BINARY_OPERATOR),
	BAR_BAR_EQ              ("||=",   BINARY_OPERATOR),

	// -- IDENTIFIER --
	IDENTIFIER              (null),

	// -- Keywords --

	// - data attribute keywords -
	CHARACTER                      ("CHARACTER",    "CHAR",   KEYWORD),
	WIDECHAR                      ("WIDECHAR",   "WCHAR",   KEYWORD),
	VARYING                      ("VARYING",   "VAR",   KEYWORD),
	VARYINGZ                      ("VARYINGZ",   "VARZ",    KEYWORD),
	NONVARYING                      ("NONVARYING",   "NONVAR",  KEYWORD),
	BIT                      ("BIT",   KEYWORD),
	GRAPHIC                      ("GRAPHIC",   "G", KEYWORD),

	BINARY                      ("BINARY",   "BIN", KEYWORD),
	DECIMAL                      ("DECIMAL",   "DEC",   KEYWORD),
	FIXED                      ("FIXED",   KEYWORD),
	FLOAT                      ("FLOAT",   KEYWORD),
	REAL                      ("REAL",   KEYWORD),
	COMPLEX                      ("COMPLEX",   KEYWORD),

	AREA                      ("AREA",   KEYWORD),
	DIMENSION                      ("DIMENSION",   KEYWORD),
	ENTRY                      ("ENTRY",   KEYWORD),
	FILE                      ("FILE",   KEYWORD),
	FORMAT                      ("FORMAT",   KEYWORD),
	HANDLE                      ("HANDLE",   KEYWORD),
	LABEL                      ("LABEL",   KEYWORD),
	OFFSET                      ("OFFSET",   KEYWORD),
	ORDINAL                      ("ORDINAL",   KEYWORD),
	PICTURE                      ("PICTURE",   KEYWORD),
	POINTER                      ("POINTER",   KEYWORD),
	PRECISION                      ("PRECISION",   KEYWORD),
	RETURNS                      ("RETURNS",   KEYWORD),
	SIGNED                      ("SIGNED",   KEYWORD),
	STRUCTURE                      ("STRUCTURE",   KEYWORD),
	TASK                      ("TASK",   KEYWORD),
	TYPE                      ("TYPE",   KEYWORD),
	UNSIGNED                      ("UNSIGNED",   KEYWORD),
	UNION                      ("UNION",   KEYWORD),

	// - non-data attribute keywords -
	ABNORMAL                      ("ABNORMAL",   KEYWORD),
	ALIGNED                      ("ALIGNED",   KEYWORD),
	ASSIGNABLE                      ("ASSIGNABLE",   KEYWORD),
	AUTOMATIC                      ("AUTOMATIC",   KEYWORD),
	BASED                      ("BASED",   KEYWORD),
	BIGENDIAN                      ("BIGENDIAN",   KEYWORD),
	BUFFERED                      ("BUFFERED",   KEYWORD),
	BUILTIN                      ("BUILTIN",   KEYWORD),
	BYADDR                      ("BYADDR",   KEYWORD),
	BYVALUE                      ("BYVALUE",   KEYWORD),
	CONDITION                      ("CONDITION",   KEYWORD),
	CONNECTED                      ("CONNECTED",   KEYWORD),
	CONTROLLED                      ("CONTROLLED",   KEYWORD),
	DEFINED                      ("DEFINED",   KEYWORD),
	DIRECT                      ("DIRECT",   KEYWORD),
	ENVIRONMENT                      ("ENVIRONMENT",   KEYWORD),
	EXCLUSIVE                      ("EXCLUSIVE",   KEYWORD),
	EXTERNAL                      ("EXTERNAL",   KEYWORD),
	GENERIC                      ("GENERIC",   KEYWORD),
	HEXADEC                      ("HEXADEC",   KEYWORD),
	IEEE                      ("IEEE",   KEYWORD),
	INITIAL                      ("INITIAL",   "INIT",  KEYWORD),
	INPUT                      ("INPUT",   KEYWORD),
	INTERNAL                      ("INTERNAL",   KEYWORD),
	KEYED                      ("KEYED",   KEYWORD),
	LIKE                      ("LIKE",   KEYWORD),
	LIST                      ("LIST",   KEYWORD),
	LITTLEENDIAN                      ("LITTLEENDIAN",   KEYWORD),
	NONASSIGNABLE                      ("NONASSIGNABLE",   KEYWORD),
	NONCONNECTED                      ("NONCONNECTED",   KEYWORD),
	NORMAL                      ("NORMAL",   KEYWORD),
	OPTIONAL                      ("OPTIONAL",   KEYWORD),
	OPTIONS                      ("OPTIONS",   KEYWORD),
	OUTPUT                      ("OUTPUT",   KEYWORD),
	PARAMETER                      ("PARAMETER",   KEYWORD),
	POSITION                      ("POSITION",   KEYWORD),
	PRINT                      ("PRINT",   KEYWORD),
	RECORD                      ("RECORD",   KEYWORD),
	SEQUENTIAL                      ("SEQUENTIAL",   KEYWORD),
	STATIC                      ("STATIC",   KEYWORD),
	STREAM                      ("STREAM",   KEYWORD),
	UNALIGNED                      ("UNALIGNED",   KEYWORD),
	UNBUFFERED                      ("UNBUFFERED",   KEYWORD),
	UPDATE                      ("UPDATE",   KEYWORD),
	VALUE                      ("VALUE",   KEYWORD),
	VARIABLE                      ("VARIABLE",   KEYWORD),

	// - Statements -
	CALL                      ("CALL",   KEYWORD),
	SUB                      ("SUB",   KEYWORD),
	BY                      ("BY",   KEYWORD),
	NAME                      ("NAME",   KEYWORD),
	CASE                      ("CASE",   KEYWORD),
	CHECK                      ("CHECK",   KEYWORD),
	COPY                      ("COPY",   KEYWORD),
	DEFAULT                      ("DEFAULT",   KEYWORD),
	DFT                      ("DFT",   KEYWORD),
	DELAY                      ("DELAY",   KEYWORD),
	DESCRIPTORS                      ("DESCRIPTORS",   KEYWORD),
	DISPLAY                      ("DISPLAY",   KEYWORD),
	EXIT                      ("EXIT",   KEYWORD),
	FETCH                      ("FETCH",   KEYWORD),
	HALT                      ("HALT",   KEYWORD),
	IGNORE                      ("IGNORE",   KEYWORD),
	LOCATE                      ("LOCATE",   KEYWORD),
	NONE                      ("NONE",   KEYWORD),
	ORDER                      ("ORDER",   KEYWORD),
	RANGE                      ("RANGE",   KEYWORD),
	RELEASE                      ("RELEASE",   KEYWORD),
	REORDER                      ("REORDER",   KEYWORD),
	REPLY                      ("REPLY",   KEYWORD),
	SNAP                      ("SNAP",   KEYWORD),
	SYSTEM                      ("SYSTEM",   KEYWORD),
	TAB                      ("TAB",   KEYWORD),
	UNLOCK                      ("UNLOCK",   KEYWORD),
	WAIT                      ("WAIT",   KEYWORD),
	ALLOCATE                      ("ALLOCATE",   KEYWORD),
	ALLOC                      ("ALLOC",   KEYWORD),
	BEGIN                      ("BEGIN",   KEYWORD),
	CLOSE                      ("CLOSE",   KEYWORD),
	DECLARE                      ("DECLARE",   "DCL", KEYWORD),
	DELETE                      ("DELETE",   KEYWORD),
	DO                      ("DO",   KEYWORD),
	ELSE                      ("ELSE",   KEYWORD),
	END                      ("END",   KEYWORD),
	GET                      ("GET",   KEYWORD),
	GOTO                      ("GOTO",   KEYWORD),
	GO                      ("GO",   KEYWORD),
	TO                      ("TO",   KEYWORD),
	IF                      ("IF",   KEYWORD),
	LEAVE                      ("LEAVE",   KEYWORD),
	ON                      ("ON",   KEYWORD),
	OPEN                      ("OPEN",   KEYWORD),
	OTHERWISE                      ("OTHERWISE",   KEYWORD),
	OTHER                      ("OTHER",   KEYWORD),
	PROCEDURE                      ("PROCEDURE", "PROC",  KEYWORD),
	PACKAGE                      ("PACKAGE",  KEYWORD),
	EXPORTS                      ("EXPORTS",  KEYWORD),
	MAIN                      ("MAIN",  KEYWORD),
	PUT                      ("PUT",   KEYWORD),
	READ                      ("READ",   KEYWORD),
	RESCAN                      ("RESCAN",   KEYWORD),
	RETURN                      ("RETURN",   KEYWORD),
	REVERT                      ("REVERT",   KEYWORD),
	REWRITE                      ("REWRITE",   KEYWORD),
	SELECT                      ("SELECT",   KEYWORD),
	SIGNAL                      ("SIGNAL",   KEYWORD),
	STATEMENT                      ("STATEMENT",   KEYWORD),
	STOP                      ("STOP",   KEYWORD),
	THEN                      ("THEN",   KEYWORD),
	WHEN                      ("WHEN",   KEYWORD),
	WRITE                      ("WRITE",   KEYWORD),

	// on condition keywords
	FINISH                      ("FINISH",   KEYWORD),
	ANYCONDITION                      ("ANYCONDITION",   KEYWORD),


	// PreProcessor keywords
	ACTIVATE                      ("ACTIVATE",   KEYWORD),
	DEACTIVATE                      ("DEACTIVATE",   KEYWORD),
	DICTIONARY                      ("DICTIONARY",   KEYWORD),
	ERROR                      ("ERROR",   KEYWORD),
	FATAL                      ("FATAL",   KEYWORD),
	INCLUDE                      ("INCLUDE",   KEYWORD),
	INFORM                      ("INFORM",   KEYWORD),
	PAGE                      ("PAGE",   KEYWORD),
	REPLACE                      ("REPLACE",   KEYWORD),
	TITLE                      ("TITLE",   KEYWORD),
	WARN                      ("WARN",   KEYWORD),

	// - other -
	LINESIZE                      ("LINESIZE",   KEYWORD),
	PAGESIZE                      ("PAGESIZE",   KEYWORD),
	REREAD                      ("REREAD",   KEYWORD),
	DEFINE                      ("DEFINE",   KEYWORD),
	ALIAS                      ("ALIAS",   KEYWORD),
	WHILE                      ("WHILE",   KEYWORD),
	UNTIL                      ("UNTIL",   KEYWORD),
	LOOP                      ("LOOP",   KEYWORD),

	;

	private String lexeme;
	private String lexemeAlt;
	private PliTokenKindTag tag;
	private PliTokenKindTag tag2;
	private PliOperatorPriority priority;

	PliTokenKind(String lexeme) { this.lexeme = lexeme; }
	PliTokenKind(String lexeme, String lexemeAlt) {
		this.lexeme = lexeme;
		this.lexemeAlt = lexemeAlt;
	}
	PliTokenKind(String lexeme, String lexemeAlt, PliTokenKindTag tag) {
		this.lexeme = lexeme;
		this.lexemeAlt = lexemeAlt;
		this.tag = tag;
	}
	PliTokenKind(String lexeme, String lexemeAlt, PliTokenKindTag tag,
				 PliOperatorPriority priority) {
		this.lexeme = lexeme;
		this.lexemeAlt = lexemeAlt;
		this.tag = tag;
		this.priority = priority;
	}

	PliTokenKind(String lexeme, PliTokenKindTag tag) {
		this.lexeme = lexeme;
		this.tag = tag;
	}
	PliTokenKind(String lexeme, PliTokenKindTag tag, PliOperatorPriority priority) {
		this.lexeme = lexeme;
		this.tag = tag;
		this.priority = priority;
	}

	public PliToken newToken() {
		return new PliToken(this, this.lexeme);
	}

	public PliToken newToken(String lexeme) {
		return new PliToken(this, lexeme);
	}

	static Map<String, PliTokenKind> keywords = new HashMap<>();
	static {
		for (PliTokenKind kind : values()) {
			if (kind.tag == KEYWORD) {
				keywords.put(kind.lexeme, kind);
				keywords.put(kind.lexemeAlt, kind);
			}
		}
	}

	public static PliTokenKind findKeyword(String lexeme) {
		return keywords.get(lexeme.toUpperCase());
	}

	@Override
	public String toString() {
		if (this == PliTokenKind.NEW_LINE) {
			return name() + "('\\n')";
		} else if (tag() == PliTokenKindTag.KEYWORD) {
			return name();
		} else if (this.lexeme != null) {
			return name() + "(" + lexeme() + ")";
		} else if (this.lexemeAlt != null) {
			return name() + "(" + lexeme() + ")";
		} else {
			return name();
		}
	}
}
