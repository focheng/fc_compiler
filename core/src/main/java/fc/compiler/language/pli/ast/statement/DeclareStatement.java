package fc.compiler.language.pli.ast.statement;

import fc.compiler.common.ast.StatementBase;
import fc.compiler.common.ast.declaration.VariableDeclaration;
import fc.compiler.common.ast.expression.Identifier;
import fc.compiler.common.ast.statement.CompositeStatement;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author FC
 */
@Data @Accessors(fluent = true, chain = true)
public class DeclareStatement extends CompositeStatement<VariableDeclaration> {
}
