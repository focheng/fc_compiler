package fc.compiler.language.antlr.traditional;

import fc.compiler.language.antlr.ast.Rule;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author FC
 */
public class ParserBuilder extends ClassBuilderBase {
	private Map<String, String> parseMultipleMethods = new LinkedHashMap<>();

	public void buildFileHeader(String packageName, String lang) {
		add(STR."package \{packageName};");
		addEmptyLine();
		add(STR."import TODO;");
		addEmptyLine();
		add(STR."public class \{lang}Parser {");
	}

	public void buildFileFooter() {
		add("}");
	}

	public void buildEmptyParseMethod(Rule rule) {
		String ruleName = rule.name().id();
		String returnType = StringUtils.capitalize(ruleName);
		add1(STR."public \{returnType} parse\{returnType}()");
		add2("return null;");
		add1("}");
	}

	public void buildParseMethodStart(Rule rule, String uniqueKinds) {
		String ruleName = rule.name().id();
		String returnType = StringUtils.capitalize(ruleName);
		add1(STR."public \{returnType} parse\{returnType}() {");
		if (!StringUtils.isBlank(uniqueKinds)) {
			add2(STR."if (!token.kind.isAnyOf(\{uniqueKinds})) return null;");
			addEmptyLine();
		}
		add2(STR."\{returnType} result = new \{returnType}();");
	}

	public void buildParseMethodEnd(Rule rule) {
		add2("return result;");
		add1("}");
		addEmptyLine();
	}

	public void buildParseReturnedIdentifier() {
		add2("Identifier result = parseIdentifier();");
	}

	public void buildParseIdentifier(String variable) {
		add2(STR."result.\{variable}(parseIdentifier());");
	}

	public void buildParseStringLiteral() {
		add1("parseStringLiteral();");
	}

	/**
	 * e.g. "acceptToken(COMMA);"
	 */
	public void buildAcceptToken(String expectedToken) {
		add2(STR."acceptToken(\{expectedToken});");
	}

	/**
	 * e.g. "acceptAnyToken(IDENTIFICATION, ID);"
	 */
	public void buildAcceptAnyToken(String allTokens) {
		add2(STR."acceptAnyToken(\{allTokens});");
	}
	/**
	 * e.g. "optionalToken(COMMA);"
	 */
	public void buildOptionalToken(String expectedToken) {
		add2(STR."optionalToken(\{expectedToken});");
	}

	/**
	 * e.g. "result.xxx(parseXXX());"
	 */
	public void buildParseAndSet(String name) {
		String capitalizedName = StringUtils.capitalize(name);
		add2(STR."result.\{name}(parse\{capitalizedName}));");
	}

	/**
	 * e.g. "result.statementList(parseStatementList());"
	 */
	public void buildStatementParseMultiples(String name) {
		String capitalizedName = StringUtils.capitalize(name);
		add2(STR."result.\{name}List(parse\{capitalizedName}List());");
	}

	public void buildMethodParseMultiple(String rule) {
		String type = StringUtils.capitalize(rule);
		StringBuilder sb = new StringBuilder();
		add1(STR."public static List<\{type}> parse\{type}List() {", sb);
			add2(STR."List<\{type}> list = new ArrayList<>();", sb);
			add2("while (true) {", sb);
				add3(STR."\{type} o = parse\{type}();", sb);
				add3("if (o == null)", sb);
					add4("break;", sb);
				add3("list.add(o);", sb);
			add2("}", sb);
			add2("return list", sb);
		add1("}", sb);
		addEmptyLine(sb);
		parseMultipleMethods.put(rule, sb.toString());
	}

	@Override
	public String toString() {
		parseMultipleMethods.values().forEach(method -> sb.append(method));
		buildFileFooter();
		return sb.toString();
	}

}
