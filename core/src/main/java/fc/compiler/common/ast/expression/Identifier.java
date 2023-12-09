package fc.compiler.common.ast.expression;

import fc.compiler.common.ast.ExpressionBase;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author FC
 */
@Getter @Setter @Accessors(chain = true)
public class Identifier extends ExpressionBase {
	String id;
}
