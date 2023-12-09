package fc.compiler.common.ast;

/**
 * @author FC
 */
@FunctionalInterface
public interface StatementVisitor extends AstNodeVisitor {
	<R, P> R visitStatement(Statement that, P p);

	@Override
	default <R, P> R visitNode(AstNode that, P p) {
		return visitStatement((Statement) that, p);
	}
}
