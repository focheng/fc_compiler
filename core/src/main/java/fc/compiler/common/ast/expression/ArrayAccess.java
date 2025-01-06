package fc.compiler.common.ast.expression;

import fc.compiler.common.ast.Expression;
import fc.compiler.common.ast.ExpressionBase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author FC
 */
@Getter @Setter @Accessors(fluent = true) @ToString(callSuper = true) @AllArgsConstructor
public class ArrayAccess extends ExpressionBase {
	Expression expression;
	Expression index;
}
