package fc.compiler.language.antlr.traditional;

import fc.compiler.language.antlr.ast.AntlrCompilationUnit;
import org.junit.jupiter.api.Test;

import static fc.compiler.language.antlr.traditional.UniqueTokenKindFinder.*;

/**
 * @author FC
 */
class UniqueTokenKindFinderTest {

	@Test
	void test() {
		String code = "grammar Jcl;\n" +
				"jclStatement: execStatement | moveStatement | nullStatement;\n" +
				"execStatement: name? EXEC (parameter comment?)?;\n" +
				"moveStatement: name? MOVE (parameter comment?)?;\n" +
				"nullStatement: ;\n";
		UniqueTokenKindFinder finder = initFinder(code);
		UniqueTokenKinds kinds = finder.getUniqueTokenKinds("jclStatement");
		System.out.println("unique kinds: ");
		System.out.println(kinds);
	}

	private static UniqueTokenKindFinder initFinder(String code) {
		FcgLexer lexer = new FcgLexer(code);
		FcgParser parser = new FcgParser(lexer);
		AntlrCompilationUnit cu = parser.parseCompilationUnit();

		UniqueTokenKindFinder finder = new UniqueTokenKindFinder();
		finder.tokenKindBuilder(new TokenKindBuilder());
		finder.visit(cu, new UniqueTokenKinds());
		return finder;
	}
}