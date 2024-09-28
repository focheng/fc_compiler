package fc.compiler.language.antlr.ast;

import fc.compiler.common.ast.Expression;
import fc.compiler.common.ast.ExpressionBase;
import fc.compiler.common.ast.expression.Identifier;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author FC
 */
@Getter @Setter @Accessors(fluent = true) @ToString
public class QuantifiedExpression extends ExpressionBase {
	Expression expression;
	String quantifierType;  // *, ?, +
	// range {n}, {n,}, {n,m}
//	int min = -1;
//	int max = -1;

	public boolean isOptional() {
		return "*".equals(quantifierType) || "?".equals(quantifierType);
	}
	public boolean isMultiple() {
		return "*".equals(quantifierType) || "+".equals(quantifierType);
	}

	public boolean isZeroOrOne()    { return "?".equals(quantifierType); }
	public boolean isZeroOrMore()   { return "*".equals(quantifierType); }
	public boolean isOneOrMore()    { return "+".equals(quantifierType); }
}
