package fc.compiler.language.antlr;

import fc.compiler.common.ast.AstNode;
import fc.compiler.common.lexer.CodeReaderBase;
import fc.compiler.common.parser.StringTokenParserHub;
import fc.compiler.common.parser.StringTokenReader;
import fc.compiler.language.antlr.ast.AntlrCompilationUnit;
import fc.compiler.language.antlr.modern.AntlrLexerWithCodeReader;
import fc.compiler.language.antlr.modern.AntlrParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author FC
 */
public class AntlrParserTest {
	public static final String CODE_TYPICAL =
		"/* comment */ grammar Cobol85; " +
		"compilationUnit : program+ ;\n" +
		"program : identificationDivision environmentDivision? dataDivision? procedureDivision? program* endProgramStatement?  ;\n" +
		"identificationDivision : (IDENTIFICATION | ID) DIVISION DOT_FS programIdParagraph identificationDivisionBody* ;\n" +
		"inputOutputSectionParagraph : fileControlParagraph | ioControlParagraph ;"
		;
	public static final String CODE_SINGLE = "grammar Cobol85;\n" +
			"identificationDivision : (IDENTIFICATION | ID) DIVISION DOT_FS programIdParagraph identificationDivisionBody* ;";

	protected AstNode codeToAst(String code, StringTokenParserHub parser) {
		CodeReaderBase reader = new CodeReaderBase(code.toCharArray());
		reader.onStartToken();
		StringTokenReader tokenReader = new StringTokenReader(new AntlrLexerWithCodeReader(), reader);
		return parser.parse(tokenReader, AntlrParser.initRegistry());
	}

	@Test
	void parseFile() throws IOException {
	}

	@Test
	void parseCompilationUnit() {
		parse(CODE_SINGLE);
	}

	public static AntlrCompilationUnit parseFile(String file) throws IOException {
		List<String> lines = Files.readAllLines(Paths.get(file));
		String code = lines.stream().collect(Collectors.joining("\n"));
		return parse(code);
	}

	public static AntlrCompilationUnit parse(String code) {
		StringTokenReader tokenReader = new StringTokenReader(new AntlrLexerWithCodeReader(), new CodeReaderBase(code.toCharArray()));
		AntlrCompilationUnit unit = AntlrParser.parseCompilationUnit(tokenReader, AntlrParser.initRegistry());
		return unit;
	}
}
