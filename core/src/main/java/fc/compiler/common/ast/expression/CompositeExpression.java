package fc.compiler.common.ast.expression;

import fc.compiler.common.ast.AstNode;
import fc.compiler.common.ast.Expression;
import fc.compiler.common.ast.ExpressionBase;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * Composite node with children nodes.
 * @author FC
 */
@Getter @Setter @Accessors(fluent = true) @ToString
public class CompositeExpression<T extends Expression> extends ExpressionBase {
	List<T> children = new ArrayList<>();

	public void add(T child) {
		children.add(child);
	}

	@Override
	public String toString() {
		return children.toString();
	}

	public static Expression simplify(CompositeExpression<Expression> composite) {
		if (composite == null || composite.children() == null)
			return EmptyExpression.singleton;
		return switch (composite.children().size()) {
			case 0 -> EmptyExpression.singleton;
			case 1 -> composite.children().get(0);
			default -> composite;
		};
	}

}
