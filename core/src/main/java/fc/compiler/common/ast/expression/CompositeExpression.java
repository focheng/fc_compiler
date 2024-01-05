package fc.compiler.common.ast.expression;

import fc.compiler.common.ast.Expression;
import fc.compiler.common.ast.ExpressionBase;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * @author FC
 */
@Getter @Setter @Accessors(fluent = true) @ToString
public class CompositeExpression<T extends Expression> extends ExpressionBase {
	List<T> expressions = new ArrayList<>();

	public void add(T expression) {
		expressions.add(expression);
	}
}
