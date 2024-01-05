package fc.compiler.language.cobol.ast.division;

import fc.compiler.common.ast.Statement;
import fc.compiler.common.ast.StatementBase;
import fc.compiler.common.ast.expression.Identifier;
import fc.compiler.common.ast.statement.CompositeStatement;
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
	CompositeStatement<Statement> statementList;
}