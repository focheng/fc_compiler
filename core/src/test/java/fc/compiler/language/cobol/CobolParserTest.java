package fc.compiler.language.cobol;

import fc.compiler.common.ast.AstNode;
import fc.compiler.common.ast.statement.CompositeStatement;
import fc.compiler.common.lexer.CodeReader;
import fc.compiler.common.parser.Parser;
import fc.compiler.common.parser.ParserBase;
import fc.compiler.common.parser.TokenReader;
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
				+ " 01 INPUT-NUM PIC S9(4) VALUE ZERO.\n"
				+ " PROCEDURE DIVISION.\n"
				+ "     DISPLAY \"hello, COBOL!\".\n"
				;
		TokenReader tokenReader = new TokenReader(new CobolLexer(), new CodeReader(code.toCharArray()));
		CompositeStatement cs = CobolParser.parseCompilationUnit(tokenReader, CobolParser.initRegistry());
		System.out.println(cs);
	}

	@Test
	void parseDataDescriptionEntry() {

	}
}
