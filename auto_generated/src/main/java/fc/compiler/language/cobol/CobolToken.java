package fc.compiler.language.cobol;

import fc.compiler.common.token.TokenBase;

/**
 * @author FC
 */
public class CobolToken extends TokenBase<CobolTokenKind> {

	public CobolToken(CobolTokenKind kind, String lexeme) {
		this.kind = kind;
		this.lexeme = lexeme;
	}

	@Override
	public String toString() {
		if (kind == CobolTokenKind.NEW_LINE) {
			return kind.name() + "('\\n')";
		} else if (kind.tag() == CobolTokenKindTag.KEYWORD) {
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
