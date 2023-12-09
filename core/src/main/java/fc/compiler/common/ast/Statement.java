package fc.compiler.common.ast;

/**
 * Root interface for Statement Nodes.
 * @author FC
 */
@FunctionalInterface
public interface Statement extends AstNode {
	<R, P> R accept(StatementVisitor visitor, P p);

	@Override
	default <R, P> R accept(AstNodeVisitor visitor, P p) {
		return accept((StatementVisitor) visitor, p);
	}
}
