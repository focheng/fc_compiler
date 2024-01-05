package fc.compiler.language.antlr.ast;

import fc.compiler.common.ast.Statement;
import fc.compiler.common.ast.expression.Identifier;
import fc.compiler.common.ast.statement.CompositeStatement;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author FC
 */
@Getter @Setter @Accessors(fluent = true) @ToString(callSuper = true)
public class AntlrCompilationUnit extends CompositeStatement<Rule> {
	boolean isLexer = false;
	Identifier name;
}
