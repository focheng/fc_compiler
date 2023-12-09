package fc.compiler.common.ast;

import java.lang.reflect.Parameter;

/**
 * Root interface for AST (Abstract Syntax Tree) Node.
 * @author FC
 */
@FunctionalInterface
public interface AstNode {
	/**
	 * Visitor Design Pattern.
	 * @param visitor
	 * @param p     parameter object of P
	 * @return
	 * @param <R>   generic result type
	 * @param <P>   generic parameter type
	 */
	<R, P> R accept(AstNodeVisitor visitor, P p);
}
