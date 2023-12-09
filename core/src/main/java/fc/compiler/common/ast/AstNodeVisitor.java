package fc.compiler.common.ast;

/**
 * @author FC
 */
@FunctionalInterface
public interface AstNodeVisitor {
	/**
	 * Visitor Design Pattern.
	 * @param that  the object to visit
	 * @param p     parameter object of P
	 * @return
	 * @param <R>   generic result type
	 * @param <P>   generic parameter type
	 */
	<R, P> R visitNode(AstNode that, P p);
}
