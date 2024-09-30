package fc.compiler.language.antlr.traditional;

import fc.compiler.common.lexer.CodeReaderBase;
import fc.compiler.common.lexer.Lexer;
import fc.compiler.common.lexer.LexerBase;

/**
 * Lexer for Antlr-like xml.
 * @author FC
 */
public class FcgLexer extends LexerBase<FcgTokenKind, FcgToken> {

	public FcgLexer(String code) {
		super(code);
	}

	public FcgToken scanToken() {
		onStartToken();
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

	protected FcgToken scanComment() {
		acceptChar('/');
		if (ch == '/') {
			return scanLineComment();
		} else if (ch == '*') {
			return scanBlockComment();
		}
		return lexError("scanComments");
	}

	protected FcgToken scanLineComment() {
		while (ch != EOF_CHAR && ch != CR && ch != LF) {
			nextChar();
		}
		FcgToken token = FcgTokenKind.LINE_COMMENT.newToken(lexeme());
		if (ch != EOF_CHAR)
			scanNewLine();
		return token;
	}

	protected FcgToken scanBlockComment() {
		while (ch != EOF_CHAR) {
			switch (ch) {
				case CR:
				case LF:    acceptLineTerminator();
				case '*':   nextChar();
					if (optionalChar('/')) {
						return FcgTokenKind.BLOCK_COMMENT.newToken(lexeme());
					}
				default:    nextChar();
			}
		}
		return lexError("no enclosed block comment");
	}

	protected FcgToken scanStringLiteral() {
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
		return FcgTokenKind.STRING_LITERAL.newToken(stringLiteralLexeme());
	}

	protected boolean isIdentifierStart() {
		return ('a' <= ch && ch <= 'z') || ('A' <= ch && ch <= 'Z') || ch == '_';
	}

	protected boolean isIdentifierRest() {
		return ('a' <= ch && ch <= 'z') || ('A' <= ch && ch <= 'Z') || ch == '_'
				|| ('0' <= ch && ch <= '9');
	}

	protected FcgToken scanIdentifier() {
		do {
			nextChar();
		} while (isIdentifierRest());

		String lexeme = lexeme();
		FcgTokenKind kind = FcgTokenKind.findKeyword(lexeme);
		if (kind != null) {
			return kind.newToken(lexeme);
		}

		return FcgTokenKind.IDENTIFIER.newToken(lexeme);
	}

	protected boolean isNumberStart() {
		return Character.isDigit(ch);
	}

	protected boolean isNumberRest() {
		return Character.isDigit(ch);
	}

	protected FcgToken scanNumber() {
		do {
			nextChar();
		} while (isNumberRest());
		return FcgTokenKind.NUMBER_LITERAL.newToken(lexeme());
	}

	protected FcgToken scanWhiteSpaces() {
		do {
			nextChar();
		} while (isWhiteSpace());
		return FcgTokenKind.WHITE_SPACES.newToken(lexeme());
	}

	protected FcgToken scanNewLine() {
		optionalChar(CR);
		optionalChar(LF);
		lineNo++;
		lineStartPosition = bp;
		return FcgTokenKind.NEW_LINE.newToken(lexeme());
	}

	protected FcgToken lexError(String hint) {
		logError(hint);
		nextChar();
		return FcgTokenKind.ERROR_TOKEN.newToken();
	}

}
