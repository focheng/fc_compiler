package fc.compiler.language.cobol.ast.statement;

import fc.compiler.common.ast.Expression;
import fc.compiler.common.ast.statement.SwitchCaseStatement;
import fc.compiler.common.ast.statement.SwitchStatement;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author FC
 */
@Getter @Setter @Accessors(fluent = true) @ToString(callSuper = true)
public class EvaluateWhenStatement extends SwitchCaseStatement {
	Expression value;
	List<Expression> alsoSelects;
}