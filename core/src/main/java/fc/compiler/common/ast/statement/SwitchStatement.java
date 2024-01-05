package fc.compiler.common.ast.statement;

import fc.compiler.common.ast.Expression;
import fc.compiler.common.ast.StatementBase;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author FC
 */
@Getter @Setter @Accessors(fluent= true) @ToString
public class SwitchStatement<T extends SwitchCaseStatement> extends StatementBase {
	Expression expression;
	CompositeStatement<T> caseStatements = new CompositeStatement();
}
