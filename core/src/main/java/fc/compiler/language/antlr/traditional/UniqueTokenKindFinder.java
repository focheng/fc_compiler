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
public class UniqueTokenKindFinder extends FcgVisitor<UniqueTokenKindFinder.UniqueTokenKinds> {
	private final Map<String, UniqueTokenKinds> rule2UniqueTokenKinds = new HashMap<>();
	@Setter private TokenKindBuilder kb;

	/**
	 * <kind1, quantifier, next> & <kind2, quantifier, next> & <kind3, quantifier, next>
	 */
	@Getter @Setter @Accessors(fluent = true, chain = true)
	@NoArgsConstructor
	@EqualsAndHashCode
	public static class KindLinkNode {
		@EqualsAndHashCode.Include
		private String kind;
		private Quantifier quantifier;
		private KindLinkNode next;

		public KindLinkNode(String kind) {
			this.kind = kind;
		}
	}

	public static class UniqueTokenKinds {
		Set<KindLinkNode> links = new LinkedHashSet<>();

		public void add(String kind) {
			if (kind != null)
				links.add(new KindLinkNode(kind));
		}

		public void addAll(UniqueTokenKinds another) {
			if (another != null)
				links.addAll(another.links);
		}

		@Override
		public String toString() {
			return links.stream().map(link -> link.kind).collect(Collectors.joining(", "));
		}
	}

	public UniqueTokenKinds getUniqueTokenKinds(String ruleName) {
		if (!rule2UniqueTokenKinds.containsKey(ruleName)) {
			UniqueTokenKinds uniqueTokenKinds = new UniqueTokenKinds();
			visit(rules.get(ruleName), uniqueTokenKinds);
			rule2UniqueTokenKinds.put(ruleName, uniqueTokenKinds);
		}

		return rule2UniqueTokenKinds.get(ruleName);
	}

	@Override
	public void visit(Alternatives alternatives, UniqueTokenKinds kinds) {
		alternatives.children().forEach(expr -> visit(expr, kinds));
	}

	@Override
	public void visit(Sequence sequence, UniqueTokenKinds kinds) {
		KindLinkNode link = new KindLinkNode();
		for (Expression expr : sequence.children()) {
			visit(expr, kinds);
			if (hasMandatory(link)) {
				break;
			}
		}
	}

	@Override
	public void visit(Identifier node, UniqueTokenKinds kinds,
	                  Quantifier quantifier, boolean returnIdentifier) {
		if (rules.containsKey(node.id())) {
			kinds.addAll(getUniqueTokenKinds(node.id()));
		} else if (isLexerRule(node.id())) {
			kinds.add(node.id());
		}
	}

	@Override
	public void visit(StringLiteral node, UniqueTokenKinds kinds,
	                  Quantifier quantifier) {
		kinds.add(kb.getTokenKind(node.value()));
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
