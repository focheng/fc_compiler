package fc.compiler.language.pli.ast.statement;

import fc.compiler.common.ast.Expression;
import fc.compiler.common.ast.Statement;
import fc.compiler.common.ast.StatementBase;
import fc.compiler.common.ast.expression.Identifier;
import fc.compiler.common.ast.statement.CompositeStatement;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author FC
 */
@Data @Accessors(fluent = true, chain = true)
public class PliDoStatement extends CompositeStatement<Statement> {
	protected Identifier reference;
	protected Expression initialExpr;
	protected Expression toExpr;
	protected Expression byExpr;
	protected Expression whileExpr;
	protected Expression untilExpr;
}
