package fc.compiler.language.cobol.ast.division;

import fc.compiler.common.ast.Statement;
import fc.compiler.common.ast.CompositeStatement;
import fc.compiler.language.cobol.ast.clause.UsingClause;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author FC
 */
@Getter @Setter @Accessors(fluent = true) @ToString(callSuper = true)
public class ProcedureDivision extends CompositeStatement<Statement> {
	UsingClause usingClause;
}