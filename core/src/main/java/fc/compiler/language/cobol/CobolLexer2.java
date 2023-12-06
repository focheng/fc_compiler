package fc.compiler.language.cobol;

import fc.compiler.common.lexer.CodeReader;
import fc.compiler.common.lexer.Constants;
import fc.compiler.common.lexer.Lexer;
import fc.compiler.common.lexer.LexerBase;
import fc.compiler.common.token.Token;
import fc.compiler.common.token.TokenKind;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import static fc.compiler.common.lexer.Constants.*;

/**
 * @author FC
 */
@Slf4j
public class CobolLexer2 extends LexerBase {
	//	protected TokenFactory tokenFactory;
	@Getter	@Setter	protected IsIdentifierStart isIdentifierStart;
	@Getter @Setter	protected IsIdentifierPart isIdentifierPart;
	@Getter @Setter	protected Lexer whitespaceLexer;

	public CobolLexer2() {
		isIdentifierStart = Character::isJavaIdentifierStart;
		isIdentifierPart  = Character::isJavaIdentifierPart;
		whitespaceLexer = this::scanWhiteSpaces;
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

					lexError(reader, "Unsupported lexeme " + reader.ch + " @ " + reader.position);
					return null;
			}
		}
	}

	// -- spaces --
	protected Token scanWhiteSpaces(CodeReader reader) {
		while (reader.accept(SPACE, TAB, FF, LF, CR)) {
			// accept() already read the next character
		}
		return null;    // by default, white spaces are ignored.
	}

	// -- comments --

	// -- identifier --
	public Token scanIdentifier(CodeReader reader) {
		reader.nextChar();

		while (isIdentifierPart.is(reader.ch)) {
			reader.nextChar();
		}

		return new Token(TokenKind.IDENTIFIER, reader.position)
				.setLexeme(reader.getLexeme());
	}


	// -- literals --

}
