package fc.compiler.common.ast.declaration;

import fc.compiler.common.ast.Expression;
import fc.compiler.common.ast.Statement;
import fc.compiler.common.ast.StatementBase;
import fc.compiler.common.ast.expression.Identifier;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author FC
 */
@Getter @Setter @Accessors(fluent = true)
public class FunctionDeclaration extends StatementBase {
	// Modifiers modifiers;
	Expression returnType;
	Identifier name;
	List<VariableDeclaration> parameters;
	Statement statement;
}
