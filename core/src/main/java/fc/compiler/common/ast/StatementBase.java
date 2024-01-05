package fc.compiler.common.ast;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.swing.plaf.nimbus.State;

/**
 * @author FC
 */
@Getter @Setter @Accessors(fluent= true)
public class StatementBase implements Statement {
	Statement parent;       // assigned and used in ast visiting.

	@Override
	public <R, P> R accept(StatementVisitor visitor, P p) {
		visitor.visitStatement(this, p);
		return null;
	}
}
