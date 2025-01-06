package fc.compiler.language.antlr.traditional;

import fc.compiler.common.ast.AstNode;
import fc.compiler.common.ast.AstNodeVisitor;
import fc.compiler.common.ast.Expression;
import fc.compiler.common.ast.expression.Identifier;
import fc.compiler.common.ast.expression.Literal;
import fc.compiler.common.ast.expression.ParenthesizedExpression;
import fc.compiler.common.ast.expression.StringLiteral;
import fc.compiler.language.antlr.ast.*;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import static fc.compiler.common.util.StrUtils.isAllUpperCase;
import static fc.compiler.language.antlr.traditional.ParserBuilder.*;
import static fc.compiler.language.antlr.traditional.UniqueTokenKindFinder.*;

/**
 * @author FC
 */
@Setter @Accessors(fluent = true, chain = true)
public class FcgJavaCodeGenerator implements AstNodeVisitor<Void, AstNode, Object> {
	// -- options --
	private CodeGeneratorOptions options;

	// -- specific builders --
	private LexerBuilder lexerBuilder = new LexerBuilder();
	private ParserBuilder parserBuilder = new ParserBuilder();
	private TokenKindBuilder tokenKindBuilder = new TokenKindBuilder().lexerBuilder(lexerBuilder);
	private AstNodeClassBuilder astClassBuilder = new AstNodeClassBuilder();
	// VisitorBuilder vb;
	private UniqueTokenKindFinder finder = new UniqueTokenKindFinder();

	// -- --
	private Map<String, Rule> rules = new HashMap<>();
	private Set<String> undefinedRuleNames = new HashSet<>();

	@Override
	public Void visit(AstNode node2, Object o) {
		switch (node2) {
			case Identifier             node:   visit(node, ACTION_SET);   break;
			case StringLiteral          node:   visit(node, ACTION_SET);   break;
			case Expression             node:   visit(node, ACTION_SET);   break;
			case Rule                   node:   visit(node, ACTION_SET);   break;
			case AntlrCompilationUnit   node:   visit(node, ACTION_SET);   break;
			default:    break;
		}
		return null;
	}

	public void visit(AntlrCompilationUnit cu) {
		if (StringUtils.isEmpty(options.lang()))
			options.lang(cu.name().id());
		lexerBuilder.options(options);
		parserBuilder.options(options);
		tokenKindBuilder.options(options);
		astClassBuilder.options(options);

		cu.rules().forEach(rule -> rules.put(rule.name().id(), rule));
		finder.tokenKindBuilder(tokenKindBuilder).rules(rules);
		cu.rules().forEach(rule -> visit(rule, ACTION_SET));
	}

	public void visit(Rule rule, String action) {
		String ruleName = rule.name().id();
		if (isAllUpperCase(ruleName)) {
			visitLexerRule(rule, ruleName);
		} else {
			visitParserRule(rule, action, ruleName);
		}
	}

	private void visitParserRule(Rule rule, String action, String ruleName) {
		if (rule.expression() == null) {
			parserBuilder.buildEmptyParseMethod(rule);
		} else if (isIdentifierAliasRule(rule)) {
			// do nothing. no parse method and ast type definition for identifier alias.
			// e.g. <code>programName : IDENTIFIER ;</code>
		} else if ("statement".equalsIgnoreCase(ruleName)) {
			statementBranches(rule);
		//} else if ("expression".equalsIgnoreCase(ruleName)) {
			// TODO
		} else {
			FirstTokenKinds kinds = finder.getUniqueTokenKinds(ruleName);
			parserBuilder.buildParseMethodStart(rule, kinds.toString());
			visit(rule.expression(), action);
			parserBuilder.buildParseMethodEnd(true);

			buildAstType(rule, ruleName);
		}
	}

	private void buildAstType(Rule rule, String ruleName) {
		String typeName = StringUtils.capitalize(ruleName);
		astClassBuilder.startType(typeName);

		Expression ruleExpr = rule.expression();
		boolean multiple = false;
		if (ruleExpr instanceof QuantifiedExpression qe) {
			ruleExpr = qe.expression();
			multiple = qe.isMultiple();
		}

		switch (ruleExpr) {
			case Identifier id:     addFieldToAstType(id, null, multiple);      break;
			case Sequence seq:      buildAstTypeSequence(ruleName, seq, multiple);  break;
			case Alternatives alt:  // do nothing, just generate a base class.
				break;
			default:
				System.out.println("TODO: buildAstType for " + ruleName);
		}
		astClassBuilder.endType(typeName);

//		if (rule.expression() instanceof Sequence seq) {
//			Map<String, String> map = new LinkedHashMap<>();    // variable -> type
//			for (Expression child : seq.children()) {
//				boolean multiple = false;
//				if (child instanceof QuantifiedExpression qe) {
//					child = qe.expression();
//					multiple = qe.isMultiple();
//				}
//				switch (child) {
//					case Identifier id:     addFieldToAstType(id, map, multiple); break;
//					case Alternatives alt:  map.put("alt", "AstNode");  break;
//					default:
//						System.out.println("TODO: buildAstType for " + ruleName);
//				}
//			}
//			astClassBuilder.addType(ruleName, map);
//		} else if (rule.expression() instanceof Alternatives alt) {
//			astClassBuilder.addBaseClass(ruleName);
//		}
	}

	private void buildAstTypeSequence(String ruleName, Sequence seq, boolean multiple) {
		for (Expression child : seq.children()) {
			boolean multipleChild = multiple;
			if (child instanceof QuantifiedExpression qe) {
				child = qe.expression();
				multiple = qe.isMultiple();
			}

			switch (child) {
				case Identifier id:     addFieldToAstType(id, null, multipleChild); break;
				case Alternatives alt:  astClassBuilder.addField("alt", "AstNode");  break;
				case StringLiteral literal:   break;
				default:
					System.out.println("TODO: buildAstType sequence " + ruleName);
			}
		}
	}

	private void visitLexerRule(Rule rule, String ruleName) {
		// simple TokenKind definition. e.g. THEN : 'THEN'; DOT: '.' ;
		if (rule.expression() instanceof StringLiteral literal) {
			tokenKindBuilder.add(ruleName, literal.value());
		} else if ("IDENTIFIER".equals(ruleName)) {
			System.out.println("TODO: translate to scanIdentifier()");
		} else if (ruleName.endsWith("LITERAL")) {
			System.out.println("TODO: translate to scan"+ ruleName +"()");
		}
		if (rule.fragment()) {
			// TODO:
		}
	}

	private void addFieldToAstType(Identifier id, Map<String, String> map, boolean multiple) {
		String variableName = null;
		String typeName = null;
		if ("IDENTIFIER".equals(id.id())) {
			variableName = "id";
			typeName = "Identifier";
		} else if ("LITERAL".equals(id.id())) {
			variableName = "value";
			typeName = "Literal";
		} else if (isAllUpperCase(id.id())) {
		} else {
			variableName = id.id();
			typeName = StringUtils.capitalize(id.id());
			if (rules.containsKey(id.id())) {
				if (isIdentifierAliasRule(rules.get(variableName))) {
					typeName = "Identifier";
				}
			} else {
				if (!undefinedRuleNames.contains(id.id())) {
					System.out.println("undefined rule name: " + id.id());
					undefinedRuleNames.add(id.id());
				}
			}
		}

		if (variableName != null) {
			if (multiple) {
				variableName = variableName + "List";
				typeName = "List<" + typeName + ">";
			}
			//map.put(variableName, typeName);
			astClassBuilder.addField(variableName, typeName);
		}
	}

	public void visit(Expression expr, String action) {
		switch (expr) {
			case Identifier                 node:   visit(node, action);    break;
			case StringLiteral              node:   visit(node, action);    break;
			case Alternatives               node:   visit(node, action);    break;
			case Sequence                   node:   visit(node, action);    break;
			case ParenthesizedExpression    node:   visit(node, action);    break;
			case QuantifiedExpression       node:   visit(node, action);    break;
			default:    break;
		}
	}

	/**
	 * cases:
	 * - only keywords/literal. idDivision : (IDENTIFICATION | ID) DIVISION '.'
	 *                          => acceptAnyToken(IDENTIFICATION, ID);
	 * - nested rules:          statement: ifStatement | forStatement | returnStatement
	 * - keywords & multiple:   thenStatement : NEXT SENTENCE | statement*
	 " - statement:             statement : moveStatement | exitStatement | setStatement ;\n" +
	 */
	public void visit(Alternatives alternatives, String action) {
		if (isAllTokens(alternatives)) {
			parserBuilder.buildAcceptAnyToken(getAllTokens(alternatives));
		} else {
			parserBuilder.startSwitchExpression();
			for (Expression alternative : alternatives.children()) {
				FirstTokenKinds cases = finder.firstTokenKind(alternative);
				parserBuilder.startSwitchCaseExpression(cases);
				parserBuilder.increaseIndent();
				visit(alternative, action);
				if (!ACTION_RETURN.equals(action))
					parserBuilder.add2("break;");
				parserBuilder.decreaseIndent();
			}
			parserBuilder.endSwitchExpression();
		}
	}

	public void visit(Sequence sequence, String action) {
		sequence.children().forEach(expr -> visit(expr, action));
	}

	public void visit(ParenthesizedExpression pe, String action) {
		visit(pe.expression(), action);
	}

	public void visit(QuantifiedExpression qe, String action) {
		if (qe.expression() instanceof Identifier id) {
			visit(id, action, Quantifier.of(qe.quantifierType()));
		} else {
			System.out.println("TODO: visit QuantifiedExpression" + qe.expression());
		}
	}

	public void visit(Identifier node, String action) {
		visit(node, action, Quantifier.EXACTLY_ONE);
	}

	public void visit(Identifier node, String action, Quantifier quantifier) {
		String id = node.id();
		// refer to a rule.
		if (rules.containsKey(id)) {
			if (isIdentifierAliasRule(rules.get(id))) {
				tokenKindBuilder.add(id, id);
				parserBuilder.parseIdentifier(id, action);
			} else {
				if (quantifier.isMultiple()) {
					parserBuilder.parseTypeList(id, action);
					parserBuilder.addParseListMethod(id);
				} else {
					parserBuilder.parseType(id, action);
				}
			}
		}
		// not explicitly defined in grammar
		// default: id with all uppercase letter is lexer rule.
		else if (isLexerRule(id)) {
			tokenKindBuilder.add(id, id);
			if (quantifier.isOptional()) {
				parserBuilder.buildOptionalToken(id);
			} else {
				parserBuilder.buildAcceptToken(id);
			}
		} else {
			parserBuilder.parseIdentifier(id, action);
		}
	}

	public void visit(StringLiteral node, String action) {
		visit(node, action, Quantifier.EXACTLY_ONE);
	}

	public void visit(StringLiteral node, String action, Quantifier quantifier) {
		String kind = tokenKindBuilder.getTokenKind(node.value());
		if (kind != null) {
			if (quantifier.isOptional()) {
				parserBuilder.buildOptionalToken(kind);
			} else {
				parserBuilder.buildAcceptToken(kind);
			}
		} else {
			parserBuilder.buildParseStringLiteral();
		}
	}

	public void statementBranches(Rule rule) {
		String ruleName = rule.name().id();
		FirstTokenKinds kinds = finder.getUniqueTokenKinds(ruleName);
		parserBuilder.buildParseMethodStart(rule, kinds.toString(), false);

		parserBuilder.startSwitchExpression();
		for (Expression alternative : ((Alternatives)rule.expression()).children()) {
			FirstTokenKinds cases = finder.firstTokenKind(alternative);
			parserBuilder.startSwitchCaseExpression(cases);
			parserBuilder.increaseIndent();
			visit(alternative, ACTION_RETURN);
			parserBuilder.decreaseIndent();
		}
		parserBuilder.endSwitchExpression();
		parserBuilder.buildParseEmptyStatement();
		parserBuilder.buildParseMethodEnd(false);
	}

	/**
	 * "COMMA", "PROGRAM_ID". except "IDENTIFIER"
	 */
	protected boolean isLexerRule(String ruleName) {
		return isAllUpperCase(ruleName) && !"IDENTIFIER".equals(ruleName);
	}

	/**
	 * Rule define a meaningful IDENTIFIER alias.
	 * e.g. <code>programName : IDENTIFIER ;</code>
	 */
	private static boolean isIdentifierAliasRule(Rule rule) {
		return rule.expression() instanceof Identifier;
	}

	private static boolean isLiteralAliasRule(Rule rule) {
		return rule.expression() instanceof Literal;
	}

	private boolean isAstTypeId(Identifier id) {
		return rules.containsKey(id.id()) && !isAllUpperCase(id.id());
	}

	private boolean isAllTokens(Alternatives alternatives) {
		return alternatives.children().stream().allMatch(expr ->
				expr instanceof Identifier id && !rules.containsKey(id.id())
						|| (expr instanceof StringLiteral sl && tokenKindBuilder.getTokenKind(sl.value()) != null)
		);
	}

	private String getAllTokens(Alternatives alternatives) {
		return alternatives.children().stream().map(expr -> {
			if (expr instanceof Identifier id && !rules.containsKey(id.id())) {
				return id.id();
			} else if (expr instanceof StringLiteral stringLiteral) {
				String tokenKind = tokenKindBuilder.getTokenKind(stringLiteral.value());
				if (tokenKind != null)
					return tokenKind;
			}
			return "";
		}).collect(Collectors.joining(", "));
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(astClassBuilder.toCode());
		sb.append(lexerBuilder.toCode());
		sb.append(tokenKindBuilder.toCode());
		sb.append(parserBuilder.toCode());
		return sb.toString();
	}

	public void write(String srcPath) throws IOException {
		String templateFolder = "src/main/java/fc/compiler/language/antlr/traditional/";
		String packageFolder = String.join("/", options.packageName().split("\\."));
		String folder = srcPath + "/" + packageFolder + "/";
//		Files.writeString(Path.of(folder + options.lang() + "Parser.java"), parserBuilder.toCode());

		String template = Files.readString(Path.of(templateFolder + "/FcgLexer.java_template"));
		template = template.replace("${lang}", options.lang());
		template = template.replace("${packageName}", options.packageName());
		template = template.replace("${scanMethods}", lexerBuilder.toCode());
		Files.writeString(Path.of(folder + options.lang() + "Lexer.java"), template);

		template = Files.readString(Path.of(templateFolder + "/FcgTokenKind.java_template"));
		template = template.replace("${lang}", options.lang());
		template = template.replace("${packageName}", options.packageName());
		template = template.replace("${body}", tokenKindBuilder.toCode());
		Files.writeString(Path.of(folder + options.lang() + "TokenKind.java"), template);

		template = Files.readString(Path.of(templateFolder + "/FcgTokenKindTag.java_template"));
		template = template.replace("${lang}", options.lang());
		template = template.replace("${packageName}", options.packageName());
		Files.writeString(Path.of(folder + options.lang() + "TokenKindTag.java"), template);

		template = Files.readString(Path.of(templateFolder + "/FcgToken.java_template"));
		template = template.replace("${lang}", options.lang());
		template = template.replace("${packageName}", options.packageName());
		Files.writeString(Path.of(folder + options.lang() + "Token.java"), template);

		template = Files.readString(Path.of(templateFolder + "/FcgParser.java_template"));
		template = template.replace("${lang}", options.lang());
		template = template.replace("${packageName}", options.packageName());
		template = template.replace("${parseMethods}", parserBuilder.toCode());
		Files.writeString(Path.of(folder + options.lang() + "Parser.java"), template);

		for (Map.Entry<String, String> entry : astClassBuilder.types().entrySet()) {
			String typeName = entry.getKey();
			String code = entry.getValue();
			code = STR."import \{options.packageName()}.ast;\n\n" + code;
			Files.writeString(Path.of(folder + "/ast/" + typeName + ".java"), code);
		}
	}
}
