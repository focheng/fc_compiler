package fc.compiler.lexer;

import fc.compiler.token.Token;
import fc.compiler.token.TokenKind;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import static fc.compiler.lexer.Constants.*;


/**
 * Base class for Lexer (Lexical Analyzer).
 * @author FC
 */
@Slf4j @Accessors(chain = true)
public class LexerBase implements Lexer {
//	protected TokenFactory tokenFactory;
//	@Getter @Setter	protected CodeReader reader;
	@Getter @Setter	protected IsIdentifierStart isIdentifierStart;
	@Getter @Setter	protected IsIdentifierPart isIdentifierPart;
	@Getter @Setter	protected Lexer whitespaceLexer;

	public LexerBase() {
		isIdentifierStart = Character::isJavaIdentifierStart;
		isIdentifierPart  = Character::isJavaIdentifierPart;
		whitespaceLexer = LexerBase::scanWhiteSpaces;
	}

	public Token scan(CodeReader reader) {
		while (true) {
			reader.sp = reader.bp;
			reader.newPosition();
			switch (reader.ch) {
				case SPACE: case TAB: case FF: case LF: case CR:
					Token token = whitespaceLexer.scan(reader);
					if (token != null) {
						return token;
					}

				case Constants.EOF: return new Token(TokenKind.EOF, reader.position);

				default:
					if (isIdentifierStart.is(reader.ch))      return scanIdentifier(reader);
//					if (isDigit(reader.ch))                return scanNumber();
//					if (isStringLiteralStart(reader.ch))   return scanStringLiteral(ch);
//					if (isCharLiteralQuotation(reader.ch)) return scanCharLiteral(ch);

					lexError("Unsupported lexeme " + reader.ch + " @ " + reader.position);
					return null;
			}
		}
	}

	protected void lexError(String s) {
		log.warn(s);
	}

	// -- spaces --
	protected static Token scanWhiteSpaces(CodeReader reader) {
		while (reader.accept(SPACE, TAB, FF, LF, CR)) {
			// accept() already read the next character
		}
		return null;    // by default, white spaces are ignored.
	}

	// -- comments --

	// -- identifier --
	protected Token scanIdentifier(CodeReader reader) {
		reader.nextChar();

		while (isIdentifierPart.is(reader.ch)) {
			reader.nextChar();
		}

		return new Token(TokenKind.IDENTIFIER, reader.position)
				.setLexeme(reader.getLexeme());
	}


	// -- literals --

}
