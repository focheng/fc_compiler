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
@Slf4j @Getter @Setter @Accessors(chain = true)
public class LexerBase implements Lexer {
	protected LexerMapper mapper;

	@Override
	public Token scan(CodeReader reader) {
		reader.sp = reader.bp;
		reader.newPosition();
		Lexer lexer = mapper.getLexer(reader.ch);
		if (lexer != null) {
			Token token = lexer.scan(reader);
			return token;
		} else {
			lexError(reader, "Unsupported lexeme '" + reader.ch + "' @ " + reader.position);
			return null;
		}
	}

	protected static Token lexError(CodeReader reader, String s) {
		log.warn(s);
		return new Token(ERROR, reader.position).setLexeme(reader.getLexeme());
	}

	protected static Token scanDummy(CodeReader reader) {
		reader.nextChar();
		return new Token(TokenKind.ERROR, reader.position).setLexeme(reader.getLexeme());
	}

	public static Token scanEOF(CodeReader reader) {
		reader.nextChar();
		return new Token(EOF, reader.position);
	}
	public static Token scanComma(CodeReader reader) { return singleCharToken(reader, COMMA); }
	public static Token scanSemicolon(CodeReader reader) {return singleCharToken(reader, SEMICOLON);}
	public static Token scanColon(CodeReader reader) {return singleCharToken(reader, COLON);}
	public static Token scanLeftParen(CodeReader reader) {return singleCharToken(reader, LEFT_PAREN);}
	public static Token scanRightParen(CodeReader reader) {return singleCharToken(reader, RIGHT_PAREN);}
	public static Token scanLeftBracket(CodeReader reader) {return singleCharToken(reader, LEFT_BRACKET);}
	public static Token scanRightBracket(CodeReader reader) {return singleCharToken(reader, RIGHT_BRACKET);}
	public static Token scanLeftBrace(CodeReader reader) {return singleCharToken(reader, LEFT_BRACE);}
	public static Token scanRightBrace(CodeReader reader) {return singleCharToken(reader, RIGHT_BRACE);}
	public static Token scanQuestion(CodeReader reader) {return singleCharToken(reader, QUESTION);}
	public static Token scanAt(CodeReader reader) {return singleCharToken(reader, AT);}

	protected static Token singleCharToken(CodeReader reader, String kind) {
		reader.nextChar();
		return new Token(kind, reader.position).setLexeme(reader.getLexeme());
	}

	public static Token scanSingleQuote(CodeReader reader) {
		return scanCharLiteral(reader, '\'');
	}

	public static Token scanDoubleQuote(CodeReader reader) {
		return scanStringLiteral(reader, '\"');
	}

	public static Token scanStringLiteral(CodeReader reader) {
		return scanStringLiteral(reader, '\"');
	}
	public static Token scanStringLiteral(CodeReader reader, char quote) {
		reader.nextChar();
		while (reader.ch != quote
				&& reader.ch != CR
				&& reader.ch != LF
				&& reader.ch != Constants.EOF) {
			scanEscapedChar(reader);
			reader.nextChar();
		}
		if (reader.ch == quote) {
			reader.nextChar();
			return new Token(STRING_LITERAL, reader.position).setLexeme(reader.getLexeme());
		} else {
			lexError(reader, "invalid string literal: " + reader.getLexeme());
			return null;
		}
	}

	public static Token scanCharLiteral(CodeReader reader, char quote) {
		reader.accept(quote);
		if (reader.accept(quote)) {
			return lexError(reader, "Empty character literal.");
		} else {
			scanEscapedChar(reader);
			if (reader.accept(quote)) {
				return new Token(CHAR_LITERAL, reader.position).setLexeme(reader.getLexeme());
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
}
