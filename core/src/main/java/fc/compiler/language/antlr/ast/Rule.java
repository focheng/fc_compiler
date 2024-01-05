package fc.compiler.language.antlr.ast;

import fc.compiler.common.ast.Expression;
import fc.compiler.common.ast.StatementBase;
import fc.compiler.common.ast.expression.Identifier;
import fc.compiler.common.ast.statement.CompositeStatement;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author FC
 */
@Getter @Setter @Accessors(fluent = true) @ToString
public class Rule extends StatementBase {
	Identifier name;
	List<Expression> choices;
}
