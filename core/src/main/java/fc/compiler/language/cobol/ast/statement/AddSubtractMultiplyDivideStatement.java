package fc.compiler.language.cobol.ast.statement;

import fc.compiler.common.ast.Expression;
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
public class AddSubtractMultiplyDivideStatement extends ExpressionStatement {
	Expression leftOperand;
	String operator;
	Expression rightOperand;
	CompositeExpression<RoundedIdentifier> givingIdentifiers;
	boolean corresponding = false;
	boolean giving = false;
}