package fc.compiler.common.ast;

/**
 * @author FC
 */
public class ExpressionBase implements Expression {
	@Override
	public <R, P> R accept(ExpressionVisitor visitor, P p) {
		visitor.visitExpression(this, p);
		return null;
	}
}
