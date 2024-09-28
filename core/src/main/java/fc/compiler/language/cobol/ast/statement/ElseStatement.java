package fc.compiler.language.cobol.ast.statement;

import fc.compiler.common.ast.Statement;
import fc.compiler.common.ast.statement.CompositeStatement;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author FC
 */
@Getter @Setter @Accessors(fluent = true) @ToString
public class ElseStatement extends CompositeStatement<Statement> {
	// next sentence
}