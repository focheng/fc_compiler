package fc.compiler.language.cobol.ast.division;

import fc.compiler.common.ast.Statement;
import fc.compiler.common.ast.expression.Identifier;
import fc.compiler.common.ast.statement.CompositeStatement;
import fc.compiler.language.cobol.ast.clause.ProcedureUsingClause;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author FC
 */
@Getter @Setter @Accessors(fluent = true) @ToString(callSuper = true)
public class ProcedureDivision extends CompositeStatement<Statement> {
	ProcedureUsingClause procedureUsingClause;
	Identifier procedureGivingClause;
	Identifier procedureDeclaratives;
	Identifier procedureDivisionBody;
}