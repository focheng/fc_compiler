package fc.compiler.language.antlr.modern;

import fc.compiler.common.token.StringTokenKind;
import fc.compiler.language.antlr.ast.*;
import fc.compiler.language.cobol.CobolTokenKind;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author FC
 */
public class AntlrVisitorBase<P> implements AntlrVisitor<P> {
	Map<String, Rule> rules = new LinkedHashMap<>();    // name to rule
	/**
	 * Map token literals like {@code 'while'} to its token type. It may be that
	 * {@code WHILE="while"=35}, in which case both token name and string literal
	 * will have entries both mapped to 35.
	 *  antlr token type  | token name | token literal
	 *  ------------------|------------|----------------
	 *      35            |   WHILE    |   'while'
	 *      41            |   EQUAL    |     '='
	 * .
	 * Refer to below fields in antlr runtime Grammar.java.
	 *  tokenNameToTypeMap
	 *  stringLiteralToTypeMap
	 *  typeToStringLiteralList
	 *  typeToTokenList
	 */
	Map<String, String> mapStringLiteral2TokenKind = new HashMap<>();
//	Map<String, String> mapTokenName2Kind = new HashMap<>();

	public AntlrVisitorBase() {
		initMapStringLiteral2TokenKind();
	}

	private void initMapStringLiteral2TokenKind() {
		try {
			for (Field field : StringTokenKind.class.getDeclaredFields()) {
				if (field.getType().equals(String.class)) {
					String keyword = (String) field.get(null);
					mapStringLiteral2TokenKind.put(keyword, field.getName());
				}
			}
			for (Field field : CobolTokenKind.class.getDeclaredFields()) {
				if (field.getType().equals(String.class)) {
					String keyword = (String) field.get(null);
					mapStringLiteral2TokenKind.put(keyword, field.getName());
				}
			}
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Void visit(AntlrCompilationUnit node, P p) {
		for (Rule rule : node.statementList()) {
			rules.put(rule.name().id(), rule);
		}
		for (Rule rule : node.statementList()) {
			visit(rule, p);
		}

		return null;
	}
}
