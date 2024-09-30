package fc.compiler.language.antlr;

import fc.compiler.common.lexer.LexerWithCodeReaderBaseTest;
import fc.compiler.common.token.StringToken;
import fc.compiler.language.antlr.modern.AntlrLexerWithCodeReader;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * @author FC
 */
class AntlrLexerTest extends LexerWithCodeReaderBaseTest {
	@Test
	void scan() {
		String code = "/* comment */ grammar Cobol85; startRule\n" +
				"    : compilationUnit EOF\n" +
				"    ;";
		List<StringToken> tokenList = codeToTokens(code, new AntlrLexerWithCodeReader());
		tokenList.forEach(System.out::println);
	}

}