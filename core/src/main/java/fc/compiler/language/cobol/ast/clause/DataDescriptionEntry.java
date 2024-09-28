package fc.compiler.language.cobol.ast.clause;

import fc.compiler.common.ast.AstNode;
import fc.compiler.common.ast.StatementBase;
import fc.compiler.common.ast.expression.Identifier;
import fc.compiler.common.ast.expression.Literal;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author FC
 */
@Getter @Setter @Accessors(fluent = true) @ToString
public class DataDescriptionEntry extends StatementBase {
	Literal<String> levelNumber;
	Identifier dataName;
	DataPictureClause dataPictureClause;
	DataValueClause dataValueClause;
	AstNode dummy1;
	AstNode dummy2;
}