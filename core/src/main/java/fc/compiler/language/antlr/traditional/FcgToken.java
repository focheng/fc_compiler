package fc.compiler.language.antlr.traditional;

import lombok.*;
import lombok.experimental.Accessors;

/**
 * @author FC
 */
@Data
@Accessors(fluent = true, chain = true)
@NoArgsConstructor @AllArgsConstructor
public class FcgToken {
	protected FcgTokenKind kind;
	protected String lexeme;

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
