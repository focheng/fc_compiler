package fc.compiler.common.parser;

import java.util.HashMap;
import java.util.Map;

/**
 * @author FC
 */
@Deprecated
public class StringTokenParserRegistry {
	protected StringTokenParser defaultParser;
//	protected Parser statementParser;
//	protected Parser expressionParser;
	protected Map<String, StringTokenParser> parsers = new HashMap<>();

	public StringTokenParser get(String tokenKind) {
		StringTokenParser parser = parsers.get(tokenKind);
		return parser != null ? parser : defaultParser;
	}

	public StringTokenParser put(String tokenKind, StringTokenParser parser) {
		return parsers.put(tokenKind, parser);
	}
}
