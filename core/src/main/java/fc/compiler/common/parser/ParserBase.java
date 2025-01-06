package fc.compiler.common.parser;

import fc.compiler.common.ast.AstNode;
import fc.compiler.common.lexer.Lexer;
import fc.compiler.common.token.Token;

/**
 * @author FC
 */
public abstract class ParserBase<Kind, T extends Token<Kind>, Node extends AstNode>
		extends TokenReaderBase<Kind, T>
		implements Parser<Node> {
	public ParserBase(Lexer<T> lexer) {
		super.lexer = lexer;

		nextToken();
	}
}
