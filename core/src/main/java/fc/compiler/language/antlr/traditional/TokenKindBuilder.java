package fc.compiler.language.antlr.traditional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import static fc.compiler.common.util.StrUtils.containsLetterOrDigit;

/**
 * @author FC
 */
@Accessors(fluent = true, chain = true)
public class TokenKindBuilder extends ClassBuilderBase {

//	private Map<String, String> lexeme2KindNames = new LinkedHashMap<>();  // lexeme -> token kind name
	private Map<String, String> punctuators = new HashMap<>();  // lexeme -> token kind name
	private Map<String, String> keywords    = new HashMap<>();  // lexeme -> token kind name

	@Setter LexerBuilder lexerBuilder;

	public String toCode() {
		add1("// -- ERROR --");
		add1("ERROR_TOKEN,");
		addEmptyLine();

		add1("// -- EOF --");
		add1("EOF,");
		addEmptyLine();

		add1("// -- white space --");
		add1("WHITE_SPACES,");
		add1("NEW_LINE,");
		addEmptyLine();

		add1("// -- comments --");
		add1("LINE_COMMENT            (null, COMMENT),");
		addEmptyLine();

		add1("// -- literals --");
		add1("STRING_LITERAL,");
		addEmptyLine();

		add1("// -- separators (punctuators) --");
		punctuators.forEach((lexeme, kindName) -> {
			add1(kindName + "\t\t\t(\"" + lexeme + "\"),");
		});
		addEmptyLine();

		add1("// -- IDENTIFIER --");
		add1("IDENTIFIER              (null),");
		addEmptyLine();

		add1("// -- Keywords --");
		keywords.forEach((lexeme, kindName) -> {
			add1(kindName + "\t\t\t(\"" + lexeme + "\", KEYWORD),");
		});
		addEmptyLine();

		return sb.toString();
	}

	public String getTokenKind(String lexeme) {
//		String tokenKindName = lexeme2KindNames.get(lexeme);
		String tokenKindName = keywords.get(lexeme);
		if (tokenKindName == null)
			tokenKindName = punctuators.get(lexeme);
		if (tokenKindName == null) {
			tokenKindName = getDefaultTokenKindName(lexeme);
			add(tokenKindName, lexeme);
		}
		return tokenKindName;
	}

	public void add(String tokenKind, String lexeme) {
//		lexeme2KindNames.put(lexeme, tokenKind);
		if (containsLetterOrDigit(lexeme)) {
			keywords.put(lexeme, tokenKind);
		} else {
			punctuators.put(lexeme, tokenKind);
			lexerBuilder.rootCharNodes().add(new LexerBuilder.CharNode(lexeme, tokenKind));
		}
	}

	// default map from punctuator (separator) to token kind name if not defined explicitly.
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

	//	@Getter @Setter @Accessors(fluent = true, chain = true)
//	@NoArgsConstructor @AllArgsConstructor
//	public static class Lexeme2TokenKindName {
//		String lexeme;
//		String tokenKindName;
//	}

//	public static class TokenKindMetadata {
//		private String name;
//		private String lexeme;
//		private FcgTokenKindTag tag;
//	}
}
