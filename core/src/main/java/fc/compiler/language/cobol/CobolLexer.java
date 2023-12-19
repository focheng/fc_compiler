package fc.compiler.language.cobol;

import fc.compiler.common.lexer.*;
import fc.compiler.common.token.Token;
import fc.compiler.common.token.TokenKind;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;

import static fc.compiler.common.lexer.Constants.*;
import static fc.compiler.common.lexer.Constants.CR;
import static fc.compiler.common.lexer.Constants.SPACE;
import static fc.compiler.common.token.TokenKind.*;
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
public class CobolLexer extends LexerBase {
	protected CobolCompilerOptions options;
	@Getter protected boolean previousTokenLineTerminator = true;

	public CobolLexer() {
		this.mapper = initLexerMapper();
		initReservedKeywords();
	}

	public Token scan(CodeReader reader) {
		reader.sp = reader.bp;
		reader.newPosition();
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
						return new Token("IGNORED DEBUG CODE", reader.position).lexeme(reader.lexeme());
					} else { // ignore this character.
						reader.nextChar();
						break;
					}
				default:
					if (reader.ch != SPACE && !Character.isWhitespace(reader.ch)) {
						Token token = lexError(reader, "Unsupported line indicator");
						reader.nextChar();
						return token;
					}
			}
		}
		return super.scan(reader);
	}

	public LexerMapper initLexerMapper() {
		LexerMapper mapper = new LexerMapper();
		mapper.mapLexer(Constants.EOF,    LexerBase::scanEOF);

		mapper.mapLexer(SPACE,  LexerBase::scanWhiteSpaces);
		mapper.mapLexer(TAB,    LexerBase::scanWhiteSpaces);
		mapper.mapLexer(FF,     LexerBase::scanWhiteSpaces);
		mapper.mapLexer(LF,     LexerBase::scanLineTerminator);
		mapper.mapLexer(CR,     LexerBase::scanLineTerminator);

		for (char c = 'a'; c <= 'z'; c++) mapper.mapLexer(c, CobolLexer::scanIdentifier);
		for (char c = 'A'; c <= 'Z'; c++) mapper.mapLexer(c, CobolLexer::scanIdentifier);
		for (char c = '0'; c <= '9'; c++) mapper.mapLexer(c, CobolLexer::onDigit);

		// separators
		mapper.mapLexer(',', CobolLexer::onComma);	    // Comma
		mapper.mapLexer(';', CobolLexer::onSemicolon);	// Semicolon
		mapper.mapLexer('.', CobolLexer::onPeriod);	    // Period or decimal point
		mapper.mapLexer('(', LexerBase::scanLeftParen);	// Left parenthesis
		mapper.mapLexer(')', LexerBase::scanRightParen);// Right parenthesis
		mapper.mapLexer(':', LexerBase::scanColon);	    // Colon
		mapper.mapLexer('&', reader -> scanSingleCharToken(reader, AMPERSAND));	// Ampersand
		mapper.mapLexer('=', CobolLexer::scanDoubleEqual);	// Equal sign
		mapper.mapLexer('*', LexerBase::scanLineComment);	// Asterisk

//		mapper.mapLexer('+', );	// Plus sign
//		mapper.mapLexer('-', );	// Minus sign or hyphen
//		mapper.mapLexer('/', );	// Slash
//		mapper.mapLexer('$', );	// Dollar sign
//		mapper.mapLexer('>', );	// Greater-than sign
//		mapper.mapLexer('<', );	// Less-than sign
//		mapper.mapLexer('_', );	// Underscore

		mapper.mapLexer('\'', CobolLexer::scanSingleQuote);
		mapper.mapLexer('\"', LexerBase::scanDoubleQuote);

		mapper.setDefaultLexer(LexerBase::scanDummy);
		return mapper;
	}

	public void initReservedKeywords() {
		try {
			for (Field field : CobolTokenKind.class.getFields()) {
				String keyword = (String)field.get(null);
				reservedKeywords.put(keyword, keyword);
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

	public static Token onComma(CodeReader reader) {
		return scanSeparator(reader, ',', SEPARATOR_COMMA);
	}

	public static Token onSemicolon(CodeReader reader) {
		return scanSeparator(reader, ',', SEPARATOR_SEMICOLON);
	}

	public static Token onPeriod(CodeReader reader) {
		return scanSeparator(reader, '.', SEPARATOR_PERIOD);
	}

	public static Token scanSeparator(CodeReader reader, char leadingChar, String tokenKind) {
		reader.accept(leadingChar);
		if (Character.isWhitespace(reader.ch)) {
			reader.nextChar();
			for (; Character.isWhitespace(reader.ch); reader.nextChar()) {}
			return new Token(tokenKind, reader.position).lexeme(reader.lexeme());
		} else {
			return lexError(reader, "Separator " + leadingChar + " is not followed by space");
		}
	}

	/** The == pseudo-text delimiter */
	public static Token scanDoubleEqual(CodeReader reader) {
		reader.accept('=');
		reader.accept('=');
		return new Token("PSEUDO_TEXT", reader.position).lexeme(reader.lexeme());
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
	public static Token scanSingleQuote(CodeReader reader) {
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
	public static Token scanIdentifier(CodeReader reader) {
		char prev = reader.ch;
		if (!reader.accept(CobolLexer::isLetterOrDigit)) {
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
		return new Token(TokenKind.reservedKeywords.getOrDefault(uppercase, IDENTIFIER),
				reader.position).lexeme(lexeme);
	}

	public static Token onDigit(CodeReader reader) {
		reader.acceptDigits();
		if (reader.accept('.')) {
			reader.acceptDigits();
			return new Token(NUMBER_LITERAL, reader.position).lexeme(reader.lexeme());
		} else if (isIdentifierPart(reader.ch)) {
			while (reader.accept(CobolLexer::isIdentifierPart)) {}
			return new Token(IDENTIFIER, reader.position).lexeme(reader.lexeme());
		} else {
			return new Token(NUMBER_LITERAL, reader.position).lexeme(reader.lexeme());
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
