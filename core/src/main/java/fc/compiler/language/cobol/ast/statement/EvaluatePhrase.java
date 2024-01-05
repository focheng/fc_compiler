package fc.compiler.language.cobol.ast.statement;

import fc.compiler.common.ast.Expression;
import fc.compiler.common.ast.ExpressionBase;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author FC
 */
@Getter @Setter @Accessors(fluent = true) @ToString
public class EvaluatePhrase extends ExpressionBase {
	boolean isAny;
	boolean isTrue;
	boolean isFalse;
	boolean isNot;
	Expression condition;
	Expression value;
	Expression through;
}