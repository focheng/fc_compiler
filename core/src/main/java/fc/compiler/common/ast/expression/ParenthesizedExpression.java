package fc.compiler.common.ast.expression;

import fc.compiler.common.ast.Expression;
import fc.compiler.common.ast.ExpressionBase;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author FC
 */
@Getter @Setter @Accessors(fluent = true) @ToString(callSuper = true)
@NoArgsConstructor
public class ParenthesizedExpression extends ExpressionBase {
	private Expression expression;
	private String operator = "()";

	public ParenthesizedExpression(Expression expr) {
		this.expression = expr;
	}
}
