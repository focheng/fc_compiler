package fc.compiler.language.java;

import fc.compiler.common.lexer.CodeReader;
import fc.compiler.common.lexer.LexerBase;
import fc.compiler.common.lexer.LexerMapper;
import fc.compiler.common.token.Token;

import static fc.compiler.common.lexer.Constants.EOF;
import static fc.compiler.common.lexer.Constants.*;
import static fc.compiler.common.token.TokenKind.*;

/**
 * @author FC
 */
public class JavaLexer extends LexerBase {
	public JavaLexer() {
		this.mapper = initLexerMapper();
	}

	public LexerMapper initLexerMapper() {
		LexerMapper mapper = new LexerMapper();
		mapper.mapLexer(EOF,    JavaLexer::scanEOF);

		mapper.mapLexer(SPACE,  JavaLexer::scanWhiteSpaces);
		mapper.mapLexer(TAB,    JavaLexer::scanWhiteSpaces);
		mapper.mapLexer(FF,     JavaLexer::scanWhiteSpaces);
		mapper.mapLexer(LF,     JavaLexer::scanLineTerminator);
		mapper.mapLexer(CR,     JavaLexer::scanLineTerminator);

		for (char c = 'a'; c < 'z'; c++) {
			mapper.mapLexer(c,     JavaLexer::scanIdentifier);
		}
		for (char c = 'A'; c < 'Z'; c++) {
			mapper.mapLexer(c,     JavaLexer::scanIdentifier);
		}
		mapper.mapLexer('$',     JavaLexer::scanIdentifier);
		mapper.mapLexer('_',     JavaLexer::scanIdentifier);

		mapper.mapLexer('0', JavaLexer::scanNumber);
		for (char c = '1'; c < '9'; c++) {
			mapper.mapLexer(c,     JavaLexer::scanNumber);
		}

		mapper.mapLexer('.', JavaLexer::scanDot);
		mapper.mapLexer(',', LexerBase::scanComma);
		mapper.mapLexer(';', LexerBase::scanSemicolon);
		mapper.mapLexer(':', LexerBase::scanColon);
		mapper.mapLexer('(', LexerBase::scanLeftParen);
		mapper.mapLexer(')', LexerBase::scanRightParen);
		mapper.mapLexer('[', LexerBase::scanLeftBracket);
		mapper.mapLexer(']', LexerBase::scanRightBracket);
		mapper.mapLexer('{', LexerBase::scanLeftBrace);
		mapper.mapLexer('}', LexerBase::scanRightBrace);
		mapper.mapLexer('?', LexerBase::scanQuestion);
		mapper.mapLexer('@', LexerBase::scanAt);

		mapper.mapLexer('+', JavaLexer::scanPlus);
		mapper.mapLexer('-', JavaLexer::scanMinus);
		mapper.mapLexer('*', JavaLexer::scanStar);
		mapper.mapLexer('/', JavaLexer::scanSlash);
		mapper.mapLexer('&', JavaLexer::scanAmpersand);
		mapper.mapLexer('|', JavaLexer::scanBar);
		mapper.mapLexer('=', JavaLexer::scanEqual);
		mapper.mapLexer('>', JavaLexer::scanGT);
		mapper.mapLexer('<', JavaLexer::scanLT);
		mapper.mapLexer('!', JavaLexer::scanExclamationMark);
		mapper.mapLexer('%', JavaLexer::scanPercent);
		mapper.mapLexer('~', JavaLexer::scanTilde);
		mapper.mapLexer('^', JavaLexer::scanCaret);

		mapper.mapLexer('\'', LexerBase::scanSingleQuote);
		mapper.mapLexer('\"', LexerBase::scanDoubleQuote);

		mapper.setDefaultLexer(this::scanDefault);
		return mapper;
	}

	protected Token scanDefault(CodeReader reader) {
		reader.nextChar();
		return null;
	}

	public static Token scanWhiteSpaces(CodeReader reader) {
		reader.skipWhitespace();
		return new Token(WHITE_SPACES, reader.position).setLexeme(reader.getLexeme());    // by default, white spaces are ignored.
	}

	public static Token scanLineTerminator(CodeReader reader) {
		if (reader.acceptLineTerminator()) {
			return new Token(LINE_TERMINATOR, reader.position).setLexeme(reader.getLexeme());
		}
		return null;
	}

	protected static Token scanIdentifier(CodeReader reader) {
		if (!Character.isJavaIdentifierStart(reader.ch))
			return null;

		reader.nextChar();

		while (Character.isJavaIdentifierPart(reader.ch)) {
			reader.nextChar();
		}

		return new Token(IDENTIFIER, reader.position)
				.setLexeme(reader.getLexeme());
	}

	protected static Token scanNumber(CodeReader reader) {
		if (reader.ch == '0') { // '0x1A', '0b01', '017'
			reader.nextChar();
			if (reader.ch == 'x' || reader.ch == 'X') {
				reader.nextChar();
				return scanHexNumberLiteral(reader);
			} else if (reader.ch == 'b') {
				reader.nextChar();
				return scanBinaryNumberLiteral(reader);
			} else {
				return scanOctNumberLiteral(reader);
			}
		} else {
			return scanDecimalNumberLiteral(reader);
		}
	}

	private static Token scanDecimalNumberLiteral(CodeReader reader) {
		// scan integral part
		for (; '0' <= reader.ch && reader.ch <= '9'; reader.nextChar()) {}

		// scan fraction and suffix parts
		if (reader.accept('.')) {
			for (; '0' <= reader.ch && reader.ch <= '9'; reader.nextChar()) {}
		}

		return new Token(NUMBER_LITERAL, reader.position).setLexeme(reader.getLexeme());
	}

	private static Token scanHexNumberLiteral(CodeReader reader) {
		// scan integral part
		for (; reader.isHexDigit(); reader.nextChar()) {}

		// scan fraction and suffix parts
		if (reader.accept('.')) {
			for (; reader.isHexDigit(); reader.nextChar()) {}
		}

		return new Token(NUMBER_LITERAL, reader.position).setLexeme(reader.getLexeme()).setRadix(16);
	}

	private static Token scanOctNumberLiteral(CodeReader reader) {
		// scan integral part
		for (; reader.isOctDigit(); reader.nextChar()) {}

		// scan fraction and suffix parts
		if (reader.accept('.')) {
			for (; reader.isOctDigit(); reader.nextChar()) {}
		}

		return new Token(NUMBER_LITERAL, reader.position).setLexeme(reader.getLexeme()).setRadix(8);
	}

	private static Token scanBinaryNumberLiteral(CodeReader reader) {
		// scan integral part
		for (; reader.ch == '0' || reader.ch == '1'; reader.nextChar()) {}

		// scan fraction and suffix parts
		if (reader.accept('.')) {
			for (; reader.ch == '0' || reader.ch == '1'; reader.nextChar()) {}
		}

		return new Token(NUMBER_LITERAL, reader.position).setLexeme(reader.getLexeme()).setRadix(2);
	}

	private static Token scanFractionAndSuffix(CodeReader reader) {
		for (; '0' <= reader.ch && reader.ch <= '9'; reader.nextChar()) {}
		return new Token(NUMBER_LITERAL, reader.position).setLexeme(reader.getLexeme());
	}


	protected static Token scanDot(CodeReader reader) {
		if (reader.accept("...")) {
			return new Token(ELLIPSIS, reader.position).setLexeme("...");
		} else {
			reader.nextChar();
			if (reader.accept('.')) {   // no double dots.
				lexError(reader, "double dots is invalid token.");
			} else if ('0' <= reader.ch && reader.ch <= '9') {
				return scanFractionAndSuffix(reader);
			} else {
				return new Token(DOT, reader.position).setLexeme(".");
			}
		}
		return null;
	}

	private static Token scanPlus(CodeReader reader) {
		return scanDoubleOrEqualCompoundOperator(reader, '+', PLUS_PLUS, PLUS_EQUAL, PLUS);
	}

	private static Token scanMinus(CodeReader reader) {
		return scanDoubleOrEqualCompoundOperator(reader, '-', MINUS_MINUS, MINUS_EQUAL, MINUS);
	}

	private static Token scanAmpersand(CodeReader reader) {
		return scanDoubleOrEqualCompoundOperator(reader, '&', AMPERSAND_AMPERSAND, AMPERSAND_EQUAL, AMPERSAND);
	}

	private static Token scanBar(CodeReader reader) {
		return scanDoubleOrEqualCompoundOperator(reader, '|', BAR_BAR, BAR_EQUAL, BAR);
	}

	private static Token scanDoubleOrEqualCompoundOperator(CodeReader reader, char operator,
	                                                       String doubleKind, String compoundKind, String simpleKind) {
		reader.accept(operator);
		if (reader.accept(operator)) {
			return new Token(doubleKind,    reader.position).setLexeme(reader.getLexeme());
		} else if (reader.accept('=')) {
			return new Token(compoundKind,  reader.position).setLexeme(reader.getLexeme());
		} else {
			return new Token(simpleKind,    reader.position).setLexeme(reader.getLexeme());
		}
	}

	private static Token scanStar(CodeReader reader) {
		return scanEqualCompoundOperator(reader, '*', STAR_EQUAL, STAR);
	}

	private static Token scanEqual(CodeReader reader) {
		return scanEqualCompoundOperator(reader, '=', EQUAL_EQUAL, EQUAL);
	}

	private static Token scanPercent(CodeReader reader) {
		return scanEqualCompoundOperator(reader, '%', PERCENT_EQUAL, PERCENT);
	}

	private static Token scanTilde(CodeReader reader) {
		return scanEqualCompoundOperator(reader, '~', TILDE_EQUAL, TILDE);
	}

	private static Token scanCaret(CodeReader reader) {
		return scanEqualCompoundOperator(reader, '^', CARET_EQUAL, CARET);
	}

	private static Token scanExclamationMark(CodeReader reader) {
		return scanEqualCompoundOperator(reader, '!', EXCLAMATION_MARK_EQUAL, EXCLAMATION_MARK);
	}

	public static Token scanEqualCompoundOperator(CodeReader reader, char operator,
	                                              String compoundKind, String simpleKind) {
		reader.accept(operator);
		return scanEqualCompoundOperator(reader, compoundKind, simpleKind);
	}

	private static Token scanEqualCompoundOperator(CodeReader reader, String compoundKind, String simpleKind) {
		if (reader.accept('=')) {
			return new Token(compoundKind, reader.position).setLexeme(reader.getLexeme());
		} else {
			return new Token(simpleKind, reader.position).setLexeme(reader.getLexeme());
		}
	}

	private static Token scanGT(CodeReader reader) {
		return scanGTOrLT(reader, '>', GT_GT_EQUAL, GT_GT, GT_EQUAL, GT);
	}

	private static Token scanLT(CodeReader reader) {
		return scanGTOrLT(reader, '<', LT_LT_EQUAL, LT_LT, LT_EQUAL, LT);
	}

	private static Token scanGTOrLT(CodeReader reader, char operator,
	                                String doubleCompoundKind, String doubleKind,
	                                String compoundKind, String simpleKind) {
		reader.accept(operator);
		if (reader.accept(operator)) {
			if (reader.accept('=')) {
				return new Token(doubleCompoundKind, reader.position).setLexeme(reader.getLexeme());
			} else {
				return new Token(doubleKind, reader.position).setLexeme(reader.getLexeme());
			}
		} else if (reader.accept('=')) {
			return new Token(compoundKind,  reader.position).setLexeme(reader.getLexeme());
		} else {
			return new Token(simpleKind,    reader.position).setLexeme(reader.getLexeme());
		}
	}

	private static Token scanSlash(CodeReader reader) {
		reader.accept('/');
		if (reader.accept('/')) {
			return scanLineComment(reader);
		} else if (reader.accept('*')) {
			if (reader.accept('*')) {
				return scanJavaDoc(reader);
			} else {
				return scanBlockComment(reader);
			}
		} else {
			return scanEqualCompoundOperator(reader, SLASH_EQUAL, SLASH);
		}
	}

	private static Token scanLineComment(CodeReader reader) {
		reader.skipToEndOfLine();
		return new Token(LINE_COMMENT, reader.position).setLexeme(reader.getLexeme());
	}

	private static Token scanBlockComment(CodeReader reader) {
		throw new RuntimeException("not implemented");
	}

	private static Token scanJavaDoc(CodeReader reader) {
		throw new RuntimeException("not implemented");
	}

}
