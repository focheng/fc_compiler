package fc.compiler.language.cobol.ast;

import fc.compiler.common.ast.AstNode;
import fc.compiler.common.ast.AstNodeVisitor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author FC
 */
@Getter @Setter @Accessors(fluent = true) @ToString
public class CharacterString implements AstNode {
	private String format;

	@Override
	public <R, P> R accept(AstNodeVisitor visitor, P p) {
		return visitor.visitNode(this, p);
	}
}
