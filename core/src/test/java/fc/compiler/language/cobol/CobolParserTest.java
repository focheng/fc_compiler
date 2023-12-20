package fc.compiler.language.cobol;

import fc.compiler.common.ast.AstNode;
import fc.compiler.common.ast.CompositeStatement;
import fc.compiler.common.ast.statement.IfStatement;
import fc.compiler.common.lexer.CodeReader;
import fc.compiler.common.parser.Parser;
import fc.compiler.common.parser.TokenReader;
import fc.compiler.language.cobol.ast.CobolProgram;
import org.junit.jupiter.api.Test;

/**
 * @author FC
 */
public class CobolParserTest {
	protected AstNode codeToAst(String code, Parser parser) {
		CodeReader reader = new CodeReader(code.toCharArray());
		reader.sp = reader.bp;
		reader.newPosition();
		TokenReader tokenReader = new TokenReader(new CobolLexer(), reader);
//		CobolParser mainParser = new CobolParser();
		return parser.parse(tokenReader, CobolParser.initRegistry());
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
		TokenReader tokenReader = new TokenReader(new CobolLexer(), new CodeReader(code.toCharArray()));
		CobolProgram program = CobolParser.parseCompilationUnit(tokenReader, CobolParser.initRegistry());
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
	void parseDataDescriptionEntry() {

	}
}
