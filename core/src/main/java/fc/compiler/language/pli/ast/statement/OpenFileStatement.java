package fc.compiler.language.pli.ast.statement;

import fc.compiler.common.ast.Expression;
import fc.compiler.common.ast.StatementBase;
import fc.compiler.common.ast.expression.Identifier;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * @author FC
 */
@Data @Accessors(fluent = true, chain = true)
public class OpenFileStatement extends StatementBase {
	protected Identifier name;
	protected List<String> options = new ArrayList<>();;
	protected Expression titleExpr;
	protected Expression lineSizeExpr;
	protected Expression pageSizeExpr;
}
