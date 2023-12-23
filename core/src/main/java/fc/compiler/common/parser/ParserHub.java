package fc.compiler.common.parser;

import fc.compiler.common.ast.AstNode;
import fc.compiler.common.token.Token;

/**
 * Parser hub will dispatch registered parser to the given token kind.
 * @author FC
 */
@FunctionalInterface
public interface ParserHub /*extends Parser */{
	public Parser get(String tokenKind);
//	public Parser put(String tokenKind, Parser parser);

//	default AstNode parse(TokenReader reader) {
//		Token token = reader.token();
//		Parser parser = get(token.kind());
//		if (parser != null) {
//			AstNode node = parser.parse(reader);
//			return node;
//		}
//		return null;
//	}
}
