package fc.compiler.common.parser;

import java.util.HashMap;
import java.util.Map;

/**
 * @author FC
 */
public class ParserRegistry {
	protected Parser defaultParser;
//	protected Parser statementParser;
//	protected Parser expressionParser;
	protected Map<String, Parser> parsers = new HashMap<>();

	public Parser get(String tokenKind) {
		Parser parser = parsers.get(tokenKind);
		return parser != null ? parser : defaultParser;
	}

	public Parser put(String tokenKind, Parser parser) {
		return parsers.put(tokenKind, parser);
	}
}
