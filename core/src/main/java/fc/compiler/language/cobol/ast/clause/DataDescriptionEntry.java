package fc.compiler.language.cobol.ast.clause;

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
public class DataDescriptionEntry extends StatementBase {
	Identifier dataName;
	PictureClause pictureClause;
	ValueClause valueClause;
}