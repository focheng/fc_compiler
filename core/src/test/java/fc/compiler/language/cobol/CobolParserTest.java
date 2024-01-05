package fc.compiler.language.cobol;

import fc.compiler.common.ast.AstNode;
import fc.compiler.common.ast.Statement;
import fc.compiler.common.ast.statement.IfStatement;
import fc.compiler.common.lexer.CodeReader;
import fc.compiler.common.parser.ParserHub;
import fc.compiler.common.parser.TokenReader;
import fc.compiler.language.cobol.ast.CobolCompilationUnit;
import fc.compiler.language.cobol.ast.CobolProgram;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author FC
 */
public class CobolParserTest {
	protected AstNode codeToAst(String code, ParserHub parser) {
		CodeReader reader = new CodeReader(code.toCharArray());
		reader.onStartToken();
		TokenReader tokenReader = new TokenReader(new CobolLexer(), reader);
//		CobolParser mainParser = new CobolParser();
		return parser.parse(tokenReader, CobolParser.initRegistry());
	}

	@Test
	void parseFile() throws IOException {
		String file = "d:\\cosmos\\code\\open_source\\compiler\\proleap-cobol-parser\\src\\test\\resources\\gov\\nist\\CM101M.CBL";
		List<String> lines = new Preprocessor().proprocess(Files.readAllLines(Paths.get(file)));
		String code = lines.stream().collect(Collectors.joining("\n"));

		doParseCompilationUnit(code);
	}

	@Test
	void parseCompilationUnit() {
		String code = " IDENTIFICATION DIVISION.\n"
				+ " PROGRAM-ID. SAMPLE.\n"
				+ " DATA DIVISION.\n"
				+ " WORKING-STORAGE SECTION.\n"
				+ " 01 INPUT-NUM     PIC S9(4) VALUE ZERO.\n"
				+ " 01 BIRTH-DAY.\n"
				+ "     05 YYYY      PIC 9(4).\n"
				+ "     05 MM        PIC 99.\n"
				+ " PROCEDURE DIVISION.\n"
				+ "     DISPLAY \"hello, COBOL!\".\n"
				;
		doParseCompilationUnit(code);
	}

	private static void doParseCompilationUnit(String code) {
		TokenReader tokenReader = new TokenReader(new CobolLexer(), new CodeReader(code.toCharArray()));
		CobolCompilationUnit unit = CobolParser.parseCompilationUnit(tokenReader, CobolParser.initRegistry());
		CobolProgram program = unit.programs().get(0);
		System.out.println(program.idDivision());
		System.out.println(program.environmentDivision());
		System.out.println(program.dataDivision());
		System.out.println(program.procedureDivision());
	}

	@Test
	void parseIfStatement() {
		String codeSimpleIf = " IF WS-GENDER EQUAL 'M'\n" +
					  "     DISPLAY \"Person is Male\"\n" +
					  " END-IF.";
		String codeIfElse = " IF WS-GENDER EQUAL 'M'\n" +
				"    DISPLAY \"Person is Male\"\n" +
				" ELSE \n" +
				"    DISPLAY \"person is Female\"\n" +
				" END-IF.";
		String codeNestedIf = "IF WS-MARKS-PERCENT > 60\n" +
				"  DISPLAY 'GOT FIRST CLASS'\n" +
				"ELSE\n" +
				"  IF WS-MARKS-PERCENT > 50\n" +
				"         DISPLAY 'GOT SECOND CLASS'\n" +
				"  ELSE \n" +
				"         DISPLAY 'GOT THIRD CLASS'\n" +
				"  END-IF\n" +
				"END-IF.\n";
		TokenReader tokenReader = new TokenReader(new CobolLexer(), new CodeReader(codeSimpleIf.toCharArray()));
		IfStatement statement = CobolParser.parseIfStatement(tokenReader, CobolParser.initRegistry());
		System.out.println(statement);
	}

	@Test
	void parseEvaluateStatement() {
		String code = " EVALUATE WEEK-DAY\n" +
				"\t     WHEN 01\n" +
				"\t\t      DISPLAY \"TODAY IS SUNDAY\"\n" +
				"\t     WHEN 07\n" +
				"\t\t\t  DISPLAY \"TODAY IS SATURDAY\"\n" +
				"\t     WHEN OTHER\n" +
				"\t\t\t  DISPLAY \"INVALID INPUT\"\n" +
				"     END-EVALUATE.";
		String codeMultiWhens = " EVALUATE STD-GRADE\n" +
				" WHEN \"A\" WHEN \"B\" DISPLAY 'Student got FIRST CLASS'\n" +
				" WHEN \"C\" DISPLAY 'Student got SECOND CLASS'\n" +
				" WHEN OTHER DISPLAY 'Student Failed'\n" +
				" END-EVALUATE.\n";
		TokenReader tokenReader = new TokenReader(new CobolLexer(), new CodeReader(codeMultiWhens.toCharArray()));
		Statement statement = CobolParser.parseEvaluateStatement(tokenReader, CobolParser.initRegistry());
		System.out.println(statement);
	}

	@Test
	void parseDataDescriptionEntry() {

	}
}
