package fc.compiler.language.cobol;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

import static fc.compiler.language.cobol.CobolTokenKindTag.*;

/**
 * @author FC
 */
@Getter @Accessors(fluent = true, chain = true)
@NoArgsConstructor
public enum CobolTokenKind {
	// -- ERROR --
	ERROR_TOKEN,

	// -- EOF --
	EOF,

	// -- white space --
	WHITE_SPACES,
	NEW_LINE,

	// -- comments --
	LINE_COMMENT            (null, COMMENT),

	// -- literals --
	STRING_LITERAL,

	// -- separators (punctuators) --
	DOT			("."),

	// -- IDENTIFIER --
	IDENTIFIER              (null),

	// -- Keywords --
	ENVIRONMENT			("ENVIRONMENT", KEYWORD),
	SOURCE_COMPUTER			("SOURCE_COMPUTER", KEYWORD),
	END_IF			("END_IF", KEYWORD),
	______			("REMARKS", KEYWORD),
	SENTENCE			("SENTENCE", KEYWORD),
	_____			("AUTHOR", KEYWORD),
	PROGRAM_ID			("PROGRAM_ID", KEYWORD),
	DIVISION			("DIVISION", KEYWORD),
	WORKING_STORAGE			("WORKING_STORAGE", KEYWORD),
	INPUT_OUTPUT			("INPUT_OUTPUT", KEYWORD),
	OPTIONAL			("OPTIONAL", KEYWORD),
	RELATIVE			("RELATIVE", KEYWORD),
	NEXT			("NEXT", KEYWORD),
	FILE			("FILE", KEYWORD),
	IF			("IF", KEYWORD),
	____MINUS_______			("DATE-WRITTEN", KEYWORD),
	____MINUS________			("DATE-COMPILED", KEYWORD),
	PROCEDURE			("PROCEDURE", KEYWORD),
	STOP			("STOP", KEYWORD),
	FILE_CONTROL			("FILE_CONTROL", KEYWORD),
	CONFIGURATION			("CONFIGURATION", KEYWORD),
	IS			("IS", KEYWORD),
	SELECT			("SELECT", KEYWORD),
	USING			("USING", KEYWORD),
	PASSWORD			("PASSWORD", KEYWORD),
	DATA			("DATA", KEYWORD),
	___________			("INSTALLATION", KEYWORD),
	OBJECT_COMPUTER			("OBJECT_COMPUTER", KEYWORD),
	_______			("SECURITY", KEYWORD),
	END			("END", KEYWORD),
	continueStatement			("continueStatement", KEYWORD),
	THEN			("THEN", KEYWORD),
	LINKAGE			("LINKAGE", KEYWORD),
	PROGRAM			("PROGRAM", KEYWORD),
	SECTION			("SECTION", KEYWORD),
	KEY			("KEY", KEYWORD),
	EXIT			("EXIT", KEYWORD),


	;

	private String lexeme;
	private CobolTokenKindTag tag;

	CobolTokenKind(String lexeme) {
		this.lexeme = lexeme;
	}

	CobolTokenKind(String lexeme, CobolTokenKindTag tag) {
		this.lexeme = lexeme;
		this.tag = tag;
	}

	public CobolToken newToken() {
		return new CobolToken(this, this.lexeme);
	}

	public CobolToken newToken(String lexeme) {
		return new CobolToken(this, lexeme);
	}

	static Map<String, CobolTokenKind> keywords = new HashMap<>();
	static {
		for (CobolTokenKind kind : values()) {
			if (kind.tag == KEYWORD) {
				keywords.put(kind.lexeme, kind);
			}
		}
	}

	public static CobolTokenKind findKeyword(String lexeme) {
		return keywords.get(lexeme);
	}

	public boolean isAnyOf(CobolTokenKind... kinds) {
		for (CobolTokenKind kind : kinds) {
			if (this == kind)
				return true;
		}
		return false;
	}
}
