package fc.compiler.language.cobol;

import fc.compiler.common.lexer.IdentifierLexerWithCodeReader;
import fc.compiler.common.token.StringToken;
import fc.compiler.common.lexer.LexerWithCodeReaderBaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author FC
 */
class CobolLexerWithCodeReaderTest extends LexerWithCodeReaderBaseTest {
	@Test
	void scan() {
		String code = "  identification division.";
		List<StringToken> tokenList = codeToTokens(code, new CobolLexerWithCodeReader());
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
					codeToToken(code, CobolLexerWithCodeReader::onDigit).toString());
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

		IdentifierLexerWithCodeReader idLexer = new IdentifierLexerWithCodeReader();
		idLexer.isIdentifierStart(CobolLexerWithCodeReader::isLetterOrDigit);
		idLexer.isIdentifierPart(CobolLexerWithCodeReader::isIdentifierPart);
		for (String code : cases.keySet()) {
			Assertions.assertEquals(cases.get(code), codeToToken(code, CobolLexerWithCodeReader::scanIdentifier).toString());
		}
	}

	@Test
	void stringLiteral() {
		Assertions.assertEquals("Token(STRING_LITERAL, ''single quoted string'', (1, 1))",
				codeToToken("'single quoted string'", CobolLexerWithCodeReader::onSingleQuote).toString());
		Assertions.assertEquals("Token(STRING_LITERAL, '\"double quoted string\"', (1, 1))",
				codeToToken("\"double quoted string\"", CobolLexerWithCodeReader::scanStringLiteral).toString());
	}

	@Test
	void numberLiteral() {
	}
}