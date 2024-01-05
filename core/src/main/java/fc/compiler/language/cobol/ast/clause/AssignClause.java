package fc.compiler.language.cobol.ast.clause;

import fc.compiler.common.ast.Expression;
import fc.compiler.common.ast.StatementBase;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author FC
 */
@Getter @Setter @Accessors(fluent = true) @ToString
public class AssignClause extends StatementBase {
	Expression assignee;
}