package fc.compiler.language.pli.ast.statement;

import fc.compiler.common.ast.Statement;
import fc.compiler.common.ast.expression.Identifier;
import fc.compiler.common.ast.statement.CompositeStatement;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author FC
 */
@Data @Accessors(fluent = true, chain = true)
public class PliPackage extends CompositeStatement<Statement> {
	protected Identifier name;
	protected List<Identifier> exports;
}
