package fc.compiler.common.lexer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * The position in the source code.
 * @author FC
 */
@Getter @Setter @ToString @AllArgsConstructor
public class Position {
	private String fileName;
	private int line;   // line no
	private int column; // the start position in the line.

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("(").append(line).append(", ").append(column).append(")");
		if (fileName != null)
			sb.append("@").append(fileName);
		return sb.toString();
	}
}
