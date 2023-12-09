package fc.compiler.common.ast.statement;

import fc.compiler.common.ast.Expression;
import fc.compiler.common.ast.Statement;
import fc.compiler.common.ast.StatementBase;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * @author FC
 */
@Getter @Setter @Accessors(chain = true)
public class SwitchCaseStatement<T extends Statement> extends StatementBase {
	Expression expression;
	List<T> statements = new ArrayList<>();
}
