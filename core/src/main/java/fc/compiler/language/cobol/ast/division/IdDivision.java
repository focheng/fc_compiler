package fc.compiler.language.cobol.ast.division;

import fc.compiler.common.ast.StatementBase;
import fc.compiler.common.ast.expression.Identifier;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author FC
 */
@Getter @Setter @Accessors(fluent = true) @ToString
public class IdDivision extends StatementBase {
    Identifier programName;
    String author;
    String installation;
    String dateWritten;
    String dateCompiled;
    String security;
}