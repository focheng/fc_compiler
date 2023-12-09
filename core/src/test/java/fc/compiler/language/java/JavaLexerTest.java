package fc.compiler.language.java;

import fc.compiler.common.lexer.CodeReader;
import fc.compiler.common.lexer.LexerBase;
import fc.compiler.common.token.Token;
import fc.compiler.common.token.TokenKind;
import fc.compiler.lexer.LexerBaseTest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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