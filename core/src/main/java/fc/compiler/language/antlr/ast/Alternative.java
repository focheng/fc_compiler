package fc.compiler.language.antlr.ast;

import fc.compiler.common.ast.Expression;
import fc.compiler.common.ast.expression.CompositeExpression;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author FC
 */
@Getter @Setter @Accessors(fluent = true) @ToString(callSuper = true)
public class Alternative extends CompositeExpression<Expression> {
	// ElementOptions

	public Alternative(List<Expression> elements) {
		children(elements);
	}
}
