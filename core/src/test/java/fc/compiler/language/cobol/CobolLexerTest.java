package fc.compiler.language.cobol;

import fc.compiler.common.lexer.CodeReader;
import fc.compiler.common.lexer.LexerBase;
import fc.compiler.common.token.Token;
import fc.compiler.common.token.TokenKind;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author FC
 */
class CobolLexerTest {
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