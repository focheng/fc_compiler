package fc.compiler.common.ast.expression;

import fc.compiler.common.ast.Expression;
import fc.compiler.common.ast.ExpressionBase;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * @author FC
 */
@Getter @Setter @Accessors(fluent = true) @ToString @NoArgsConstructor @AllArgsConstructor
public class BinaryExpression extends ExpressionBase {
	protected Expression leftOperand;
	protected String operator;
	protected Expression rightOperand;
}
