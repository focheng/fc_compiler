package fc.compiler.common.parser;

import fc.compiler.common.ast.AstNode;
import fc.compiler.common.lexer.CodeReader;
import fc.compiler.common.lexer.Lexer;

/**
 * @author FC
 */
public interface Parser {
	AstNode parse(Lexer lexer, CodeReader reader);
}
