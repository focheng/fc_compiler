package fc.compiler.language.antlr.traditional;

import fc.compiler.language.antlr.ast.Rule;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;

import static fc.compiler.language.antlr.traditional.UniqueTokenKindFinder.*;

/**
 * @author FC
 */
public class ParserBuilder extends ClassBuilderBase {
	public static final String ACTION_SET       = "set";
	public static final String ACTION_ASSIGN    = "assign";
	public static final String ACTION_RETURN    = "return";

	private Map<String, String> parseMultipleMethods = new LinkedHashMap<>();

	public String toCode() {
		parseMultipleMethods.values().forEach(method -> sb.append(method));
		addParseIdentifierMethod();
		return sb.toString();
	}

	public void buildFileHeader() {
		add(STR."package \{options.packageName()};");
		addEmptyLine();
		add(STR."import TODO;");
		addEmptyLine();
		add(STR."public class \{options.lang()}Parser {");
	}

	public void buildFileFooter() {
		add("}");
	}

	/**
	 * build empty parse method for empty rule.
	 * e.g. <code>"emptyRule: ;"</code>
	 * -> <code>"public ReturnType parseReturnType() { return null; }"</code>
	 * @param rule
	 */
	public void buildEmptyParseMethod(Rule rule) {
		String ruleName = rule.name().id();
		String returnType = StringUtils.capitalize(ruleName);
		add1(STR."public \{returnType} parse\{returnType}() {");
		add2("return null;");
		add1("}");
	}

	public void buildParseMethodStart(Rule rule, String uniqueKinds) {
		buildParseMethodStart(rule, uniqueKinds, true);
	}
	public void buildParseMethodStart(Rule rule, String uniqueKinds, boolean newResult) {
		String ruleName = rule.name().id();
		String returnType = StringUtils.capitalize(ruleName);
		add1(STR."public \{returnType} parse\{returnType}() {");
		if (!StringUtils.isBlank(uniqueKinds)) {
			add2(STR."if (!token.kind.isAnyOf(\{uniqueKinds})) return null;");
			addEmptyLine();
		}
		if (newResult) {
			add2(STR."\{returnType} result = new \{returnType}();");
		}
	}

	public void buildParseMethodEnd(boolean returnResult) {
		if (returnResult)
			add2("return result;");
		add1("}");
		addEmptyLine();
	}

	/**
	 * build a parseType statement.
	 * e.g. <code>result.xxx(parseXXX());</code>
	 */
	public void parseType(String ruleName, String action) {
		String capitalizedName = StringUtils.capitalize(ruleName);
		switch (action) {
			case ACTION_SET:     add2(STR."result.\{ruleName}(parse\{capitalizedName}());");         break;
			case ACTION_ASSIGN:  add2(STR."\{capitalizedName} result = parse\{capitalizedName}();"); break;
			case ACTION_RETURN:  add2(STR."return parse\{capitalizedName}();");                      break;
		}
	}

	/**
	 * A specialized version of building parseIdentifier() statement.
	 */
	public void parseIdentifier(String variable, String action) {
		switch (action) {
			case "set":     add2(STR."result.\{variable}(parseIdentifier());");  break;
			case "assign":  add2("Identifier result = parseIdentifier();"); break;
		}
	}

	/**
	 * A specialized version of building parseXxxList() statement.
	 * e.g. <code>result.xxxList(parseXxxList());</code>
	 */
	public void parseTypeList(String ruleName, String action) {
		String capitalizedName = StringUtils.capitalize(ruleName);
		switch (action) {
			case ACTION_SET:     add2(STR."result.\{ruleName}List(parse\{capitalizedName}List());");         break;
			case ACTION_ASSIGN:  add2(STR."List<\{capitalizedName}> result = parse\{capitalizedName}List();"); break;
			case ACTION_RETURN:  add2(STR."return parse\{capitalizedName}List();");                      break;
		}
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

	public void syntaxError(String hint) {
		add2(STR."syntaxError(\"\{hint}\")");
	}

	public void addParseListMethod(String rule) {
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

	private void addParseIdentifierMethod() {
		add1("protected Identifier parseIdentifier() {");
			add2("String lexeme = token.lexeme();");
			add2("acceptToken(IDENTIFIER);");
			add2("return Identifier.of(lexeme);");
		add1("}");
	}

	public void startSwitchExpression() {
		add2("switch (token.kind()) {");
		increaseIndent();
	}

	public void endSwitchExpression() {
		decreaseIndent();
		add2("}");
	}

	public void startSwitchCaseExpression(FirstTokenKinds kinds) {
		for (KindLinkNode kind : kinds.links) {
			add2(STR."case \{kind.name()}: ");
		}
	}

	public void startSwitchCaseBreak() {
		add3(STR."break;");
	}

	/**
	 * <code>return null; </code>
	 * <code>return new EmptyStatement(); </code>
	 * <code>return parseEmptyStatement(); </code>
	 */
	public void buildParseEmptyStatement() {
		add2("return null;");
	}
}
