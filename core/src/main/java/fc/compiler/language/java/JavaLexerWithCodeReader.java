package fc.compiler.language.java;

import fc.compiler.common.lexer.CodeReaderBase;
import fc.compiler.common.lexer.LexerWithCodeReaderBase;
import fc.compiler.common.lexer.LexerMapper;
import fc.compiler.common.token.StringToken;

import static fc.compiler.common.lexer.Constants.EOF;
import static fc.compiler.common.lexer.Constants.*;
import static fc.compiler.common.token.StringTokenKind.*;

/**
 * @author FC
 */
public class JavaLexerWithCodeReader extends LexerWithCodeReaderBase {
	public JavaLexerWithCodeReader() {
		this.mapper = initLexerMapper();
	}

	public LexerMapper initLexerMapper() {
		LexerMapper mapper = new LexerMapper();
		mapper.mapLexer(EOF,    LexerWithCodeReaderBase::scanEOF);

		mapper.mapLexer(SPACE,  LexerWithCodeReaderBase::scanWhiteSpaces);
		mapper.mapLexer(TAB,    LexerWithCodeReaderBase::scanWhiteSpaces);
		mapper.mapLexer(FF,     LexerWithCodeReaderBase::scanWhiteSpaces);
		mapper.mapLexer(LF,     LexerWithCodeReaderBase::scanLineTerminator);
		mapper.mapLexer(CR,     LexerWithCodeReaderBase::scanLineTerminator);

		for (char c = 'a'; c <= 'z'; c++) {
			mapper.mapLexer(c,     JavaLexerWithCodeReader::scanIdentifier);
		}
		for (char c = 'A'; c <= 'Z'; c++) {
			mapper.mapLexer(c,     JavaLexerWithCodeReader::scanIdentifier);
		}
		mapper.mapLexer('$',     JavaLexerWithCodeReader::scanIdentifier);
		mapper.mapLexer('_',     JavaLexerWithCodeReader::scanIdentifier);

		mapper.mapLexer('0', JavaLexerWithCodeReader::scanNumber);
		for (char c = '1'; c < '9'; c++) {
			mapper.mapLexer(c,     JavaLexerWithCodeReader::scanNumber);
		}

		mapper.mapLexer('.', JavaLexerWithCodeReader::scanDot);
		mapper.mapLexer(',', LexerWithCodeReaderBase::scanComma);
		mapper.mapLexer(';', LexerWithCodeReaderBase::scanSemicolon);
		mapper.mapLexer(':', LexerWithCodeReaderBase::scanColon);
		mapper.mapLexer('(', LexerWithCodeReaderBase::scanLeftParen);
		mapper.mapLexer(')', LexerWithCodeReaderBase::scanRightParen);
		mapper.mapLexer('[', LexerWithCodeReaderBase::scanLeftBracket);
		mapper.mapLexer(']', LexerWithCodeReaderBase::scanRightBracket);
		mapper.mapLexer('{', LexerWithCodeReaderBase::scanLeftBrace);
		mapper.mapLexer('}', LexerWithCodeReaderBase::scanRightBrace);
		mapper.mapLexer('?', LexerWithCodeReaderBase::scanQuestion);
		mapper.mapLexer('@', LexerWithCodeReaderBase::scanAt);

		mapper.mapLexer('+', JavaLexerWithCodeReader::scanPlus);
		mapper.mapLexer('-', JavaLexerWithCodeReader::scanMinus);
		mapper.mapLexer('*', JavaLexerWithCodeReader::scanStar);
		mapper.mapLexer('/', JavaLexerWithCodeReader::scanSlash);
		mapper.mapLexer('&', JavaLexerWithCodeReader::scanAmpersand);
		mapper.mapLexer('|', JavaLexerWithCodeReader::scanBar);
		mapper.mapLexer('=', JavaLexerWithCodeReader::scanEqual);
		mapper.mapLexer('>', JavaLexerWithCodeReader::scanGT);
		mapper.mapLexer('<', JavaLexerWithCodeReader::scanLT);
		mapper.mapLexer('!', JavaLexerWithCodeReader::scanExclamationMark);
		mapper.mapLexer('%', JavaLexerWithCodeReader::scanPercent);
		mapper.mapLexer('~', JavaLexerWithCodeReader::scanTilde);
		mapper.mapLexer('^', JavaLexerWithCodeReader::scanCaret);

		mapper.mapLexer('\'', LexerWithCodeReaderBase::scanCharLiteral);
		mapper.mapLexer('\"', LexerWithCodeReaderBase::scanStringLiteral);

		mapper.setDefaultLexer(this::scanDefault);
		return mapper;
	}

	protected StringToken scanDefault(CodeReaderBase reader) {
		reader.nextChar();
		return null;
	}

	public static StringToken scanIdentifier(CodeReaderBase reader) {
		if (!Character.isJavaIdentifierStart(reader.ch))
			return null;

		reader.nextChar();

		while (Character.isJavaIdentifierPart(reader.ch)) {
			reader.nextChar();
		}

		return new StringToken(IDENTIFIER, reader.lexeme(), reader.position);
	}

	public static StringToken scanNumber(CodeReaderBase reader) {
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

	public static StringToken scanDecimalNumberLiteral(CodeReaderBase reader) {
		// scan integral part
		for (; '0' <= reader.ch && reader.ch <= '9'; reader.nextChar()) {}

		// scan fraction and suffix parts
		if (reader.optionalChar('.')) {
			for (; '0' <= reader.ch && reader.ch <= '9'; reader.nextChar()) {}
		}

		return new StringToken(NUMBER_LITERAL, reader.lexeme(), reader.position);
	}

	public static StringToken scanHexNumberLiteral(CodeReaderBase reader) {
		// scan integral part
		for (; reader.isHexDigit(); reader.nextChar()) {}

		// scan fraction and suffix parts
		if (reader.optionalChar('.')) {
			for (; reader.isHexDigit(); reader.nextChar()) {}
		}

		StringToken token = new StringToken(NUMBER_LITERAL, reader.lexeme(), reader.position);
		token.radix(16);
		return token;
	}

	public static StringToken scanOctNumberLiteral(CodeReaderBase reader) {
		// scan integral part
		for (; reader.isOctDigit(); reader.nextChar()) {}

		// scan fraction and suffix parts
		if (reader.optionalChar('.')) {
			for (; reader.isOctDigit(); reader.nextChar()) {}
		}

		StringToken token = new StringToken(NUMBER_LITERAL, reader.lexeme(), reader.position);
		token.radix(8);
		return token;
	}

	public static StringToken scanBinaryNumberLiteral(CodeReaderBase reader) {
		// scan integral part
		for (; reader.ch == '0' || reader.ch == '1'; reader.nextChar()) {}

		// scan fraction and suffix parts
		if (reader.optionalChar('.')) {
			for (; reader.ch == '0' || reader.ch == '1'; reader.nextChar()) {}
		}

		StringToken token = new StringToken(NUMBER_LITERAL, reader.lexeme(), reader.position);
		token.radix(2);
		return token;
	}

	public static StringToken scanFractionAndSuffix(CodeReaderBase reader) {
		for (; '0' <= reader.ch && reader.ch <= '9'; reader.nextChar()) {}
		return new StringToken(NUMBER_LITERAL, reader.lexeme(), reader.position);
	}


	public static StringToken scanDot(CodeReaderBase reader) {
		if (reader.optionalSequentialChars("...")) {
			return new StringToken(ELLIPSIS, "...", reader.position);
		} else {
			reader.nextChar();
			if (reader.optionalChar('.')) {   // no double dots.
				lexError(reader, "double dots is invalid token.");
			} else if ('0' <= reader.ch && reader.ch <= '9') {
				return scanFractionAndSuffix(reader);
			} else {
				return new StringToken(DOT, ".", reader.position);
			}
		}
		return null;
	}

	public static StringToken scanPlus(CodeReaderBase reader) {
		return scanDoubleOrEqualCompoundOperator(reader, '+', PLUS_PLUS, PLUS_EQUAL, PLUS);
	}

	public static StringToken scanMinus(CodeReaderBase reader) {
		return scanDoubleOrEqualCompoundOperator(reader, '-', MINUS_MINUS, MINUS_EQUAL, MINUS);
	}

	public static StringToken scanAmpersand(CodeReaderBase reader) {
		return scanDoubleOrEqualCompoundOperator(reader, '&', AMPERSAND_AMPERSAND, AMPERSAND_EQUAL, AMPERSAND);
	}

	public static StringToken scanBar(CodeReaderBase reader) {
		return scanDoubleOrEqualCompoundOperator(reader, '|', BAR_BAR, BAR_EQUAL, BAR);
	}

	public static StringToken scanDoubleOrEqualCompoundOperator(CodeReaderBase reader, char operator,
	                                                            String doubleKind, String compoundKind, String simpleKind) {
		reader.optionalChar(operator);
		if (reader.optionalChar(operator)) {
			return new StringToken(doubleKind,    reader.lexeme(), reader.position);
		} else if (reader.optionalChar('=')) {
			return new StringToken(compoundKind,  reader.lexeme(), reader.position);
		} else {
			return new StringToken(simpleKind,    reader.lexeme(), reader.position);
		}
	}

	public static StringToken scanStar(CodeReaderBase reader) {
		return scanEqualCompoundOperator(reader, '*', STAR_EQUAL, STAR);
	}

	public static StringToken scanEqual(CodeReaderBase reader) {
		return scanEqualCompoundOperator(reader, '=', EQUAL_EQUAL, EQUAL);
	}

	public static StringToken scanPercent(CodeReaderBase reader) {
		return scanEqualCompoundOperator(reader, '%', PERCENT_EQUAL, PERCENT);
	}

	public static StringToken scanTilde(CodeReaderBase reader) {
		return scanEqualCompoundOperator(reader, '~', TILDE_EQUAL, TILDE);
	}

	public static StringToken scanCaret(CodeReaderBase reader) {
		return scanEqualCompoundOperator(reader, '^', CARET_EQUAL, CARET);
	}

	public static StringToken scanExclamationMark(CodeReaderBase reader) {
		return scanEqualCompoundOperator(reader, '!', EXCLAMATION_MARK_EQUAL, EXCLAMATION_MARK);
	}

	public static StringToken scanEqualCompoundOperator(CodeReaderBase reader, char operator,
	                                                    String compoundKind, String simpleKind) {
		reader.optionalChar(operator);
		return scanEqualCompoundOperator(reader, compoundKind, simpleKind);
	}

	public static StringToken scanEqualCompoundOperator(CodeReaderBase reader, String compoundKind, String simpleKind) {
		if (reader.optionalChar('=')) {
			return new StringToken(compoundKind, reader.lexeme(), reader.position);
		} else {
			return new StringToken(simpleKind, reader.lexeme(), reader.position);
		}
	}

	public static StringToken scanGT(CodeReaderBase reader) {
		return scanGTOrLT(reader, '>', GT_GT_EQUAL, GT_GT, GT_EQUAL, GT);
	}

	public static StringToken scanLT(CodeReaderBase reader) {
		return scanGTOrLT(reader, '<', LT_LT_EQUAL, LT_LT, LT_EQUAL, LT);
	}

	public static StringToken scanGTOrLT(CodeReaderBase reader, char operator,
	                                     String doubleCompoundKind, String doubleKind,
	                                     String compoundKind, String simpleKind) {
		reader.optionalChar(operator);
		if (reader.optionalChar(operator)) {
			if (reader.optionalChar('=')) {
				return new StringToken(doubleCompoundKind, reader.lexeme(), reader.position);
			} else {
				return new StringToken(doubleKind, reader.lexeme(), reader.position);
			}
		} else if (reader.optionalChar('=')) {
			return new StringToken(compoundKind,  reader.lexeme(), reader.position);
		} else {
			return new StringToken(simpleKind,    reader.lexeme(), reader.position);
		}
	}

	public static StringToken scanSlash(CodeReaderBase reader) {
		reader.optionalChar('/');
		if (reader.optionalChar('/')) {           // "//" for line comment
			return scanLineComment(reader);
		} else if (reader.optionalChar('*')) {
			if (reader.optionalChar('*')) {
				if (reader.optionalChar('/'))     // "/**/" for empty block comment
					return new StringToken(BLOCK_COMMENT, reader.lexeme(), reader.position);
				else                            // "/**" for java doc
					return scanJavaDoc(reader);
			} else {                            // "/*" for block comment
				return scanBlockComment(reader);
			}
		} else {
			return scanEqualCompoundOperator(reader, SLASH_EQUAL, SLASH);
		}
	}

	public static StringToken scanBlockComment(CodeReaderBase reader) {
		while (reader.hasNext()) {
			if (reader.optionalChar('*')) {
				if (reader.optionalChar('/'))
					break;
				else
					reader.nextChar();
			} else {
				reader.nextChar();
			}
		}
		return new StringToken(BLOCK_COMMENT, reader.lexeme(), reader.position);
	}

	public static StringToken scanJavaDoc(CodeReaderBase reader) {
		throw new RuntimeException("not implemented");
	}

}
