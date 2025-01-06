package fc.compiler.language.pli;

import fc.compiler.common.lexer.LexerBase;

import static fc.compiler.language.pli.PliTokenKind.*;
import static fc.compiler.language.pli.PliTokenKind.STRING_LITERAL;

/**
 * PLI (PL/1) lexer.
 * @author FC
 */
public class PliLexer extends LexerBase<PliTokenKind, PliToken> {

	public PliLexer(String code) {
		super(code);
	}

	public PliToken scanToken() {
		onStartToken();
		switch (ch) {
			case EOF_CHAR:                  return EOF.newToken();
			case CR:
			case LF:                        return scanNewLine();
			case SPACE:
			case TAB:
			case VT:
			case FF:                        return scanWhiteSpaces();
			case '"':
			case '\'':                      return scanStringLiteral();
			case ',':           nextChar(); return COMMA.newToken();
			case ';':           nextChar(); return SEMICOLON.newToken();
			case ':':           nextChar(); return COLON.newToken();
			case '%':           nextChar(); return PERCENT.newToken();
			case '(':           nextChar(); return LEFT_PAREN.newToken();
			case ')':           nextChar(); return RIGHT_PAREN.newToken();

			case '+':           nextChar(); return twoCharToken('=', PLUS_EQ, PLUS);
			case '>':           nextChar(); return twoCharToken('=', GE, GT);
			case '<':           nextChar(); return twoCharToken('=', LE, LT);
			case '&':           nextChar(); return twoCharToken('=', AND_EQ, AND);
			case '=':           nextChar(); return twoCharToken('>', LOCATOR_HANDLE, EQ);
			case '*':           nextChar();
				switch (ch) {
					case '*':   nextChar();
						switch (ch) {
							case '=':
								nextChar();	return STAR_STAR_EQ.newToken();
							default:
											return STAR_STAR.newToken();
						}
					case '=':   nextChar(); return STAR_EQ.newToken();
					default:                return STAR.newToken();
				}
			case '-':           nextChar();
				switch (ch) {
					case '>':   nextChar(); return LOCATOR_POINTER.newToken();
					case '=':   nextChar(); return MINUS_EQ.newToken();
					default:                return MINUS.newToken();
				}
			case '?':           nextChar();
				switch (ch) {
					case '>':   nextChar(); return NOT_GT.newToken();
					case '<':   nextChar(); return NOT_LT.newToken();
					case '=':   nextChar(); return NOT_EQ.newToken();
					default:                return NOT.newToken();
				}
			case '|':           nextChar();
				switch (ch) {
					case '|':   nextChar(); return twoCharToken('=', BAR_BAR_EQ, BAR_BAR);
					case '=':   nextChar(); return BAR_EQ.newToken();
					default:                return BAR.newToken();
				}
			case '/':           nextChar();
				switch (ch) {
					case '*':   nextChar(); return scanBlockComment();
					case '=':   nextChar(); return SLASH_EQ.newToken();
					default:                return SLASH.newToken();
				}
			case '.':           nextChar();
				if ('0' <= ch && ch <= '9') {
					return scanNumber(true);
				} else {
					return DOT.newToken();
				}
			default:
				if (isIdentifierStart()) {
					return scanIdentifier();
				} else if (isNumberStart()) {
					return scanNumber();
				}
				return lexError("scanToken()");
		}
	}

	private PliToken twoCharToken(char char2nd,
	                              PliTokenKind kindOf2Chars, PliTokenKind kindOf1Char) {
		if (optionalChar(char2nd)) {
			return kindOf2Chars.newToken();
		} else {
			return kindOf1Char.newToken();
		}
	}

	private PliToken threeCharToken(char char2nd, char char3rd,
	                                PliTokenKind kindOf3Chars, PliTokenKind kindOf2Chars, PliTokenKind kindOf1Char) {
		if (optionalChar(char2nd)) {
			if (optionalChar(char3rd)) {
				return kindOf3Chars.newToken();
			} else {
				return kindOf2Chars.newToken();
			}
		} else {
			return kindOf1Char.newToken();
		}
	}

	protected PliToken scanBlockComment() {
		while (ch != EOF_CHAR) {
			switch (ch) {
				case CR:
				case LF:    acceptLineTerminator();     break;
				case '*':   nextChar();
					if (optionalChar('/')) {
						return BLOCK_COMMENT.newToken(lexeme());
					}
					break;
				default:    nextChar();
			}
		}
		return lexError("no enclosed block comment");
	}

	// -- IDENTIFIER --

	protected boolean isIdentifierStart() {
		return ('a' <= ch && ch <= 'z') || ('A' <= ch && ch <= 'Z') || ch == '_'
				|| ch == '$' || ch == '#' || ch == '@' || ch == '!';
	}

	protected boolean isIdentifierRest() {
		return ('a' <= ch && ch <= 'z') || ('A' <= ch && ch <= 'Z') || ch == '_'
				|| ('0' <= ch && ch <= '9')
				|| ch == '$' || ch == '#' || ch == '@' || ch == '!';
	}

	protected PliToken scanIdentifier() {
		do {
			nextChar();
		} while (isIdentifierRest());

		String lexeme = lexeme();
		PliTokenKind kind = findKeyword(lexeme);
		if (kind != null) {
			return kind.newToken(lexeme);
		}

		return IDENTIFIER.newToken(lexeme);
	}

	// -- NUMBER_LITERAL --

	protected boolean isNumberStart() {
		return ('0' <= ch && ch <= '9');
	}

	protected boolean isNumberRest() {
		return ('0' <= ch && ch <= '9') || ch == '_';
	}

	/**
	 * Scan decimal or binary numbers.
	 * Hex number must be quoted because the suffix 'B' is a hex digit.
	 * @return
	 */
	protected PliToken scanNumber() { return scanNumber(false); }
	protected PliToken scanNumber(boolean seenDot) {
		if (seenDot) {
			return scanNumberFraction(false);
		} else {
			do {
				nextChar();
			} while (isNumberRest());
			boolean seenScientificNotation = scanOptionalScientificNotation();
			if (ch == '.') {
				nextChar();
				return scanNumberFraction(seenScientificNotation);
			} else {
				return newNumberLiteralToken(false, seenScientificNotation);
			}
		}
	}

	private PliToken scanNumberFraction(boolean seenScientificNotation) {
		while (isNumberRest()) {
			nextChar();
		}

		boolean seenAnother = scanOptionalScientificNotation();
		if (seenScientificNotation && seenAnother)
			lexError("multiple scientific notation");

		return newNumberLiteralToken(true, seenScientificNotation);
	}

	private PliToken newNumberLiteralToken(boolean seenDot,
													boolean seenScientificNotation) {
		PliToken token = NUMBER_LITERAL.newToken(lexeme());
		int radix = 10;
		switch (ch) {
			case 'b': case 'B': radix = 2; 	nextChar();	break;
			case 'x': case 'X': radix = 16;	nextChar();	break;
			default:			radix = 10;
		}

		String lexeme = token.lexeme();
		// remove optional readability supporting '_'
		if (lexeme.indexOf('_') > -1)
			lexeme = lexeme.replaceAll("_", "");
		if ((seenDot || seenScientificNotation) && radix != 10) {
			lexeme = convertToDecimalString(lexeme, radix);
		}
		Object value = null;
		if (seenDot || seenScientificNotation)
			value = Double.parseDouble(lexeme);
		else
			value = Integer.parseInt(lexeme, radix);
		token.radix(radix).value(value);
		return token;
	}

	private String convertToDecimalString(String lexeme, int radix) {
		StringBuilder sb = new StringBuilder();

		// deal '.'
		int dotIndex = lexeme.indexOf('.');
		if (dotIndex >= 0) {
			if (dotIndex > 0)
				sb.append(Integer.parseInt(lexeme.substring(0, dotIndex), radix));
			sb.append(".");
		}
		int beginIndex = dotIndex >= 0 ? dotIndex+1 : 0;

		// deal 'e' or 'E' of scientific notation
		int eIndex = lexeme.indexOf('e');
		if (eIndex == -1)
			eIndex = lexeme.indexOf('E');
		if (eIndex >= 0) {
			sb.append(Integer.parseInt(lexeme.substring(beginIndex, eIndex), radix))
					.append(lexeme.charAt(eIndex))
					.append(Integer.parseInt(lexeme.substring(eIndex+1)));
		} else {
			sb.append(Integer.parseInt(lexeme.substring(beginIndex, lexeme.length()), radix));
		}

		return sb.toString();
	}

	private boolean scanOptionalScientificNotation() {
		if (optionalAnyOf('e', 'E')) {
			optionalAnyOf('+', '-');
			if ('0' <= ch && ch <= '9') {
				do {
					nextChar();
				} while (isNumberRest());
			} else {
				lexError("malformed scientific notation for float point literal");
			}
			return true;
		} else {
			return false;
		}
	}

	// -- single/double quoted STRING_LITERAL --

	protected PliToken scanStringLiteral() {
		char quote = ch;
		acceptChar(ch);
		while (ch != EOF_CHAR) {
			if (ch == CR || ch == LF) {
				scanNewLine();
			}
			if (ch == quote) {
				nextChar();
				if (ch == quote) {
					// double quotes is translated to one quote.
				} else {
					break;
				}
			}
			nextChar();
		}
		String value = stripAndDoubleQuote(lexeme(), quote);
		PliToken token = STRING_LITERAL.newToken(value);
		scanStringLiteralSuffix(token);
		return token;
	}

	private void scanStringLiteralSuffix(PliToken token) {
		if (!isAnyOf('x', 'X', 'b', 'B', 'w', 'W', 'm', 'M', 'g', 'G'))
			return;

		StringBuilder sb = new StringBuilder();
		switch (ch) {
			case 'x': case 'X':
				sb.append(ch);	nextChar();
				if (ch == 'n' || ch == 'N'
						|| ch == 'u' || ch == 'U') {
					sb.append(ch);	nextChar();
				}
				break;
			case 'b': case 'B':	sb.append(ch);	nextChar();
				if (ch == '4' || ch == '3') {
					sb.append(ch);	nextChar();
				}
				break;
			case 'g': case 'G':
			case 'w': case 'W':
				sb.append(ch);	nextChar();
				if (ch == 'x' || ch == 'X') {
					sb.append(ch);	nextChar();
				}
				break;
			case 'm': case 'M':
				sb.append(ch);	nextChar();
				break;
		}
		token.attribute("suffix", sb.toString());
		//token.value(convertStringLiteral(sb.toString()));	// TODO
	}

	private static String stripAndDoubleQuote(String lexeme, char quote) {
		// trim the leading and trailing quote marks
		lexeme = lexeme.substring(1, lexeme.length() - 1);

		// repalce double quote marks with single quote mark
		if (quote == '\'') {
			lexeme = lexeme.replaceAll("''", "'");
		} else {
			lexeme = lexeme.replaceAll("\"\"", "\"");
		}

		return lexeme;
	}

	// -- WHITE_SPACES/NEW_LINE --

	protected PliToken scanWhiteSpaces() {
		do {
			nextChar();
		} while (isWhiteSpace());
		return WHITE_SPACES.newToken(lexeme());
	}

	protected PliToken scanNewLine() {
		optionalChar(CR);
		optionalChar(LF);
		lineNo++;
		lineStartPosition = bp;
		return NEW_LINE.newToken(lexeme());
	}

	protected PliToken lexError(String hint) {
		logError(hint);
		nextChar();
		return ERROR_TOKEN.newToken();
	}

}
