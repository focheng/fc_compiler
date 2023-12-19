package fc.compiler.language.cobol.ast.statement;

import fc.compiler.common.ast.StatementBase;
import fc.compiler.language.cobol.ast.CharacterString;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author FC
 */
@Getter @Setter @Accessors(fluent = true) @ToString
public class ValueClause extends StatementBase {
	String value;
}