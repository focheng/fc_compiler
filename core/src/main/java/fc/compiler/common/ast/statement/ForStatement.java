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
@Getter @Setter @Accessors(fluent= true)
public class ForStatement extends StatementBase {
	Statement initializer;
	Expression condition;
	Statement update;
	Statement statement;

}
