package fc.compiler.common.ast.statement;

import fc.compiler.common.ast.Expression;
import fc.compiler.common.ast.Statement;
import fc.compiler.common.ast.StatementBase;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author FC
 */
@Getter @Setter @Accessors(chain = true)
public class IfStatement extends StatementBase {
	Expression condition;
	Statement thenStatement;
	Statement elseStatement;
	Statement elseIfStatements;

	public IfStatement(Expression expr, Statement thenStatement, Statement elseStatement) {
	}
}
