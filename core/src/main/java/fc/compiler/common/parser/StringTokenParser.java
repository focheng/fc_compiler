package fc.compiler.common.parser;

import fc.compiler.common.ast.AstNode;

/**
 * @author FC
 */
@Deprecated
@FunctionalInterface
public interface StringTokenParser<T extends AstNode> {
	T parse(StringTokenReader reader, StringTokenParserRegistry registry);
	default T parse(StringTokenReader reader) { return parse(reader, null); }
}
