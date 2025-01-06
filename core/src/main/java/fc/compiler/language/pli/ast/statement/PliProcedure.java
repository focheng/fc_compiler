package fc.compiler.language.pli.ast.statement;

import fc.compiler.common.ast.Statement;
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
@ToString(callSuper = true)
public class PliProcedure extends CompositeStatement<Statement> {
	protected Identifier name;
	protected boolean main;
}
