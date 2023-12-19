package fc.compiler.common.ast.statement;

import fc.compiler.common.ast.Statement;
import fc.compiler.common.ast.StatementBase;
import fc.compiler.common.ast.expression.Identifier;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author FC
 */
@Getter @Setter @Accessors(fluent= true)
public class LabeledStatement extends StatementBase {
	Identifier label;
	Statement statement;
}
