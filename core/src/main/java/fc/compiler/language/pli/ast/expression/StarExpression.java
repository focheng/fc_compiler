package fc.compiler.language.pli.ast.expression;

import fc.compiler.common.ast.ExpressionBase;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author FC
 */
@Data
@Accessors(fluent = true, chain = true)
@ToString(callSuper = true)
public class StarExpression extends ExpressionBase {
}
