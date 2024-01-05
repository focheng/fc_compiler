package fc.compiler.common.lexer;

import fc.compiler.common.token.Token;
import fc.compiler.common.token.TokenKind;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

import static fc.compiler.common.lexer.Constants.CR;
import static fc.compiler.common.lexer.Constants.LF;
import static fc.compiler.common.token.TokenKind.*;


/**
 * Base class for Lexer (Lexical Analyzer).
 * @author FC
 */
@Slf4j @Getter @Setter @Accessors(fluent = true)
public class LexerBase implements Lexer {
	protected LexerMapper mapper;

	@Override
	public Token scan(CodeReader reader) {
		reader.onStartToken();
		Lexer lexer = mapper.getLexer(reader.ch);
		if (lexer != null) {
			Token token = lexer.scan(reader);
			return token;
		} else {
			return lexError(reader, "Unsupported lexeme '" + reader.ch + "' @ " + reader.position);
		}
	}

	protected static Token lexError(CodeReader reader, String s) {
		reader.nextChar();
		log.error(s);
		return new Token(ERROR, reader.position).lexeme(reader.lexeme());
	}

	protected static Token scanDummy(CodeReader reader) {
		reader.nextChar();
		return new Token(TokenKind.ERROR, reader.position).lexeme(reader.lexeme());
	}

	public static Token scanEOF(CodeReader reader) {
		reader.nextChar();
		return new Token(EOF, reader.position);
	}

	public static Token scanWhiteSpaces(CodeReader reader) {
		reader.skipWhitespaces();
		return new Token(WHITE_SPACES, reader.position).lexeme(reader.lexeme());    // by default, white spaces are ignored.
	}

	public static Token scanLineTerminator(CodeReader reader) {
		if (reader.acceptLineTerminator()) {
			return new Token(LINE_TERMINATOR, reader.position).lexeme(reader.lexeme());
		}
		return null;
	}

	public static Token scanComma(CodeReader reader)        { return scanSingleCharToken(reader, COMMA); }
	public static Token scanSemicolon(CodeReader reader)    { return scanSingleCharToken(reader, SEMICOLON);}
	public static Token scanColon(CodeReader reader)        { return scanSingleCharToken(reader, COLON);}
	public static Token scanLeftParen(CodeReader reader)    { return scanSingleCharToken(reader, LEFT_PAREN);}
	public static Token scanRightParen(CodeReader reader)   { return scanSingleCharToken(reader, RIGHT_PAREN);}
	public static Token scanLeftBracket(CodeReader reader)  { return scanSingleCharToken(reader, LEFT_BRACKET);}
	public static Token scanRightBracket(CodeReader reader) { return scanSingleCharToken(reader, RIGHT_BRACKET);}
	public static Token scanLeftBrace(CodeReader reader)    { return scanSingleCharToken(reader, LEFT_BRACE);}
	public static Token scanRightBrace(CodeReader reader)   { return scanSingleCharToken(reader, RIGHT_BRACE);}
	public static Token scanQuestion(CodeReader reader)     { return scanSingleCharToken(reader, QUESTION);}
	public static Token scanAt(CodeReader reader)           { return scanSingleCharToken(reader, AT);}
	public static Token scanPlus(CodeReader reader)         { return scanSingleCharToken(reader, PLUS);}
	public static Token scanStar(CodeReader reader)         { return scanSingleCharToken(reader, STAR);}
	public static Token scanBar(CodeReader reader)          { return scanSingleCharToken(reader, BAR);}

	protected static Token scanSingleCharToken(CodeReader reader, String kind) {
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
	public static Token scanStringLiteral(CodeReader reader) {
		return scanStringLiteral(reader, '\"');
	}
	public static Token scanStringLiteral(CodeReader reader, char quote) {
		reader.accept(quote);
		while (reader.ch != quote
				&& reader.ch != CR
				&& reader.ch != LF
				&& reader.ch != Constants.EOF) {
			scanEscapedChar(reader);
			reader.nextChar();
		}
		if (reader.accept(quote)) {
			return new Token(STRING_LITERAL, reader.position).lexeme(reader.lexeme());
		} else {
			lexError(reader, "invalid string literal: " + reader.lexeme());
			return null;
		}
	}

	public static Token scanCharLiteral(CodeReader reader) { return scanCharLiteral(reader, '\''); }
	public static Token scanCharLiteral(CodeReader reader, char quote) {
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
	private static char scanEscapedChar(CodeReader reader) {
		if (reader.ch == '\\') {

		}
		return 0;
	}

	public static Token scanLineComment(CodeReader reader) {
		reader.skipToEndOfLine();
		return new Token(LINE_COMMENT, reader.position).lexeme(reader.lexeme());
	}

	public static Token scanIdentifier(CodeReader reader) {
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
