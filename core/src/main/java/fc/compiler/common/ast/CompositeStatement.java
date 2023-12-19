package fc.compiler.common.ast;

import fc.compiler.common.ast.Statement;
import fc.compiler.common.ast.StatementBase;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * @author FC
 */
@Getter @Setter @Accessors(fluent= true) @ToString
public class CompositeStatement<T extends Statement> extends StatementBase {
	List<T> statements = new ArrayList<>();
}
