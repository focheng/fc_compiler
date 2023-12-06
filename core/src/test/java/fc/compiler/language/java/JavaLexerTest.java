package fc.compiler.language.java;

import fc.compiler.common.lexer.CodeReader;
import fc.compiler.common.lexer.LexerBase;
import fc.compiler.common.token.Token;
import fc.compiler.common.token.TokenKind;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author FC
 */
class JavaLexerTest {
	private List<Token> codeToTokens(String code) {
		List<Token> tokenList = new ArrayList<>();
		CodeReader reader = new CodeReader(code.toCharArray());
		JavaLexer mainLexer = new JavaLexer();
		Token t = null;
		do {
			t = mainLexer.scan(reader);
			tokenList.add(t);
		} while (t != null && t.getKind() != TokenKind.EOF);

		return tokenList;
	}

	@Test
	void scan() {
		String code = "int add() { return (1 + 2) * 3; }";
		List<Token> tokenList = codeToTokens(code);
		tokenList.forEach(System.out::println);
	}

}