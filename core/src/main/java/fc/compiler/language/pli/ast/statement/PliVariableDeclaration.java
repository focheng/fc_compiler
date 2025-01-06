package fc.compiler.language.pli.ast.statement;

import fc.compiler.common.ast.Expression;
import fc.compiler.common.ast.declaration.VariableDeclaration;
import fc.compiler.common.ast.expression.NumberLiteral;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author FC
 */
@Data @Accessors(fluent = true, chain = true) @ToString(callSuper = true)
public class PliVariableDeclaration extends VariableDeclaration {
    protected Expression length;
    protected String varying;
    protected NumberLiteral level;
    protected List<Expression> dimensions;
}
