package fc.compiler.language.cobol.ast;

import fc.compiler.common.ast.CompilationUnit;
import fc.compiler.language.cobol.ast.division.DataDivision;
import fc.compiler.language.cobol.ast.division.EnvironmentDivision;
import fc.compiler.language.cobol.ast.division.IdDivision;
import fc.compiler.language.cobol.ast.division.ProcedureDivision;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author FC
 */
@Getter @Setter @Accessors(fluent = true) @ToString
public class CobolCompilationUnit extends CompilationUnit {
	List<CobolProgram> programs;
}
