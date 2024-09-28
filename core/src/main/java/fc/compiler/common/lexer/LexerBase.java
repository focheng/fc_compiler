package fc.compiler.common.lexer;

import fc.compiler.common.token.Token;
import fc.compiler.common.token.TokenKind;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import static fc.compiler.common.lexer.Constants.CR;
import static fc.compiler.common.lexer.Constants.LF;
import static fc.compiler.common.token.TokenKind.*;


/**
 * Base class for Lexer (Lexical Analyzer).
 * @author FC
 */
@Slf4j @Getter @Setter @Accessors(fluent = true, chain = true)
public class LexerBase implements Lexer {
	protected LexerMapper mapper;

	@Override
	public Token scan(CodeReaderBase reader) {
		reader.onStartToken();
		Lexer lexer = mapper.getLexer(reader.ch);
		if (lexer != null) {
			Token token = lexer.scan(reader);
			return token;
		} else {
			return lexError(reader, "Unsupported lexeme '" + reader.ch + "' @ " + reader.position);
		}
	}

	protected static Token lexError(CodeReaderBase reader, String s) {
		reader.nextChar();
		log.error(s);
		return new Token(ERROR, reader.position).lexeme(reader.lexeme());
	}

	protected static Token scanDummy(CodeReaderBase reader) {
		reader.nextChar();
		return new Token(TokenKind.ERROR, reader.position).lexeme(reader.lexeme());
	}

	public static Token scanEOF(CodeReaderBase reader) {
		reader.nextChar();
		return new Token(EOF, reader.position);
	}

	public static Token scanWhiteSpaces(CodeReaderBase reader) {
		reader.skipWhitespaces();
		return new Token(WHITE_SPACES, reader.position).lexeme(reader.lexeme());    // by default, white spaces are ignored.
	}

	public static Token scanLineTerminator(CodeReaderBase reader) {
		if (reader.acceptLineTerminator()) {
			return new Token(LINE_TERMINATOR, reader.position).lexeme(reader.lexeme());
		}
		return null;
	}

	public static Token scanComma(CodeReaderBase reader)        { return scanSingleCharToken(reader, COMMA); }
	public static Token scanSemicolon(CodeReaderBase reader)    { return scanSingleCharToken(reader, SEMICOLON);}
	public static Token scanColon(CodeReaderBase reader)        { return scanSingleCharToken(reader, COLON);}
	public static Token scanLeftParen(CodeReaderBase reader)    { return scanSingleCharToken(reader, LEFT_PAREN);}
	public static Token scanRightParen(CodeReaderBase reader)   { return scanSingleCharToken(reader, RIGHT_PAREN);}
	public static Token scanLeftBracket(CodeReaderBase reader)  { return scanSingleCharToken(reader, LEFT_BRACKET);}
	public static Token scanRightBracket(CodeReaderBase reader) { return scanSingleCharToken(reader, RIGHT_BRACKET);}
	public static Token scanLeftBrace(CodeReaderBase reader)    { return scanSingleCharToken(reader, LEFT_BRACE);}
	public static Token scanRightBrace(CodeReaderBase reader)   { return scanSingleCharToken(reader, RIGHT_BRACE);}
	public static Token scanQuestion(CodeReaderBase reader)     { return scanSingleCharToken(reader, QUESTION);}
	public static Token scanAt(CodeReaderBase reader)           { return scanSingleCharToken(reader, AT);}
	public static Token scanPlus(CodeReaderBase reader)         { return scanSingleCharToken(reader, PLUS);}
	public static Token scanStar(CodeReaderBase reader)         { return scanSingleCharToken(reader, STAR);}
	public static Token scanBar(CodeReaderBase reader)          { return scanSingleCharToken(reader, BAR);}

	protected static Token scanSingleCharToken(CodeReaderBase reader, String kind) {
		reader.nextChar();
		return new Token(kind, reader.position).lexeme(reader.lexeme());
	}

//	public static Token scanSingleQuote(CodeReader reader) {
//		return scanCharLiteral(reader, '\'');
//	}
//
//	public static Token scanDoubleQuote(CodeReader reader) {
//		return scanStringLiteral(reader);
//	}
//
	public static Token scanStringLiteral(CodeReaderBase reader) {
		return scanStringLiteral(reader, '\"');
	}
	public static Token scanStringLiteral(CodeReaderBase reader, char quote) {
		reader.accept(quote);
		while (reader.ch != quote
				&& reader.ch != CR
				&& reader.ch != LF
				&& reader.ch != Constants.EOF) {
			scanEscapedChar(reader);
			reader.nextChar();
		}
		if (reader.accept(quote)) {
			String lexeme = reader.lexeme();
			String value = lexeme.substring(1, lexeme.length()-1);;
			return new Token(STRING_LITERAL, reader.position).lexeme(lexeme)
					.attribute("value", value);
		} else {
			lexError(reader, "invalid string literal: " + reader.lexeme());
			return null;
		}
	}

	public static Token scanCharLiteral(CodeReaderBase reader) { return scanCharLiteral(reader, '\''); }
	public static Token scanCharLiteral(CodeReaderBase reader, char quote) {
		reader.accept(quote);
		if (reader.accept(quote)) {
			return lexError(reader, "Empty character literal.");
		} else {
			scanEscapedChar(reader);
			if (reader.accept(quote)) {
				return new Token(CHAR_LITERAL, reader.position).lexeme(reader.lexeme());
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

	public static Token scanLineComment(CodeReaderBase reader) {
		reader.skipToEndOfLine();
		return new Token(LINE_COMMENT, reader.position).lexeme(reader.lexeme());
	}

	public static Token scanIdentifier(CodeReaderBase reader) {
		if (!reader.isLetter())
			return lexError(reader, "Identifier must be start with letter.");

		reader.nextChar();

		while (reader.isLetterOrDigit()) {
			reader.nextChar();
		}

		return new Token(IDENTIFIER, reader.position)
				.lexeme(reader.lexeme());
	}
}
