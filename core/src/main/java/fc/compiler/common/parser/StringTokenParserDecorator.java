package fc.compiler.common.parser;

import fc.compiler.common.ast.AstNode;
import fc.compiler.common.ast.Expression;

/**
 * Parser Decorator for Nested Expression.
 * @author FC
 */
@Deprecated
@FunctionalInterface
public interface StringTokenParserDecorator extends StringTokenParser {
	AstNode parse(StringTokenReader reader, StringTokenParserRegistry registry, Expression expr);

	default AstNode parse(StringTokenReader reader, StringTokenParserRegistry registry) { return parse(reader, registry, null); }
}
