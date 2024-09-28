package fc.compiler.common.ast;

/**
 * @param <R>   generic result type. Use Void class if no need.
 * @param <P>   generic parameter type. Use Void class if no need.
 * @author FC
 */
@FunctionalInterface
public interface AstNodeVisitor<R, T extends AstNode, P> {
	/**
	 * Visitor Design Pattern.
	 * @param node  the object to visit
	 * @param p     parameter object of P
	 * @return the result if any.
	 */
	R visit(T node, P p);
}
