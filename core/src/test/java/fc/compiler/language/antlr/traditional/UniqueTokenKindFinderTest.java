package fc.compiler.language.antlr.traditional;

import fc.compiler.language.antlr.ast.AntlrCompilationUnit;
import org.junit.jupiter.api.Test;

import static fc.compiler.language.antlr.traditional.FcgJavaCodeGeneratorTest.CODE_CU;
import static fc.compiler.language.antlr.traditional.UniqueTokenKindFinder.*;

/**
 * @author FC
 */
class UniqueTokenKindFinderTest {

	@Test
	void testCobolProgram() {
		UniqueTokenKindFinder finder = initFinder(CODE_CU);
		FirstTokenKinds kinds = finder.getUniqueTokenKinds("cobolProgram");
		System.out.println("unique kinds: ");
		System.out.println(kinds);
	}

	@Test
	void test() {
		String code = "grammar Jcl;\n" +
				"jclStatement: execStatement | moveStatement | nullStatement;\n" +
				"execStatement: name? EXEC (parameter comment?)?;\n" +
				"moveStatement: name? MOVE (parameter comment?)?;\n" +
				"nullStatement: ;\n";
		UniqueTokenKindFinder finder = initFinder(code);
		FirstTokenKinds kinds = finder.getUniqueTokenKinds("jclStatement");
		System.out.println("unique kinds: ");
		System.out.println(kinds);
	}

	private static UniqueTokenKindFinder initFinder(String code) {
		FcgLexer lexer = new FcgLexer(code);
		FcgParser parser = new FcgParser(lexer);
		AntlrCompilationUnit cu = parser.parseCompilationUnit();

		UniqueTokenKindFinder finder = new UniqueTokenKindFinder();
		finder.tokenKindBuilder().options(new CodeGeneratorOptions().packageName("foo.fox").lang("Abc"));
		finder.visit(cu, new FirstTokenKinds());
		return finder;
	}
}