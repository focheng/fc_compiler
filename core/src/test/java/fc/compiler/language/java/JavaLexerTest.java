package fc.compiler.language.java;

import fc.compiler.common.token.Token;
import fc.compiler.common.lexer.LexerBaseTest;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * @author FC
 */
class JavaLexerTest extends LexerBaseTest {
	@Test
	void scan() {
		String code = "int add() { return (1 + 2) * 3; }";
		List<Token> tokenList = codeToTokens(code, new JavaLexer());
		tokenList.forEach(System.out::println);
	}

}