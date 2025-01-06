package fc.compiler.common.ast.expression;

import lombok.NoArgsConstructor;

/**
 * @author FC
 */
@NoArgsConstructor
public class NumberLiteral extends Literal<Number> {
	public NumberLiteral(Number value) {
		this.value = value;
	}
}
