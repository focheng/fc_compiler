package fc.compiler.common.lexer;

import fc.compiler.common.token.StringToken;
import fc.compiler.common.token.StringTokenKind;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import static fc.compiler.common.lexer.Constants.CR;
import static fc.compiler.common.lexer.Constants.LF;
import static fc.compiler.common.token.StringTokenKind.*;


/**
 * Base class for Lexer (Lexical Analyzer).
 * @author FC
 */
@Deprecated
@Slf4j @Getter @Setter @Accessors(fluent = true, chain = true)
public class LexerWithCodeReaderBase implements LexerWithCodeReader {
	protected LexerMapper mapper;

	@Override
	public StringToken scanToken(CodeReaderBase reader) {
		reader.onStartToken();
		LexerWithCodeReader lexer = mapper.getLexer(reader.ch);
		if (lexer != null) {
			StringToken token = lexer.scanToken(reader);
			return token;
		} else {
			return lexError(reader, "Unsupported lexeme '" + reader.ch + "' @ " + reader.position);
		}
	}

	protected static StringToken lexError(CodeReaderBase reader, String s) {
		reader.nextChar();
		log.error(s);
		return new StringToken(ERROR, reader.lexeme(), reader.position);
	}

	protected static StringToken scanDummy(CodeReaderBase reader) {
		reader.nextChar();
		return new StringToken(StringTokenKind.ERROR, reader.lexeme(), reader.position);
	}

	public static StringToken scanEOF(CodeReaderBase reader) {
		reader.nextChar();
		return new StringToken(EOF, reader.position);
	}

	public static StringToken scanWhiteSpaces(CodeReaderBase reader) {
		reader.skipWhitespaces();
		return new StringToken(WHITE_SPACES, reader.lexeme(), reader.position);    // by default, white spaces are ignored.
	}

	public static StringToken scanLineTerminator(CodeReaderBase reader) {
		if (reader.acceptLineTerminator()) {
			return new StringToken(LINE_TERMINATOR, reader.lexeme(), reader.position);
		}
		return null;
	}

	public static StringToken scanComma(CodeReaderBase reader)        { return scanSingleCharToken(reader, COMMA); }
	public static StringToken scanSemicolon(CodeReaderBase reader)    { return scanSingleCharToken(reader, SEMICOLON);}
	public static StringToken scanColon(CodeReaderBase reader)        { return scanSingleCharToken(reader, COLON);}
	public static StringToken scanLeftParen(CodeReaderBase reader)    { return scanSingleCharToken(reader, LEFT_PAREN);}
	public static StringToken scanRightParen(CodeReaderBase reader)   { return scanSingleCharToken(reader, RIGHT_PAREN);}
	public static StringToken scanLeftBracket(CodeReaderBase reader)  { return scanSingleCharToken(reader, LEFT_BRACKET);}
	public static StringToken scanRightBracket(CodeReaderBase reader) { return scanSingleCharToken(reader, RIGHT_BRACKET);}
	public static StringToken scanLeftBrace(CodeReaderBase reader)    { return scanSingleCharToken(reader, LEFT_BRACE);}
	public static StringToken scanRightBrace(CodeReaderBase reader)   { return scanSingleCharToken(reader, RIGHT_BRACE);}
	public static StringToken scanQuestion(CodeReaderBase reader)     { return scanSingleCharToken(reader, QUESTION);}
	public static StringToken scanAt(CodeReaderBase reader)           { return scanSingleCharToken(reader, AT);}
	public static StringToken scanPlus(CodeReaderBase reader)         { return scanSingleCharToken(reader, PLUS);}
	public static StringToken scanStar(CodeReaderBase reader)         { return scanSingleCharToken(reader, STAR);}
	public static StringToken scanBar(CodeReaderBase reader)          { return scanSingleCharToken(reader, BAR);}

	protected static StringToken scanSingleCharToken(CodeReaderBase reader, String kind) {
		reader.nextChar();
		return new StringToken(kind, reader.lexeme(), reader.position);
	}

//	public static Token scanSingleQuote(CodeReader reader) {
//		return scanCharLiteral(reader, '\'');
//	}
//
//	public static Token scanDoubleQuote(CodeReader reader) {
//		return scanStringLiteral(reader);
//	}
//
	public static StringToken scanStringLiteral(CodeReaderBase reader) {
		return scanStringLiteral(reader, '\"');
	}
	public static StringToken scanStringLiteral(CodeReaderBase reader, char quote) {
		reader.optionalChar(quote);
		while (reader.ch != quote
				&& reader.ch != CR
				&& reader.ch != LF
				&& reader.ch != Constants.EOF) {
			scanEscapedChar(reader);
			reader.nextChar();
		}
		if (reader.optionalChar(quote)) {
			String lexeme = reader.lexeme();
			String value = lexeme.substring(1, lexeme.length()-1);;
			StringToken token = new StringToken(STRING_LITERAL, lexeme, reader.position);
			token.attribute("value", value);
			return token;
		} else {
			lexError(reader, "invalid string literal: " + reader.lexeme());
			return null;
		}
	}

	public static StringToken scanCharLiteral(CodeReaderBase reader) { return scanCharLiteral(reader, '\''); }
	public static StringToken scanCharLiteral(CodeReaderBase reader, char quote) {
		reader.optionalChar(quote);
		if (reader.optionalChar(quote)) {
			return lexError(reader, "Empty character literal.");
		} else {
			scanEscapedChar(reader);
			if (reader.optionalChar(quote)) {
				return new StringToken(CHAR_LITERAL, reader.lexeme(), reader.position);
			} else {
				return lexError(reader, "Unclosed character literal.");
			}
		}
	}


	/** Common escaped characters are: \r, \n, \t, \\, \", \' */
	private static char scanEscapedChar(CodeReaderBase reader) {
		if (reader.ch == '\\') {

		}
		return 0;
	}

	public static StringToken scanLineComment(CodeReaderBase reader) {
		reader.skipToEndOfLine();
		return new StringToken(LINE_COMMENT, reader.lexeme(), reader.position);
	}

	public static StringToken scanIdentifier(CodeReaderBase reader) {
		if (!reader.isLetter())
			return lexError(reader, "Identifier must be start with letter.");

		reader.nextChar();

		while (reader.isLetterOrDigit()) {
			reader.nextChar();
		}

		return new StringToken(IDENTIFIER, reader.lexeme(), reader.position);
	}
}
