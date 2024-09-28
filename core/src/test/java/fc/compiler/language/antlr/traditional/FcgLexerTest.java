package fc.compiler.language.antlr.traditional;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static fc.compiler.language.antlr.traditional.FcgTokenKind.EOF;

/**
 * @author FC
 */
class FcgLexerTest {

	@Test
	void scanToken() {
		printTokens("/* block \ncomment 1 */\n" +
				"grammar langABC; // line comment 2\n" +
				"COMMA: ','");
	}

	static void printTokens(String code) {
		code2Tokens(code).stream().limit(100).forEach(token ->
				System.out.println(token.toString())
		);
	}
	static List<FcgToken> code2Tokens(String code) {
		FcgLexer lexer = new FcgLexer(code);
		List<FcgToken> tokens = new ArrayList<>();
		FcgToken token = null;
		do {
			token = lexer.scanToken();
			tokens.add(token);
		} while (token.kind() != EOF);
		return tokens;
	}
}