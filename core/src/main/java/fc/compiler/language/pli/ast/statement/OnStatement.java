package fc.compiler.language.pli.ast.statement;

import fc.compiler.common.ast.Expression;
import fc.compiler.common.ast.Statement;
import fc.compiler.common.ast.StatementBase;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * The DISPLAY statement displays a message on the user’s screen
 * and optionally requests the user to enter a response to the message.
 * @author FC
 */
@Data @Accessors(fluent = true, chain = true)
public class OnStatement extends StatementBase {
	protected String condition;
	protected Statement action;
}
