package fc.compiler.language.cobol.ast.statement;

import fc.compiler.common.ast.Expression;
import fc.compiler.common.ast.expression.Assignment;
import fc.compiler.common.ast.expression.CompositeExpression;
import fc.compiler.common.ast.statement.ExpressionStatement;
import fc.compiler.language.cobol.ast.expression.RoundedIdentifier;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author FC
 */
@Getter @Setter @Accessors(fluent = true) @ToString
public class SetStatement extends ExpressionStatement {
	Assignment expression;
	boolean up = false;
	boolean down = false;
	boolean on = false;
	boolean off = false;
	boolean isTrue = false;
	boolean addressOf = false;
}