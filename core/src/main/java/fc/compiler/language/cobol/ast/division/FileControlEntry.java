package fc.compiler.language.cobol.ast.division;

import fc.compiler.common.ast.Expression;
import fc.compiler.common.ast.Statement;
import fc.compiler.common.ast.StatementBase;
import fc.compiler.common.ast.expression.Identifier;
import fc.compiler.common.ast.statement.CompositeStatement;
import fc.compiler.language.cobol.ast.clause.AssignClause;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author FC
 */
@Getter @Setter @Accessors(fluent = true) @ToString
public class FileControlEntry extends StatementBase {
	Identifier fileName;
	AssignClause assignClause2;
	Expression assignClause;
	Expression passwordClause;
	Expression relativeKeyClause;
	CompositeStatement<Statement> statementList;
}