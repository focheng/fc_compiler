package fc.compiler.common.parser;

import fc.compiler.common.ast.AstNode;

/**
 * Parser hub will dispatch registered parser to the given token kind.
 * @author FC
 */
@Deprecated
@FunctionalInterface
public interface StringTokenParserHub /*extends StringTokenParser*/ {
//	public Parser get(String tokenKind);
//	public Parser put(String tokenKind, Parser parser);

	AstNode parse(StringTokenReader reader, StringTokenParserRegistry registry);
//	{
//		Token token = reader.token();
//		Parser parser = registry.get(token.kind());
//		if (parser != null) {
//			AstNode node = parser.parse(reader);
//			return node;
//		}
//		return null;
//	}

	default AstNode parse(StringTokenReader reader) { return parse(reader, null); }
}
