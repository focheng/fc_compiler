package fc.compiler.language.cobol.ast;

import fc.compiler.common.ast.CompilationUnit;
import fc.compiler.common.ast.StatementBase;
import fc.compiler.common.ast.expression.Identifier;
import fc.compiler.language.cobol.ast.division.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author FC
 */
@Getter @Setter @Accessors(fluent = true) @ToString
public class CobolProgram extends StatementBase {
	IdDivision idDivision;
	EnvironmentDivision environmentDivision;
	DataDivision dataDivision;
	ProcedureDivision procedureDivision;
	List<CobolProgram> cobolProgramList;   // nested programs
	Identifier endProgram;
}
