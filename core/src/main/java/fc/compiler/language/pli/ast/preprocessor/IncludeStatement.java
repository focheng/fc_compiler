package fc.compiler.language.pli.ast.preprocessor;

import fc.compiler.common.ast.StatementBase;
import fc.compiler.common.ast.expression.Identifier;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author FC
 */
@Data @Accessors(fluent = true, chain = true)
public class IncludeStatement extends StatementBase {
	protected String fileName;
	protected Identifier ddName;
	protected Identifier member;
}
