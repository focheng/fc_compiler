package fc.compiler.language.antlr.traditional;

import fc.compiler.language.antlr.ast.AntlrCompilationUnit;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author FC
 */
public class FcgJavaCodeGeneratorTest {
	public static final String CODE_CU = STR."parser grammar Cobol85;\n" +
			"program : idDivision environmentDivision? dataDivision? procedureDivision? program* endProgramStatement?  ;\n" +
			"endProgramStatement: END PROGRAM programName '.' ;\n" +
			"idDivision: (IDENTIFICATION | ID) DIVISION '.' PROGRAM_ID '.' programName '.';\n" +
			"environmentDivision : ENVIRONMENT DIVISION '.'  ;\n" +
			"dataDivision : DATA DIVISION '.' ;\n" +
			"procedureDivision : PROCEDURE DIVISION '.' procedureDivisionBody;\n" +
			"procedureDivisionBody : paragraph* ;\n" +
			"paragraph : paragraphName '.' sentence* ;\n" +
			"sentence : statement* '.' ;\n" +
			"statement : moveStatement | exitStatement | setStatement ;\n" +
			"moveStatement : MOVE (IDENTIFIER | LITERAL) TO IDENTIFIER+ ;\n" +
			"exitStatement : EXIT PROGRAM?  ;\n" +
			"setStatement : SET IDENTIFIER TO LITERAL ;\n" +
			"programName : IDENTIFIER ;\n" +
			"paragraphName : IDENTIFIER ;\n"
			;

	@Test
	void testGenerateJavaCode() {
		generateJavaCode(CODE_CU);
	}

	@Test
	void generateCobolParser() throws IOException {
		String srcPath = "d:\\cosmos\\code\\fc_code\\java\\fc_compiler\\auto_generated\\src\\main\\java\\";
		String grammarFile = "d:\\cosmos\\code\\fc_code\\java\\fc_compiler\\grammars\\cobol85\\cobol85_fc.g4";
		String antlrCode = Files.readString(Paths.get(grammarFile));

		FcgLexer lexer = new FcgLexer(antlrCode);
		FcgParser parser = new FcgParser(lexer);
		AntlrCompilationUnit cu = parser.parseCompilationUnit();

		CodeGeneratorOptions options = new CodeGeneratorOptions().lang("Cobol").packageName("fc.compiler.language.cobol");
		FcgJavaCodeGenerator generator = new FcgJavaCodeGenerator().options(options);
		generator.visit(cu);
		generator.write(srcPath);
//		System.out.println(generator.toString());
	}

	private static void generateJavaCode(String code) {
		FcgLexer lexer = new FcgLexer(code);
		FcgParser parser = new FcgParser(lexer);
		AntlrCompilationUnit cu = parser.parseCompilationUnit();
		CodeGeneratorOptions options = new CodeGeneratorOptions().lang("Abc").packageName("foo");
		FcgJavaCodeGenerator generator = new FcgJavaCodeGenerator().options(options);
		generator.visit(cu);
		System.out.println(generator.toString());
	}
}