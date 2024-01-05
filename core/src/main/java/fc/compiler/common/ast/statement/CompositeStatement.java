package fc.compiler.common.ast.statement;

import fc.compiler.common.ast.Statement;
import fc.compiler.common.ast.StatementBase;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * Composite statement represents statement list.
 * @author FC
 */
@Getter @Setter @Accessors(fluent= true)
public class CompositeStatement<T extends Statement> extends StatementBase {
	List<T> statements = new ArrayList<>();

	public void add(T statement) {
		statements.add(statement);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[").append(System.lineSeparator());
		for (int i = 0; i < statements.size(); i++) {
			if (i > 0) {
				sb.append(", ").append(System.lineSeparator());
			}
			sb.append(statements.get(i).toString());
		}
		sb.append("]").append(System.lineSeparator());
		return sb.toString();
	}
}
