package fc.compiler.language.cobol.ast.expression;

import fc.compiler.common.ast.ExpressionBase;
import fc.compiler.common.ast.expression.Identifier;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

/**
 * @author FC
 */
@Getter @Setter @Accessors(fluent = true) @ToString
public class RoundedIdentifier extends Identifier {
	boolean rounded;
}
