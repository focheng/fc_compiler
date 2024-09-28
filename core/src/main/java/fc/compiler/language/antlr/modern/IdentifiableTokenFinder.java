package fc.compiler.language.antlr.modern;

import fc.compiler.common.ast.Expression;
import fc.compiler.common.ast.expression.Identifier;
import fc.compiler.common.ast.expression.Literal;
import fc.compiler.common.util.StrUtils;
import fc.compiler.language.antlr.ast.*;

import java.util.*;

import static org.apache.commons.lang3.StringUtils.isAllUpperCase;

/**
 * @author FC
 */
public class IdentifiableTokenFinder
		extends AntlrVisitorBase<List<IdentifiableToken>> {
	Map<String, List<IdentifiableToken>> rule2IdentifiableTokenList = new LinkedHashMap<>();

	public Void visit(Identifier id, List<IdentifiableToken> tokenList) {
		if (isAllUpperCase(id.id())) {
			tokenList.add(new IdentifiableToken(id.id()));
		} else if (rules.containsKey(id.id())) {
			visit(rules.get(id.id()), tokenList); // search recursively into the referenced rule
		} else {
		}
		return null;
	}

	public Void visit(Literal literal, List<IdentifiableToken> tokenList) {
		String key = String.valueOf(literal.value());
		if (mapStringLiteral2TokenKind.containsKey(key)) {
			tokenList.add(new IdentifiableToken(mapStringLiteral2TokenKind.get(key), false));
		}
		return null;
	}

	public Void visit(Rule rule, List<IdentifiableToken> parentTokenList) {
		String ruleName = rule.name().id();
		List<IdentifiableToken> tokenList = rule2IdentifiableTokenList.get(ruleName);
		if (tokenList == null) {
			tokenList = new ArrayList<>();
			rule2IdentifiableTokenList.put(ruleName, tokenList);
			visit(rule.expression(), tokenList);
		}

		// also added to parent rule.
		if (parentTokenList != null)
			parentTokenList.addAll(tokenList);

		return null;
	}

	// search the 1st mandatory identifiable token (identifier or literal),
	// and collect identifiable token list.
	// if the 1st mandatory sub-node is a rule or composite node, search recursively.
	public Void visit(Sequence node, List<IdentifiableToken> tokenList) {
		for (Expression expr : node.children()) {
			visit(expr, tokenList);
			if (hasNonOptional(tokenList)) {
				break;
			}
		}
		return null;
	}

	public Void visit(Alternatives node, List<IdentifiableToken> tokenList) {
		tokenList.addAll(getIdentifiableTokenList(node.children()));
		return null;
	}

	public List<IdentifiableToken> getIdentifiableTokenList(List<Expression> children) {
		List<List<IdentifiableToken>> listOfList = new ArrayList<>();

		for (Expression expr: children) {
			List<IdentifiableToken> tokenListAlt = new ArrayList<>();
			visit(expr, tokenListAlt);
			if (tokenListAlt.isEmpty()) {
				System.out.println("no identifiable token kind is found for " + expr);
			}
			listOfList.add(tokenListAlt);
		}

		return mergeTokenLists(listOfList);
	}

	public Void visit(QuantifiedExpression node, List<IdentifiableToken> tokenList) {
		List<IdentifiableToken> subTokenList = new ArrayList<>();
		visit(node.expression(), subTokenList);
		switch (node.quantifierType()) {
			case "?":
			case "*":
				downgradeToOptional(subTokenList);
				break;
			case "+":
				break;
		}
		tokenList.addAll(subTokenList);

		return null;
	}

	// -- helpers --

	public boolean hasNonOptional(List<IdentifiableToken> tokenList) {
		for (IdentifiableToken it : tokenList) {
			if (!it.optional()) {
				return true;
			}
		}
		return false;
	}

	public List<IdentifiableToken> mergeTokenLists(List<List<IdentifiableToken>> listOfList) {
		List<IdentifiableToken> mergedTokenList = new ArrayList<>();

		OptionalInt minSize = listOfList.stream().mapToInt(l -> l.size()).min();
//		int minSize = Integer.MAX_VALUE;
//		for (List<IdentifiableToken> identifiableTokens : listOfList) {
//			if (minSize > identifiableTokens.size())
//				minSize = identifiableTokens.size();
//		}

		// compare one by one in multiple list of token kinds.
		// if all tokens at i are totally different, then found.
		boolean found = false;
		int numOfAlternatives = listOfList.size();
		for (int i = 0; i < minSize.getAsInt(); i++) {
			Set<String> set = distinctTokensAt(listOfList, i);
			mergedTokenList.add(new IdentifiableToken(null, false, set));
			if (set.size() == numOfAlternatives) {
				found = true;
				break;  // all tokens are totally different in all alternative branches.
			}
		}

		if (!found) {
			System.out.println("Failed to find distinct token kind for all alternative branches " + listOfList);
//			throw new RuntimeException("Failed to find distinct token kind for all alternative branches");
		}
		return mergedTokenList;
	}

	public Set<String> distinctTokensAt(List<List<IdentifiableToken>> listOfList, int i) {
		Set<String> set = new HashSet<>();
		for (List<IdentifiableToken> tokenList : listOfList) {
			set.add(tokenList.get(i).tokenKind());
		}
		return set;
	}

	public void downgradeToOptional(List<IdentifiableToken> subTokenList) {
		subTokenList.forEach(it -> it.optional = true);
	}
}
