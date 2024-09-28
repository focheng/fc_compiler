package fc.compiler.language.antlr.modern;

import fc.compiler.common.ast.expression.Identifier;
import fc.compiler.common.ast.expression.Literal;
import fc.compiler.language.antlr.ast.QuantifiedExpression;
import fc.compiler.language.antlr.ast.Rule;

import java.util.*;

import static org.apache.commons.lang3.StringUtils.isAllUpperCase;

/**
 * @author FC
 */
public class RuleFeatureAnalyzer
		extends AntlrVisitorBase<RuleFeatureAnalyzer.RuleFeature> {

	public static class RuleFeature {
//		List<IdentifiableToken> identifiableTokenList;

		int keywordCount = 0;   // number of token kind that is reserved keyword in uppercase.
		int literalCount = 0;   // number of token kind that is string literal.
		int subRuleCount = 0;   // number of token kind that is defined rule name
		int undefinedCount = 0; // number of token kind that is not defined.
		int quantifiedCount = 0;// number of token kind that is quantified with '*' or '+'.
		int keywordCountDescendants = 0;   
		int literalCountDescendants = 0;   
		int subRuleCountDescendants = 0;   
		int undefinedCountDescendants = 0; 
		int quantifiedCountDescendants = 0;

		public boolean hasMultipleToReturn() {
			return subRuleCount + undefinedCount + quantifiedCount
					+ subRuleCountDescendants + undefinedCountDescendants + quantifiedCountDescendants
					> 1;
		}
	}

	Map<String, RuleFeature> mapRule2Feature = new LinkedHashMap<>();

	public Void visit(Rule rule, RuleFeature parentFeature) {
		String ruleName = rule.name().id();
		RuleFeature feature = mapRule2Feature.get(ruleName);
		if (feature == null) {
			feature = new RuleFeature();
			mapRule2Feature.put(ruleName, feature);
			visit(rule.expression(), feature);
		}

		// also added to parent rule.
		if (parentFeature != null) {
			parentFeature.keywordCountDescendants += feature.keywordCount;
			parentFeature.literalCountDescendants += feature.literalCount;
			parentFeature.subRuleCountDescendants += feature.subRuleCount;
			parentFeature.undefinedCountDescendants += feature.undefinedCount;
			parentFeature.quantifiedCountDescendants += feature.quantifiedCount;
		}

		return null;
	}


	public Void visit(Identifier id, RuleFeature feature) {
		if (isAllUpperCase(id.id())) {
			feature.keywordCount++;
		} else if (rules.containsKey(id.id())) {
			feature.subRuleCount++;
			visit(rules.get(id.id()), feature); // process recursively into the referenced rule
		} else {
			feature.undefinedCount++;
		}
		return null;
	}

	public Void visit(Literal literal, RuleFeature feature) {
		feature.literalCount++;
		return null;
	}

	public Void visit(QuantifiedExpression node, RuleFeature feature) {
		switch (node.quantifierType()) {
			case "?":   visit(node.expression(), feature);    break;
			case "*":
			case "+":
				feature.quantifiedCount++;
				visit(node.expression(), feature);
				break;
		}
		return null;
	}
}
