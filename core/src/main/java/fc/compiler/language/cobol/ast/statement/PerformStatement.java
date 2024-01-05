package fc.compiler.language.cobol.ast.statement;

import fc.compiler.common.ast.Expression;
import fc.compiler.common.ast.Statement;
import fc.compiler.common.ast.expression.Assignment;
import fc.compiler.common.ast.expression.Identifier;
import fc.compiler.common.ast.statement.CompositeStatement;
import fc.compiler.common.ast.statement.ExpressionStatement;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author FC
 */
@Getter @Setter @Accessors(fluent = true) @ToString
public class PerformStatement extends ExpressionStatement {
	Identifier procedureName;
	Identifier throughProcedureName;
	Expression timesExpression;
	Identifier varyingIdentifier;
	Expression fromExpression;
	Expression byExpression;
	Expression untilExpression;
	CompositeStatement<Statement> statementList;
	boolean beforeTest;
}