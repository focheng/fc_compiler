package fc.compiler.common.parser;

import fc.compiler.common.ast.AstNode;
import fc.compiler.common.ast.Expression;

/**
 * Parser Decorator for Nested Expression.
 * @author FC
 */
@FunctionalInterface
public interface ParserDecorator extends Parser {
	AstNode parse(TokenReader reader, ParserRegistry registry, Expression expr);

	default AstNode parse(TokenReader reader, ParserRegistry registry) { return parse(reader, registry, null); }
}
