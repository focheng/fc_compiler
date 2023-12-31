package fc.compiler.common.lexer;

import fc.compiler.common.lexer.CodeReader;
import fc.compiler.common.lexer.Lexer;
import fc.compiler.common.lexer.LexerBase;
import fc.compiler.common.token.Token;
import fc.compiler.common.token.TokenKind;
import fc.compiler.language.java.JavaLexer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author FC
 */
public class LexerBaseTest {
	protected Token codeToToken(String code, Lexer lexer) {
		CodeReader reader = new CodeReader(code.toCharArray());
		reader.sp = reader.bp;
		reader.newPosition();
		return lexer.scan(reader);
	}

	protected List<Token> codeToTokens(String code, Lexer mainLexer) {
		List<Token> tokenList = new ArrayList<>();
		CodeReader reader = new CodeReader(code.toCharArray());
		Token t = null;
		do {
			t = mainLexer.scan(reader);
			tokenList.add(t);
		} while (t != null && t.kind() != TokenKind.EOF);

		return tokenList;
	}


	@BeforeEach
	void setUp() {
	}

	@AfterEach
	void tearDown() {
	}

	@Test
	void scan() {
	}
}