package fc.compiler.language.antlr.traditional;

import fc.compiler.language.antlr.ast.AntlrCompilationUnit;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author FC
 */
class FcgJavaCodeGeneratorTest {

	@Test
	void testGenerateJavaCode() {
		String code = STR."parser grammar Cobol85;\n" +
				"cobolCompilationUnit : cobolProgram+;\n" +
				"cobolProgram: idDivision endProgram?;\n" +
				"idDivision: (IDENTIFICATION | ID) DIVISION '.' PROGRAM_ID '.' programName '.';\n" +
				"endProgram: END PROGRAM programName '.';\n";
		generateJavaCode(code);
	}

	private static void generateJavaCode(String code) {
		FcgLexer lexer = new FcgLexer(code);
		FcgParser parser = new FcgParser(lexer);
		AntlrCompilationUnit cu = parser.parseCompilationUnit();
		FcgJavaCodeGenerator generator = new FcgJavaCodeGenerator();
		generator.visit(cu);
		System.out.println(generator.toString());
	}
}