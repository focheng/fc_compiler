package fc.compiler.common.ast;

/**
 * @author FC
 */
@FunctionalInterface
public interface ExpressionVisitor extends AstNodeVisitor {
	<R, P> R visitExpression(Expression that, P p);

	@Override
	default <R, P> R visitNode(AstNode that, P p) {
		return visitExpression((Expression) that, p);
	}
}
