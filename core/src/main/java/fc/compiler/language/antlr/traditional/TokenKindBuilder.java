package fc.compiler.language.antlr.traditional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author FC
 */
public class TokenKindBuilder {
	@Getter @Setter @Accessors(fluent = true, chain = true)
	@NoArgsConstructor @AllArgsConstructor
	public static class Lexeme2TokenKindName {
		String lexeme;
		String tokenKindName;
	}

	// default map from punctuator (separator) to token kind name if not defined explicitly.
	private static final Map<String, String> DEFAULT_PUNCTUATORS = new HashMap<>();

	private Map<String, String> punctuators = new HashMap<>();  // lexeme -> token kind name
	private Map<String, String> keywords    = new HashMap<>();  // lexeme -> token kind name

	public String getTokenKind(String lexeme) {
		String tokenKindName = keywords.get(lexeme);
		if (tokenKindName == null)
			tokenKindName = punctuators.get(lexeme);
		if (tokenKindName == null)
			tokenKindName = getDefaultTokenKindName(lexeme);
		return tokenKindName;
	}

	public static String getDefaultTokenKindName(char lexeme) {
		return AsciiTable.table127[lexeme].token;
	}

	public static String getDefaultTokenKindName(String lexeme) {
		if (lexeme.length() == 1 && lexeme.charAt(0) < 127) {
			return getDefaultTokenKindName(lexeme.charAt(0));
		} else {
			List<Character> list = new ArrayList<>();
			for (Character c : lexeme.toCharArray()) {
				list.add(c);
			}
			boolean areAll127 = list.stream().allMatch(c -> c < 127);
			if (areAll127)
				return list.stream().map(c -> getDefaultTokenKindName(c))
						.collect(Collectors.joining("_"));
			else
				return null;
		}
	}

//	public static class TokenKindMetadata {
//		private String name;
//		private String lexeme;
//		private FcgTokenKindTag tag;
//	}
}
