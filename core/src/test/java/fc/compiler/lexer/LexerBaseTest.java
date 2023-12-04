package fc.compiler.lexer;

import fc.compiler.token.Token;
import fc.compiler.token.TokenKind;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author FC
 */
class LexerBaseTest {

	@BeforeEach
	void setUp() {
	}

	@AfterEach
	void tearDown() {
	}

	@Test
	void scan() {
		String code = "  identification division.";
		CodeReader reader = new CodeReader(code.toCharArray());
		LexerBase lexer = new LexerBase();
		Token t = null;
		do {
			t = lexer.scan(reader);
			System.out.println(t);
		} while (t != null && t.getKind() == TokenKind.EOF);
	}
}