package fc.compiler.common.ast;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author FC
 */
@Getter @Setter @Accessors(fluent= true) @ToString
public class ExpressionBase implements Expression {
	/** the result type of this expression evaluation. used in Sentamics analysis phase. */
	protected Expression type;

	@Override
	public <R, P> R accept(ExpressionVisitor visitor, P p) {
		visitor.visitExpression(this, p);
		return null;
	}
}
