package fc.compiler.common.parser;

import fc.compiler.common.ast.AstNode;

/**
 * @author FC
 */
@FunctionalInterface
public interface ParseFunction<T extends AstNode> {
	T parse();
}
