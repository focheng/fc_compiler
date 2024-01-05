package fc.compiler.common.ast.statement;

import fc.compiler.common.ast.Expression;
import fc.compiler.common.ast.StatementBase;
import fc.compiler.common.ast.expression.Assignment;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author FC
 */
@Getter @Setter @Accessors(fluent= true)
public class ExpressionStatement extends StatementBase {
	Expression expression;
}
