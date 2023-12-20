package fc.compiler.common.ast.statement;

import fc.compiler.common.ast.Expression;
import fc.compiler.common.ast.Statement;
import fc.compiler.common.ast.StatementBase;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author FC
 */
@Getter @Setter @Accessors(fluent= true) @ToString
public class IfStatement extends StatementBase {
	Expression condition;
	Statement thenStatement;
	Statement elseStatement;
	Statement elseIfStatements;
}
