package fc.compiler.language.antlr.traditional;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

import static fc.compiler.language.antlr.traditional.FcgTokenKindTag.*;

/**
 * @author FC
 */
@Getter @Accessors(fluent = true, chain = true)
@NoArgsConstructor
public enum FcgTokenKind {
	// -- ERROR --
	ERROR_TOKEN,

	// -- EOF --
	EOF,

	// -- white space --
	WHITE_SPACES,
	NEW_LINE,

	// -- comments --
	LINE_COMMENT            (null, COMMENT),
	BLOCK_COMMENT           (null, COMMENT),
	DOC_COMMENT             (null, COMMENT),

	// -- literals --
	STRING_LITERAL,
	NUMBER_LITERAL,

	// -- separators (punctuators) --
	SEMICOLON		        (";"),
	COMMA		            (","),
	COLON                   (":"),
	COLON_COLON             ("::"),
	SLASH                   ("/"),
	QUESTION                ("?"),
	STAR                    ("*"),
	PLUS                    ("+"),
	LEFT_PAREN              ("("),
	LEFT_BRACKET            ("["),
	LEFT_BRACE              ("{"),
	RIGHT_PAREN             (")"),
	RIGHT_BRACKET           ("]"),
	RIGHT_BRACE             ("}"),
	BAR                     ("|"),
	AT                      ("@"),
	DOLLAR                  ("$"),
	POUND                   ("#"),
	RANGE                   ("-"),
	SINGLE_QUOTE            ("'"),
	EQ                      ("="),
	LT                      ("<"),
	GT                      (">"),
	NOT                     ("~"),
	PLUS_ASSIGNMENT         ("+="),

	// -- IDENTIFIER --
	IDENTIFIER              (null),

	// -- Keywords --
	LEXER                   ("lexer",   KEYWORD),
	PARSER                  ("parser",  KEYWORD),
	GRAMMAR                 ("grammar", KEYWORD),
	IMPORT                  ("import",  KEYWORD),
	FRAGMENT                ("fragment",  KEYWORD),
	PROTECTED	            ("protected", KEYWORD),
	PUBLIC	 	            ("public", KEYWORD),
	PRIVATE	 	            ("private", KEYWORD),
	RETURNS	 	            ("returns", KEYWORD),
	LOCALS	 	            ("locals", KEYWORD),
	THROWS	 	            ("throws", KEYWORD),
	CATCH	 	            ("catch", KEYWORD),
	FINALLY	 	            ("finally", KEYWORD),
	MODE	 	            ("mode", KEYWORD),
	;

	private String lexeme;
	private FcgTokenKindTag tag;

	FcgTokenKind(String lexeme) {
		this.lexeme = lexeme;
	}

	FcgTokenKind(String lexeme, FcgTokenKindTag tag) {
		this.lexeme = lexeme;
		this.tag = tag;
	}

	public FcgToken newToken() {
		return new FcgToken(this, this.lexeme);
	}

	public FcgToken newToken(String lexeme) {
		return new FcgToken(this, lexeme);
	}

	static Map<String, FcgTokenKind> keywords = new HashMap<>();
	static {
		for (FcgTokenKind kind : values()) {
			if (kind.tag == KEYWORD) {
				keywords.put(kind.lexeme, kind);
			}
		}
	}

	public static FcgTokenKind findKeyword(String lexeme) {
		return keywords.get(lexeme);
	}

	public boolean isAnyOf(FcgTokenKind... kinds) {
		for (FcgTokenKind kind : kinds) {
			if (this == kind)
				return true;
		}
		return false;
	}
}
