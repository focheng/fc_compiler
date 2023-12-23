package fc.compiler.common.ast.expression;

import fc.compiler.common.ast.Expression;
import fc.compiler.common.ast.ExpressionBase;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * e.g.
 *  - a.b.c
 *  - ClassA::b
 *  - pointer->b
 *  - array[i].b
 * @author FC
 */
@Getter @Setter @Accessors(fluent = true) @ToString(callSuper = true)
public class ComponentSelect extends ExpressionBase {
	Expression expression;  // could be also a MemberSelect for nesting
	Identifier identifier;
}
