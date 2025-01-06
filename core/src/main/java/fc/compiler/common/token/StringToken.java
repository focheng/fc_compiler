package fc.compiler.common.token;

import fc.compiler.common.lexer.Position;

@Deprecated
public class StringToken extends TokenBase<String> {
	public StringToken(String kind, Position position) {
		this.kind = kind;
		this.position = position;
	}

	public StringToken(String kind, String lexeme, Position position) {
		this.kind = kind;
		this.lexeme = lexeme;
		this.position = position;
	}
}
