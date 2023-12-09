package fc.compiler.language.cobol;

import fc.compiler.common.lexer.CodeReader;
import fc.compiler.common.lexer.IdentifierLexer;
import fc.compiler.common.lexer.LexerBase;
import fc.compiler.common.token.Token;
import fc.compiler.common.token.TokenKind;
import fc.compiler.language.java.JavaLexer;
import fc.compiler.lexer.LexerBaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author FC
 */
class CobolLexerTest extends LexerBaseTest {
	@Test
	void scan() {
		String code = "  identification division.";
		List<Token> tokenList = codeToTokens(code, new CobolLexer());
		tokenList.forEach(System.out::println);
	}

	@Test
	void onDigit() {
		Map<String, String> cases = new HashMap<>();
		cases.put("01",     "Token(NUMBER_LITERAL, '01', (1, 1))");
		cases.put("01.23",  "Token(NUMBER_LITERAL, '01', (1, 1))");
		cases.put("01a",    "Token(IDENTIFIER, '01', (1, 1))");
		for (String code : cases.keySet()) {
			Assertions.assertEquals(cases.get(code),
					codeToToken(code, CobolLexer::onDigit).toString());
		}
	}

	@Test
	void identifier() {
		Map<String, String> cases = new HashMap<>();
		cases.put("a0Z",     "Token(IDENTIFIER, 'a0Z', (1, 1))");
		cases.put("identification division",  "Token(IDENTIFIER, 'identification', (1, 1))");
		cases.put("ws-version.",    "Token(IDENTIFIER, 'ws-version', (1, 1))");
		cases.put("-v.",    "Token(ERROR, '', (1, 1))");
		cases.put("v-.",    "Token(ERROR, 'v-', (1, 1))");

		IdentifierLexer idLexer = new IdentifierLexer();
		idLexer.setIsIdentifierStart(CobolLexer::isLetterOrDigit);
		idLexer.setIsIdentifierPart(CobolLexer::isIdentifierPart);
		for (String code : cases.keySet()) {
			Assertions.assertEquals(cases.get(code), codeToToken(code, CobolLexer::scanIdentifier).toString());
		}
	}

	@Test
	void stringLiteral() {
		Assertions.assertEquals("Token(STRING_LITERAL, ''single quoted string'', (1, 1))",
				codeToToken("'single quoted string'", CobolLexer::scanSingleQuote).toString());
		Assertions.assertEquals("Token(STRING_LITERAL, '\"double quoted string\"', (1, 1))",
				codeToToken("\"double quoted string\"", CobolLexer::scanDoubleQuote).toString());
	}

	@Test
	void numberLiteral() {
	}
}