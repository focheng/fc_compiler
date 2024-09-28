package fc.compiler.language.antlr.ast;

import fc.compiler.common.ast.Expression;
import fc.compiler.common.ast.expression.CompositeExpression;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author FC
 */
@Getter @Setter @Accessors(fluent = true) @ToString(callSuper = true)
public class Alternatives extends CompositeExpression<Expression> {
}
