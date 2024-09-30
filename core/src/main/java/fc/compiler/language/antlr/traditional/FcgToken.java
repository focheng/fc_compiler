package fc.compiler.language.antlr.traditional;

import fc.compiler.common.token.TokenBase;

/**
 * @author FC
 */
public class FcgToken extends TokenBase<FcgTokenKind> {

	public FcgToken(FcgTokenKind kind, String lexeme) {
		this.kind = kind;
		this.lexeme = lexeme;
	}

	@Override
	public String toString() {
		if (kind == FcgTokenKind.NEW_LINE) {
			return kind.name() + "('\\n')";
		} else if (kind.tag() == FcgTokenKindTag.KEYWORD) {
			return kind.name();
		} else if (lexeme == null) {
			if (kind.lexeme() == null)
				return kind.name();
			else
				return kind.name() + "(" + kind.lexeme() + ")";
		}

		return kind.name() + "(" + lexeme + ")";
	}
}
