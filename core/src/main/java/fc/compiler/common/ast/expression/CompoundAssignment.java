package fc.compiler.common.ast.expression;

import fc.compiler.common.ast.Expression;
import fc.compiler.common.ast.ExpressionBase;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author FC
 */
@Getter @Setter @Accessors(fluent = true) @ToString(callSuper = true)
public class CompoundAssignment extends Assignment {
	String operator;

	public CompoundAssignment(Expression lhs, Expression rhs, String operator) {
		super(lhs, rhs);
		this.operator = operator;
	}
}
