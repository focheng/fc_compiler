package fc.compiler.language.antlr.traditional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Lexer Class Builder.
 *
 * Grammar rule is token kind : lexeme =>  CharNode => Lexer scanner
 * v      PLUS:      '+'
 * v      PLUS_PLUS: '++'
 * v      PLUS_EQ:   '+='
 * CharNodes are
 * v     '+', PLUS, children[
 * v         '+', PLUS_PLUS
 * v         '=', PLUS_EQ
 * v        ]
 * Scanning codes are
 * v     case '+': nextChar;
 * v        switch (ch) {
 * v			case '+':	nextChar();	return PLUS_PLUS.nextToken();
 * v			case '=':	nextChar();	return PLUS_EQ.nextToken();
 * v			default:	nextChar();	return PLUS.newToken();
 * v		}
 *
 * @author FC
 */
@Accessors(fluent = true, chain = true)
public class LexerBuilder extends ClassBuilderBase {

	@Getter @Setter private List<CharNode> rootCharNodes;

	public String toCode() {
		String s = sb.toString();
		sb.setLength(0);
		buildFileHeader();
		buildScanToken();
		buildScanMethods();
		sb.append(s);
		buildFileFooter();
		return sb.toString();
	}

	public void buildScanToken() {
		add1(STR."public \{lang}Token scanToken() {");
			add2("resetTokenContext();");
			add2("switch (ch) {");
				add3("case EOF_CHAR:                  return EOF.newToken();");
				add3("case '\\r':");
				add3("case '\\n':                      return scanNewLine();");
				add3("case ' ':");
				add3("case '\\t':");
				add3("case VT:");
				add3("case FF:                        return scanNewLine();");
				buildCases(makeGroupMap(rootCharNodes));
				add3("default:");
					add4("if (isIdentifierStart(ch))");
						add5("return scanIdentifier();");
					add4("return lexError(\"scanToken()\");");
			add2("}");
		add1("}");
	}

	protected void buildCases(Map<Character, List<CharNode>> groups) {
		increaseIndent();
		groups.forEach((ch, group) -> {
			if (group.size() == 1) {
				String kind = group.getFirst().kind;
				add2(STR."case '\{ch}':\tnextChar();\treturn \{kind}.nextToken();");
			} else if (group.size() == 2) {
				CharNode node1 = group.get(0);
				CharNode node2 = group.get(0);
				String commonPrefix = StringUtils.getCommonPrefix(node1.lexeme, node2.lexeme);
				String kindLonger = node1.lexeme.length() > node2.lexeme.length() ? node2.kind : node1.kind;
				String kind       = node1.lexeme.length() > node2.lexeme.length() ? node1.kind : node2.kind;
				if (commonPrefix.length() == 2) {
					char ch2 = node1.lexeme.charAt(1);
					add2(STR."case '\{ch}':\tnextChar();\treturn (optionalChar('\{ch2}') ? \{kindLonger} ? \{kind}).nextToken();");
				} else {    // >= 3
					String expectedChars = commonPrefix.substring(1);
					add2(STR."case '\{ch}':\tnextChar();\treturn (optionalChar(\"\{expectedChars}\") ? \{kindLonger} ? \{kind}).nextToken();");
				}
			} else {    // group.size() >= 3
				add2(STR."case '\{ch}':\tnextChar();");
				buildSubSwitch(group);
			}
		});
		decreaseIndent();
	}

	private void buildSubSwitch(List<CharNode> group) {
		// remove the common 1st char.
		group.forEach(node -> node.lexeme = node.lexeme.substring(1));
		Optional<CharNode> singleCharNode = group.stream().filter(node -> node.lexeme.isEmpty()).findFirst();
		singleCharNode.ifPresent(group::remove);
		Map<Character, List<CharNode>> subGroups = makeGroupMap(group);

		add3("switch (ch) {");
		increaseIndent();
		buildCases(subGroups);
		decreaseIndent();

		if (singleCharNode.isPresent()) {
			String kind = singleCharNode.get().kind;
			add4(STR."default:\tnextChar();\treturn \{kind}.newToken();");
		} else {
			add4("default:\treturn lexError(\"scanToken()\");");
		}
		add3("}");
	}

	private Map<Character, List<CharNode>> makeGroupMap(List<CharNode> group) {
		return group.stream().collect(Collectors.groupingBy(node -> node.lexeme.charAt(0)));
	}

	private void buildScanMethods() {
		buildScanIdentifier();
//		buildScanComment();
	}

	private void buildScanIdentifier() {

	}

	public void buildFileHeader() {
		add(STR."package \{packageName};");
		addEmptyLine();
		add(STR."import TODO;");
		addEmptyLine();
		add(STR."public class \{lang}Lexer extends CodeReaderBase {");
	}

	public void buildFileFooter() {
		add("}");
	}

	@Getter @Setter @Accessors(fluent = true, chain = true)
	@NoArgsConstructor @AllArgsConstructor
	public static class CharNode {
		String lexeme;
		String kind;
	}
}
