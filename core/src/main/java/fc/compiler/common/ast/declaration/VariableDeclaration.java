package fc.compiler.common.ast.declaration;

import fc.compiler.common.ast.Expression;
import fc.compiler.common.ast.StatementBase;
import fc.compiler.common.ast.expression.Identifier;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author FC
 */
@Getter @Setter @Accessors(fluent = true)
public class VariableDeclaration extends StatementBase {
	// Modifiers modifiers;
	Expression type;
	Identifier name;
	Expression initializer;
	VariableDeclaration next;   // a link to next variable of same type. e.g. int i, j;
}
