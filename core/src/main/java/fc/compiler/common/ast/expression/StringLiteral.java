package fc.compiler.common.ast.expression;

import lombok.ToString;

/**
 * @author FC
 */
public class StringLiteral extends Literal<String> {
	public StringLiteral(String value) {
		this.value = value;
	}
}
