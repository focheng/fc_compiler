package fc.compiler.common.ast.statement;

import fc.compiler.common.ast.Statement;
import fc.compiler.common.ast.StatementBase;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * Composite statement represents statement list.
 * @author FC
 */
@Data
@Accessors(fluent= true)
public class CompositeStatement<T extends Statement> extends StatementBase {
	List<T> statementList = new ArrayList<>();

	public void add(T statement) {
		statementList.add(statement);
	}

	@Override
	public String toString() {
		if (statementList.isEmpty())
			return "CompositeStatement[]";

		StringBuilder sb = new StringBuilder();
		sb.append("CompositeStatement[").append(System.lineSeparator());
		for (int i = 0; i < statementList.size(); i++) {
			if (i > 0) {
				sb.append(", ").append(System.lineSeparator());
			}
			sb.append(statementList.get(i));
		}
		sb.append("]").append(System.lineSeparator());
		return sb.toString();
	}
}
