package fc.compiler.language.cobol;

import fc.compiler.common.ast.AstNode;
import fc.compiler.common.ast.CompositeStatement;
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
	void parseDataDescriptionEntry() {

	}
}
