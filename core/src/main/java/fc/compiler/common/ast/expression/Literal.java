package fc.compiler.common.ast.expression;

import fc.compiler.common.ast.ExpressionBase;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author FC
 */
@Getter @Setter @Accessors(fluent = true) @ToString
public class Literal<T> extends ExpressionBase {
	T value;
}
