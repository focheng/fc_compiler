package fc.compiler.language.antlr.traditional;

import fc.compiler.common.ast.Expression;
import fc.compiler.common.ast.expression.Identifier;
import fc.compiler.common.ast.expression.StringLiteral;
import fc.compiler.language.antlr.ast.Alternatives;
import fc.compiler.language.antlr.ast.Quantifier;
import fc.compiler.language.antlr.ast.Sequence;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.*;
import java.util.stream.Collectors;

/**
 * statement:
 *     ifStatement
 *   | whileStatement
 *   |  // empty statement
 *   ;
 * ifStatement: IF condition THEN? thenStatement (ELSE elseStatement)? END_IF?
 * thenStatement : NEXT SENTENCE | statement* ;
 * elseStatement : NEXT SENTENCE | statement* ;
 *
 * jclStatement: execStatement | moveStatement | nullStatement;
 * execStatement: name? EXEC (parameter comment?)?
 * moveStatement: name? MOVE (parameter comment?)?
 * nullStatement: ;
 *
 * @author FC
 */
@Accessors(fluent = true, chain = true)
public class UniqueTokenKindFinder extends FcgVisitor<UniqueTokenKindFinder.FirstTokenKinds> {
	// sequence of first token kinds -> rule
	// rule -> list of sequence of first token kinds
	private final Map<String, FirstTokenKinds> rule2UniqueTokenKinds = new HashMap<>();
	@Getter @Setter private TokenKindBuilder tokenKindBuilder;

	/**
	 * <kind1, quantifier, next> & <kind2, quantifier, next> & <kind3, quantifier, next>
	 */
	@Getter @Setter @Accessors(fluent = true, chain = true)
	@NoArgsConstructor
	@EqualsAndHashCode
	public static class KindLinkNode {
		@EqualsAndHashCode.Include
		private String name;
		private Quantifier quantifier = Quantifier.EXACTLY_ONE;
		private KindLinkNode next;

		public KindLinkNode(String name) {
			this.name = name;
		}
	}

	public static class FirstTokenKinds {
		List<KindLinkNode> links = new ArrayList<>();

		public void add(String kind) {
			if (kind != null)
				links.add(new KindLinkNode(kind));
		}

		public void addAll(FirstTokenKinds another) {
			if (another != null)
				links.addAll(another.links);
		}

		@Override
		public String toString() {
			return links.stream().map(link -> link.name).collect(Collectors.joining(", "));
		}
	}

	public FirstTokenKinds getUniqueTokenKinds(String ruleName) {
		if (!rule2UniqueTokenKinds.containsKey(ruleName)) {
			FirstTokenKinds uniqueTokenKinds = new FirstTokenKinds();
			visit(rules.get(ruleName), uniqueTokenKinds);
			rule2UniqueTokenKinds.put(ruleName, uniqueTokenKinds);
		}

		return rule2UniqueTokenKinds.get(ruleName);
	}

	public FirstTokenKinds firstTokenKind(Expression expression) {
		FirstTokenKinds kinds = new FirstTokenKinds();
		visit(expression, kinds);
		return kinds;
	}


//	public void visit(Rule rule, FirstTokenKinds nil) {
//		String ruleName = rule.name().id();
//		if (!rule2UniqueTokenKinds.containsKey(ruleName)) {
//			FirstTokenKinds uniqueTokenKinds = new FirstTokenKinds();
//			visit(rule.expression(), uniqueTokenKinds);
//			rule2UniqueTokenKinds.put(ruleName, uniqueTokenKinds);
//		}
//	}

	@Override
	public void visit(Alternatives alternatives, FirstTokenKinds kinds) {
		alternatives.children().forEach(expr -> visit(expr, kinds));
	}

	@Override
	public void visit(Sequence sequence, FirstTokenKinds kinds) {
		KindLinkNode link = new KindLinkNode();
		for (Expression expr : sequence.children()) {
			visit(expr, kinds);
			if (hasMandatory(link)) {
				break;
			}
		}
	}

	@Override
	public void visit(Identifier node, FirstTokenKinds kinds,
	                  Quantifier quantifier, boolean returnIdentifier) {
		if (rules.containsKey(node.id())) {
			kinds.addAll(getUniqueTokenKinds(node.id()));
		} else if (isLexerRule(node.id())) {
			kinds.add(node.id());
		} else {
			System.out.println("Unsupported first token kind: " + node.id());
		}
	}

	@Override
	public void visit(StringLiteral node, FirstTokenKinds kinds,
	                  Quantifier quantifier) {
		kinds.add(tokenKindBuilder.getTokenKind(node.value()));
	}

	private boolean hasMandatory(KindLinkNode link) {
		do {
			if (!link.quantifier.isOptional())
				return true;
			link = link.next;
		} while (link != null);

		return false;
	}


}
