package fc.compiler.language.antlr;

import fc.compiler.common.lexer.LexerBaseTest;
import fc.compiler.common.token.Token;
import fc.compiler.language.java.JavaLexer;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * @author FC
 */
class AntlrLexerTest extends LexerBaseTest {
	@Test
	void scan() {
		String code = "/* comment */ grammar Cobol85; startRule\n" +
				"    : compilationUnit EOF\n" +
				"    ;";
		List<Token> tokenList = codeToTokens(code, new AntlrLexer());
		tokenList.forEach(System.out::println);
	}

}