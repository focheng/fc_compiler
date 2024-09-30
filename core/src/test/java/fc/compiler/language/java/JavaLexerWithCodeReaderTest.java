package fc.compiler.language.java;

import fc.compiler.common.token.StringToken;
import fc.compiler.common.lexer.LexerWithCodeReaderBaseTest;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * @author FC
 */
class JavaLexerWithCodeReaderTest extends LexerWithCodeReaderBaseTest {
	@Test
	void scan() {
		String code = "int add() { return (1 + 2) * 3; }";
		List<StringToken> tokenList = codeToTokens(code, new JavaLexerWithCodeReader());
		tokenList.forEach(System.out::println);
	}

}