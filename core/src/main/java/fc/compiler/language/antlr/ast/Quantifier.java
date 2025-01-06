package fc.compiler.language.antlr.ast;

import lombok.*;
import lombok.experimental.Accessors;

/**
 * @author FC
 */
@Getter @Accessors(fluent=true, chain=true)
@AllArgsConstructor
public enum Quantifier {
	EXACTLY_ONE     ("",  false, false),
	ZERO_OR_ONE     ("?", false, true),
	ZERO_OR_MORE    ("*", true,  true),
	ONE_OR_MORE     ("+", true, false),
	;

	private String code;
	private boolean isMultiple;
	private boolean isOptional;

	public static Quantifier of(String code) {
		return switch (code) {
			case "?" -> ZERO_OR_ONE;
			case "*" -> ZERO_OR_MORE;
			case "+" -> ONE_OR_MORE;
			default  -> null;
		};
	}
}
