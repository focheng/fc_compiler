package fc.compiler.language.cobol.ast.clause;

import fc.compiler.common.ast.Statement;
import fc.compiler.common.ast.StatementBase;
import fc.compiler.common.ast.expression.Identifier;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author FC
 */
@Getter @Setter @Accessors(fluent = true) @ToString
public class FileDescriptionEntry extends StatementBase {
	boolean isSort;       // SD if true, otherwise FD
	Identifier fileName;
	Statement clause;
}