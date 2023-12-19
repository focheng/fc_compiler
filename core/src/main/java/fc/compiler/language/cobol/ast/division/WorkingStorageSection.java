package fc.compiler.language.cobol.ast.division;

import fc.compiler.common.ast.StatementBase;
import fc.compiler.language.cobol.ast.statement.DataDescriptionEntry;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * @author FC
 */
@Getter @Setter @Accessors(fluent = true) @ToString
public class WorkingStorageSection extends StatementBase {
	List<DataDescriptionEntry> variableDeclarations = new ArrayList<>();
}