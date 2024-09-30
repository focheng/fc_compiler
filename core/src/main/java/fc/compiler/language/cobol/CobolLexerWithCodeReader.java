package fc.compiler.language.cobol;

import fc.compiler.common.lexer.*;
import fc.compiler.common.token.StringToken;
import fc.compiler.common.token.StringTokenKind;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;

import static fc.compiler.common.lexer.Constants.*;
import static fc.compiler.common.lexer.Constants.CR;
import static fc.compiler.common.lexer.Constants.SPACE;
import static fc.compiler.common.token.StringTokenKind.*;
import static fc.compiler.language.cobol.CobolTokenKind.*;

/**
 * Four types of COBOL character sets:
 * - Alphabetic character set: A-Za-z and space
 * - Numeric character set: 0-9
 * - Special character set: +-/*=$,;."()<>:&
 * - Special character set extended by NetCOBOL: _
 * - National character set extended by NetCOBOL: Japanese character set
 * @author FC
 */
@Slf4j
public class CobolLexerWithCodeReader extends LexerWithCodeReaderBase {
	protected CobolCompilerOptions options;
	@Getter protected boolean previousTokenLineTerminator = true;

	public CobolLexerWithCodeReader() {
		this.mapper = initLexerMapper();
		initReservedKeywords();
	}

	public StringToken scanToken(CodeReaderBase reader) {
		reader.onStartToken();
		if (reader.isEndOfLine()) {
			previousTokenLineTerminator = true;
			return scanLineTerminator(reader);
		} else if (previousTokenLineTerminator) {
			previousTokenLineTerminator = false;
			switch (reader.ch) {
				case '*':
				case '/':
					return scanLineComment(reader);
				//case '-':
				case 'D':
					if (!options.debugMode) {
						reader.skipToEndOfLine();
						return new StringToken("IGNORED DEBUG CODE", reader.lexeme(), reader.position);
					} else { // ignore this character.
						reader.nextChar();
						break;
					}
				default:
//					if (reader.ch != SPACE && !Character.isWhitespace(reader.ch)) {
//						Token token = lexError(reader, "Unsupported line indicator");
//						reader.nextChar();
//						return token;
//					}
			}
		}
		return super.scanToken(reader);
	}

	public LexerMapper initLexerMapper() {
		LexerMapper mapper = new LexerMapper();
		mapper.mapLexer(Constants.EOF,    LexerWithCodeReaderBase::scanEOF);

		mapper.mapLexer(SPACE,  LexerWithCodeReaderBase::scanWhiteSpaces);
		mapper.mapLexer(TAB,    LexerWithCodeReaderBase::scanWhiteSpaces);
		mapper.mapLexer(FF,     LexerWithCodeReaderBase::scanWhiteSpaces);
		mapper.mapLexer(LF,     LexerWithCodeReaderBase::scanLineTerminator);
		mapper.mapLexer(CR,     LexerWithCodeReaderBase::scanLineTerminator);

		for (char c = 'a'; c <= 'z'; c++) mapper.mapLexer(c, CobolLexerWithCodeReader::scanIdentifier);
		for (char c = 'A'; c <= 'Z'; c++) mapper.mapLexer(c, CobolLexerWithCodeReader::scanIdentifier);
		for (char c = '0'; c <= '9'; c++) mapper.mapLexer(c, CobolLexerWithCodeReader::onDigit);

		// separators
		mapper.mapLexer(',', CobolLexerWithCodeReader::onComma);	    // Comma
		mapper.mapLexer(';', CobolLexerWithCodeReader::onSemicolon);	// Semicolon
		mapper.mapLexer('.', CobolLexerWithCodeReader::onPeriod);	    // Period or decimal point
		mapper.mapLexer('(', LexerWithCodeReaderBase::scanLeftParen);	// Left parenthesis
		mapper.mapLexer(')', LexerWithCodeReaderBase::scanRightParen);// Right parenthesis
		mapper.mapLexer(':', LexerWithCodeReaderBase::scanColon);	    // Colon
		mapper.mapLexer('&', reader -> scanSingleCharToken(reader, AMPERSAND));	// Ampersand
		mapper.mapLexer('=', CobolLexerWithCodeReader::onEqual);	// Equal sign
		mapper.mapLexer('*', LexerWithCodeReaderBase::scanLineComment);	// Asterisk

//		mapper.mapLexer('+', );	// Plus sign
//		mapper.mapLexer('-', );	// Minus sign or hyphen
//		mapper.mapLexer('/', );	// Slash
//		mapper.mapLexer('$', );	// Dollar sign
		mapper.mapLexer('>', CobolLexerWithCodeReader::onGT);	// Greater-than sign
		mapper.mapLexer('<', CobolLexerWithCodeReader::onLT);	// Less-than sign
//		mapper.mapLexer('_', );	// Underscore

		mapper.mapLexer('\'', CobolLexerWithCodeReader::onSingleQuote);
		mapper.mapLexer('\"', LexerWithCodeReaderBase::scanStringLiteral);

		mapper.setDefaultLexer(LexerWithCodeReaderBase::scanDummy);
		return mapper;
	}

	public void initReservedKeywords() {
		try {
			for (Field field : CobolTokenKind.class.getFields()) {
				if (field.getType().equals(String.class)) {
					String keyword = (String) field.get(null);
					reservedKeywords.put(keyword, keyword);
				}
			}
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	// -- line terminator --

	/** -- comments --
	 * - [obsolete] comment entry: any entry after keywords in IDENTIFICATION DIVISION.
	 *      It consists of any characters belonging to the computer character set.
	 * - full-line comment: Any line starting with an asterisk (*) in indicator area.
	 * - An inline comment should start with a floating comment indicator (*>).
	 */


	/** -- separator: a single or group of characters that separates words or strings --
	 * - separator space: delimits a COBOL word, literal, or character string in a PICTURE clause.
	 *      can be included immediately before or after a period, comma and semicolon.
	 * - separator comma: a sequence of one comma followed by one or more spaces.
	 * - separator semicolon: a sequence of one semicolon followed by one or more spaces.
	 *      comma and semicolon can be used wherever a separator space can be used.
	 * - separator period: a sequence of one period followed by one or more spaces.
	 *      used to indicate the end of the declaration a division, section, paragraph or sentence.
	 * - Colon: Used in reference modification and COPY statement with REPLACING.
	 * - Pseudo-text delimiter(==):	Used to replace the string with a COPY statement.
	 *      COPY copybook-name REPLACING ==:WS:== BY ==WS1==.
	 */

	public static StringToken onComma(CodeReaderBase reader) {
		return scanSeparator(reader, ',', COMMA);
	}

	public static StringToken onSemicolon(CodeReaderBase reader) {
		return scanSeparator(reader, ';', SEMICOLON);
	}

	public static StringToken onPeriod(CodeReaderBase reader) {
		return scanSeparator(reader, '.', DOT);
	}

	public static StringToken scanSeparator(CodeReaderBase reader, char leadingChar, String tokenKind) {
		reader.optionalChar(leadingChar);
		if (Character.isWhitespace(reader.ch) || reader.ch == Constants.EOF) {
			reader.nextChar();
			for (; Character.isWhitespace(reader.ch); reader.nextChar()) {}
			return new StringToken(tokenKind, reader.lexeme(), reader.position);
		} else {
			return lexError(reader, "Separator " + leadingChar + " is not followed by space");
		}
	}

	private static StringToken onEqual(CodeReaderBase reader) {
		reader.optionalChar('=');
		if (reader.optionalChar('=')) {
			/** The == pseudo-text delimiter */
			return new StringToken("PSEUDO_TEXT", reader.lexeme(), reader.position);
		} else {
			return new StringToken(EQUAL, reader.lexeme(), reader.position);
		}
	}

	private static StringToken onGT(CodeReaderBase reader) {
		reader.optionalChar('>');
		if (reader.optionalChar('=')) {
			return new StringToken(GT_EQUAL, reader.lexeme(), reader.position);
		} else {
			return new StringToken(GT, reader.lexeme(), reader.position);
		}
	}

	private static StringToken onLT(CodeReaderBase reader) {
		reader.optionalChar('<');
		if (reader.optionalChar('=')) {
			return new StringToken(LT_EQUAL, reader.lexeme(), reader.position);
		} else if (reader.optionalChar('>')) {
			return new StringToken(NOT_EQUAL, reader.lexeme(), reader.position);
		} else {
			return new StringToken(LT, reader.lexeme(), reader.position);
		}
	}

	/** -- literals --
	 * - Non-numeric literal: the alphabetic or alpha-numeric string
	 * enclosed between single (' ') or double (" ") quotation marks.
	 * It can contain any allowed character from the character set (A-Z, a-z, 0-9, and special characters).
	 * Length is 1~256 characters.
	 * non-numeric-figurative-constant
	 *
	 * - numeric literal is a numeric constant
	 * that is a combination of digits (0-9), a sign character (+ or -), and a decimal point(.).
	 * Every numeric literal is of numeric data type.
	 * Length is 1~18 characters.
	 *
	 * Numeric Literal Types:
	 * - Fixed-point numbers.
	 * - Floating-point numbers. e.g. +9.999E-3
	 *      [+/-] mantissa E [+/-] exponent
	 */
	public static StringToken onSingleQuote(CodeReaderBase reader) {
		return scanStringLiteral(reader, '\'');
	}

	/** -- identifier --
	 * COBOL word is made up of:
	 * - alphabetic characters (A-Z and a-z),
	 * - numeric characters (0-9),
	 * - hyphens (-),
	 * - or underscores (_) [NetCOBOL extension].
	 * - A user-defined word may consist of national characters. [NetCOBOL extension].
	 * Length is 1~30 characters.
	 * A hyphen or underscore must not be used as the first or last character of a COBOL word.
	 * Each lowercase alphabet is equivalent to its uppercase.
	 *
	 * two types:
	 * - User-defined Words.
	 * - Reserved Words.
	 *      - Keywords.
	 *      - Optional Words.
	 *      - Figurative Constants.
	 *      - Special Character Words.
	 */
	public static StringToken scanIdentifier(CodeReaderBase reader) {
		char prev = reader.ch;
		if (!reader.optionalChar(CobolLexerWithCodeReader::isLetterOrDigit)) {
			return lexError(reader, "invalid identifier start character");
		}

		for (; isIdentifierPart(reader.ch); reader.nextChar()) {
			prev = reader.ch;
		}

		if ('-' == prev || '_' == prev) {
			return lexError(reader, "an identifier must not end with '-' or '_'");
		}

		String lexeme = reader.lexeme();
		String uppercase = lexeme.toUpperCase();
		StringToken token = new StringToken(StringTokenKind.reservedKeywords.getOrDefault(uppercase, IDENTIFIER),
				lexeme, reader.position);
		optionalIdDivisionParagraph(reader, token);
		return token;
	}

	private static boolean optionalIdDivisionParagraph(CodeReaderBase reader, StringToken token) {
		if (token.kind() == AUTHOR
				|| token.kind() == INSTALLATION
				|| token.kind() == DATE_WRITTEN
				|| token.kind() == DATE_COMPILED
				|| token.kind() == SECURITY) {
			reader.optionalChar('.');
			String commentEntry = optionalCommentEntry(reader);
			token.attribute(token.kind(), commentEntry);
			return true;
		}
		return false;
	}

	private static String optionalCommentEntry(CodeReaderBase reader) {
		while (true) {
			reader.skipToEndOfLine();
			reader.acceptLineTerminator();
			int countOfWhitespaces = reader.skipWhitespaces();
			if (countOfWhitespaces <= 7) {
				break;
			}
		}
		return reader.lexeme();
	}

	public static StringToken onDigit(CodeReaderBase reader) {
		reader.acceptDigits();
		if (reader.optionalChar('.')) {
			reader.acceptDigits();
			return new StringToken(NUMBER_LITERAL, reader.lexeme(), reader.position);
		} else if (isIdentifierPart(reader.ch)) {
			while (reader.optionalChar(CobolLexerWithCodeReader::isIdentifierPart)) {}
			return new StringToken(IDENTIFIER, reader.lexeme(), reader.position);
		} else {
			return new StringToken(NUMBER_LITERAL, reader.lexeme(), reader.position);
		}
	}

	public static boolean isLetterOrDigit(char ch) {
		return     '0' <= ch && ch <= '9'
				|| 'a' <= ch && ch <= 'z'
				|| 'A' <= ch && ch <= 'Z';
	}

	public static boolean isIdentifierPart(char ch) {
		return isLetterOrDigit(ch) || '-' == ch || '_' == ch;
	}

	public static boolean isAlphabetic(char ch) {
		return 'a' <= ch && ch <= 'z' || 'A' <= ch && ch <= 'Z' || ' ' == ch;
	}
}
