package fc.compiler.common.ast.expression;

import fc.compiler.common.ast.ExpressionBase;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

/**
 * @author FC
 */
@Getter @Setter @Accessors(fluent = true) @ToString
public class Identifier extends ExpressionBase {
	String id;

	// Identifier pool avoid creating duplicate objects.
	public static Map<String, Identifier> pool = new HashMap<>();
	public static Identifier get(String id) {
		if (!pool.containsKey(id)) {
			pool.put(id, new Identifier().id(id));
		}
		return pool.get(id);
	}
}
