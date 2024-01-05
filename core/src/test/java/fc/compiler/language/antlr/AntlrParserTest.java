package fc.compiler.language.antlr;

import fc.compiler.common.ast.AstNode;
import fc.compiler.common.lexer.CodeReader;
import fc.compiler.common.parser.ParserHub;
import fc.compiler.common.parser.TokenReader;
import fc.compiler.language.antlr.ast.AntlrCompilationUnit;
import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * @author FC
 */
public class AntlrParserTest {
	protected AstNode codeToAst(String code, ParserHub parser) {
		CodeReader reader = new CodeReader(code.toCharArray());
		reader.onStartToken();
		TokenReader tokenReader = new TokenReader(new AntlrLexer(), reader);
		return parser.parse(tokenReader, AntlrParser.initRegistry());
	}

	@Test
	void parseFile() throws IOException {
	}

	@Test
	void parseCompilationUnit() {
		String code = "/* comment */ grammar Cobol85; " +
				"compilationUnit : programUnit+ ;\n" +
				"programUnit : identificationDivision environmentDivision? dataDivision? procedureDivision? programUnit* endProgramStatement?  ;\n" +
				"identificationDivision : (IDENTIFICATION | ID) DIVISION DOT_FS programIdParagraph identificationDivisionBody* ;\n" +
				"inputOutputSectionParagraph : fileControlParagraph | ioControlParagraph ;"
				;
		doParseCompilationUnit(code);
	}

	private static void doParseCompilationUnit(String code) {
		TokenReader tokenReader = new TokenReader(new AntlrLexer(), new CodeReader(code.toCharArray()));
		AntlrCompilationUnit unit = AntlrParser.parseCompilationUnit(tokenReader, AntlrParser.initRegistry());
		System.out.println(unit);
	}
}
