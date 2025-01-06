package fc.compiler.language.cobol;

import fc.compiler.common.lexer.CodeReaderBase;
import fc.compiler.common.lexer.Lexer;
import fc.compiler.common.lexer.LexerBase;

/**
 * Lexer for Antlr-like xml.
 * @author FC
 */
public class CobolLexer extends LexerBase<CobolTokenKind, CobolToken> {

	public CobolLexer(String code) {
		super(code);
	}

	public CobolToken scanToken() {
		resetTokenContext();
		switch (ch) {
			case EOF_CHAR:                  return EOF.newToken();
			case '\r':
			case '\n':                      return scanNewLine();
			case ' ':
			case '\t':
			case VT:
			case FF:                        return scanNewLine();
			case '.':	nextChar();	return DOT.nextToken();
			default:
				if (isIdentifierStart(ch))
					return scanIdentifier();
				return lexError("scanToken()");
		}
	}


	protected boolean isIdentifierStart() {
		return ('a' <= ch && ch <= 'z') || ('A' <= ch && ch <= 'Z') || ch == '_';
	}

	protected boolean isIdentifierRest() {
		return ('a' <= ch && ch <= 'z') || ('A' <= ch && ch <= 'Z') || ch == '_'
				|| ('0' <= ch && ch <= '9');
	}

	protected CobolToken scanIdentifier() {
		do {
			nextChar();
		} while (isIdentifierRest());

		String lexeme = lexeme();
		CobolTokenKind kind = CobolTokenKind.findKeyword(lexeme);
		if (kind != null) {
			return kind.newToken(lexeme);
		}

		return CobolTokenKind.IDENTIFIER.newToken(lexeme);
	}

	protected boolean isNumberStart() {
		return Character.isDigit(ch);
	}

	protected boolean isNumberRest() {
		return Character.isDigit(ch);
	}


	protected CobolToken scanWhiteSpaces() {
		do {
			nextChar();
		} while (isWhiteSpace());
		return CobolTokenKind.WHITE_SPACES.newToken(lexeme());
	}

	protected CobolToken scanNewLine() {
		optionalChar(CR);
		optionalChar(LF);
		lineNo++;
		lineStartPosition = bp;
		return CobolTokenKind.NEW_LINE.newToken(lexeme());
	}

	protected CobolToken lexError(String hint) {
		logError(hint);
		nextChar();
		return CobolTokenKind.ERROR_TOKEN.newToken();
	}
}
