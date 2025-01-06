package fc.compiler.language.pli;

import fc.compiler.common.ast.AstNode;
import fc.compiler.common.ast.CompilationUnit;
import fc.compiler.common.ast.Expression;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author FC
 */
class PliParserTest {

	@Test
	void parseCompilationUnit() throws IOException {
		String filePath = "d:\\cosmos\\code\\fc_code\\java\\fc_compiler\\grammars\\pli\\sample.pli";
		List<String> lines = Files.readAllLines(Path.of(filePath));
		lines = standardize(lines);
		PliLexer lexer = new PliLexer(String.join("\n", lines));
		PliParser parser = new PliParser(lexer);
		CompilationUnit cu = parser.parseCompilationUnit();
		System.out.println(cu);
	}

	public static List<String> standardize(List<String> lines) {
		List<String> newLines = new ArrayList<>();
		for (int i = 0; i < lines.size(); i++) {
			String line = lines.get(i);
			if (line != null && !line.isEmpty()) {
				warnIfExceeds80(line, i);
				line = trimTrailingNumber(line);
				line = cleanCommentLine(line);
			}
			newLines.add(line);
		}
		return newLines;
	}

	private static String trimTrailingNumber(String line) {
		if (line.length() >= 73)
			line = line.substring(0, 72);
		return line;
	}

	private static String cleanCommentLine(String line) {
		if (line.charAt(0) == '*')
			return "";
		return line;
	}

	private static String warnIfExceeds80(String line, int lineNo) {
		if (line.length() > 80)
			System.out.println("line " + lineNo + " exceeds 80: " + line);
		return line;
	}

	@Test
	void testBinaryExpression() {
		PliLexer lexer = new PliLexer("A + B ** 2 < C & D");
		PliParser parser = new PliParser(lexer);
		Expression expr = parser.parseBinaryExpression();
		System.out.println(expr);
	}
}