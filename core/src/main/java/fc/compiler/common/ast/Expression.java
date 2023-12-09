package fc.compiler.common.ast;

/**
 * Root interface for Expression Nodes.
 * @author FC
 */
@FunctionalInterface
public interface Expression extends AstNode {
	<R, P> R accept(ExpressionVisitor visitor, P p);

	@Override
	default <R, P> R accept(AstNodeVisitor visitor, P p) {
		return accept((ExpressionVisitor) visitor, p);
	}
}
