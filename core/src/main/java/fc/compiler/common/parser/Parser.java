package fc.compiler.common.parser;

import fc.compiler.common.ast.AstNode;
import fc.compiler.common.lexer.CodeReader;
import fc.compiler.common.lexer.Lexer;

import java.io.Reader;

/**
 * @author FC
 */
@FunctionalInterface
public interface Parser<T extends AstNode> {
	T parse(TokenReader reader, ParserRegistry registry);
	default T parse(TokenReader reader) { return parse(reader, null); }
}
