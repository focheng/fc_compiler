package fc.compiler.language.antlr.traditional;

/**
 * Lexer for Antlr-like xml.
 * @author FC
 */
public class FcgLexer {
	protected static final char EOF_CHAR   = 0x1A;   // control-z, End of File.
	protected static final char SPACE      = ' ';
	protected static final char TAB        = '\t';
	protected static final char VT         = 0x0B;
	protected static final char FF         = 0x0C;
	protected static final char CR         = '\r';
	protected static final char LF         = '\n';

	protected char[] code;
	protected char   ch;
	protected int    bp = -1;
	protected int    spLexeme = 0;
	protected int    lineNo = 1;
	protected int    lineStartPosition;

	public FcgLexer(String text) {
		this.code = toCharArrayPlusEof(text);

		nextChar();
	}

	public FcgToken scanToken() {
		resetTokenContext();
		switch (ch) {
			case EOF_CHAR:                  return FcgTokenKind.EOF.newToken();
			case CR:
			case LF:                        return scanNewLine();
			case SPACE:
			case TAB:
			case VT:
			case FF:                        return scanWhiteSpaces();
			case '/':                       return scanComment();
			case ';':           nextChar(); return FcgTokenKind.SEMICOLON.newToken();
			case ':':           nextChar(); return FcgTokenKind.COLON.newToken();
			case ',':           nextChar(); return FcgTokenKind.COMMA.newToken();
			case '+':           nextChar(); return FcgTokenKind.PLUS.newToken();
			case '?':           nextChar(); return FcgTokenKind.QUESTION.newToken();
			case '*':           nextChar(); return FcgTokenKind.STAR.newToken();
			case '|':           nextChar(); return FcgTokenKind.BAR.newToken();
			case '(':           nextChar(); return FcgTokenKind.LEFT_PAREN.newToken();
			case '[':           nextChar(); return FcgTokenKind.LEFT_BRACKET.newToken();
			case '{':           nextChar(); return FcgTokenKind.LEFT_BRACE.newToken();
			case ')':           nextChar(); return FcgTokenKind.RIGHT_PAREN.newToken();
			case ']':           nextChar(); return FcgTokenKind.RIGHT_BRACKET.newToken();
			case '}':           nextChar(); return FcgTokenKind.RIGHT_BRACE.newToken();
			case '@':           nextChar(); return FcgTokenKind.AT.newToken();
			case '\'':                      return scanStringLiteral();
			default:
				if (isIdentifierStart()) {
					return scanIdentifier();
				} else if (isNumberStart()) {
					return scanNumber();
				}
				return lexError("scanToken()");
		}
	}

	private FcgToken scanComment() {
		acceptChar('/');
		if (ch == '/') {
			return scanLineComment();
		} else if (ch == '*') {
			return scanBlockComment();
		}
		return lexError("scanComments");
	}

	private FcgToken scanLineComment() {
		while (ch != EOF_CHAR && ch != CR && ch != LF) {
			nextChar();
		}
		FcgToken token = FcgTokenKind.LINE_COMMENT.newToken(getLexeme());
		if (ch != EOF_CHAR)
			scanNewLine();
		return token;
	}

	private FcgToken scanBlockComment() {
		while (ch != EOF_CHAR) {
			switch (ch) {
				case CR:
				case LF:    acceptLineTerminator();
				case '*':   nextChar();
					if (optionalChar('/')) {
						return FcgTokenKind.BLOCK_COMMENT.newToken(getLexeme());
					}
				default:    nextChar();
			}
		}
		return lexError("no enclosed block comment");
	}

	private FcgToken scanStringLiteral() {
		acceptChar('\'');
		while (ch != EOF_CHAR) {
			if (ch == CR || ch == LF) {
				scanNewLine();
			}
			if (ch == '\'') {
				nextChar();
				break;
			}
			nextChar();
		}
		return FcgTokenKind.STRING_LITERAL.newToken(getStringLiteralLexeme());
	}

	private boolean isIdentifierStart() {
		return ('a' <= ch && ch <= 'z') || ('A' <= ch && ch <= 'Z');
	}

	private FcgToken scanIdentifier() {
		do {
			nextChar();
		} while (('a' <= ch && ch <= 'z') || ('A' <= ch && ch <= 'Z')
				|| ('0' <= ch && ch <= '9') || ch == '_');

		String lexeme = getLexeme();
		FcgTokenKind kind = FcgTokenKind.findKeyword(lexeme);
		if (kind != null) {
			return kind.newToken(lexeme);
		}

		return FcgTokenKind.IDENTIFIER.newToken(lexeme);
	}

	private boolean isNumberStart() {
		return Character.isDigit(ch);
	}

	private FcgToken scanNumber() {
		do {
			nextChar();
		} while (isNumberStart());
		return FcgTokenKind.IDENTIFIER.newToken(getLexeme());
	}

	private boolean isWhiteSpace() {
		return ch == SPACE || ch == TAB || ch == FF || ch == VT;
	}

	private FcgToken scanWhiteSpaces() {
		do {
			nextChar();
		} while (isWhiteSpace());
		return FcgTokenKind.WHITE_SPACES.newToken(getLexeme());
	}

	private FcgToken scanNewLine() {
		optionalChar(CR);
		optionalChar(LF);
		lineNo++;
		lineStartPosition = bp;
		return FcgTokenKind.NEW_LINE.newToken(getLexeme());
	}

	// -- helper --

	private void resetTokenContext() {
		spLexeme = bp;
	}

	private String getLexeme() {
		return String.copyValueOf(code, spLexeme, bp - spLexeme);
	}

	private String getStringLiteralLexeme() {
		return String.copyValueOf(code, spLexeme + 1, bp - spLexeme - 2);
	}

	private FcgToken lexError(String hint) {
		logError(hint);
		nextChar();
		return FcgTokenKind.ERROR_TOKEN.newToken();
	}

	private void logError(String hint) {
		System.out.println("lex error: unsupported char " + ch + " " + hint);
	}

	// -- character stream --

	/**
	 * Read and return the next character.
	 * @return next character.
	 */
	public char nextChar() {
		++bp;
		if (bp < code.length) {
			ch = code[bp];
		} else {
			ch = EOF_CHAR;
		}
		return ch;
	}

	private boolean acceptChar(char expected) {
		boolean accepted = expected == ch;
		if (accepted)
			nextChar();
		else
			logError(expected + " is expected, but " + ch + " scanned");
		return accepted;
	}

	private boolean acceptChar(char... expectedChars) {
		for (char expected : expectedChars) {
			if (expected != ch) {
				logError(expected + " is expected, but " + ch + " scanned");
				return false;
			}
			nextChar();
		}
		return true;
	}

	private boolean acceptChar(String expectedChars) {
		return acceptChar(expectedChars.toCharArray());
	}

	private boolean optionalChar(char expected) {
		boolean accepted = expected == ch;
		if (accepted)
			nextChar();
		return accepted;
	}

	private boolean optionalChar(char... expectedChars) {
		for (char expected : expectedChars) {
			if (expected != ch) {
				return false;
			}
			nextChar();
		}
		return true;
	}

	private boolean optionalChar(String expectedChars) {
		return optionalChar(expectedChars.toCharArray());
	}

	public boolean acceptLineTerminator() {
		boolean hasCR = optionalChar(CR);
		boolean hasLF = optionalChar(LF);
		if (hasCR || hasLF) {
			lineNo++;
			lineStartPosition = bp;
			return true;
		} else {
			return false;
		}
	}

	public static char[] toCharArrayPlusEof(String code) {
		char[] array = new char[code.length() + 1];
		code.getChars(0, code.length(), array, 0);
		array[code.length()] = EOF_CHAR;
		return array;
	}
}
