package fc.compiler.language.cobol.ast.division;

import fc.compiler.common.ast.StatementBase;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author FC
 */
@Getter @Setter @Accessors(fluent = true) @ToString
public class FileControlParagraph extends StatementBase {
	List<FileControlEntry> fileControlEntries;
}