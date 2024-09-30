package fc.compiler.common.lexer;

import fc.compiler.common.token.StringToken;
import fc.compiler.common.token.StringTokenKind;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author FC
 */
public class LexerWithCodeReaderBaseTest {
	protected StringToken codeToToken(String code, LexerWithCodeReader lexer) {
		CodeReaderBase reader = new CodeReaderBase(code.toCharArray());
		reader.onStartToken();
		return lexer.scanToken(reader);
	}

	protected List<StringToken> codeToTokens(String code, LexerWithCodeReader mainLexer) {
		List<StringToken> tokenList = new ArrayList<>();
		CodeReaderBase reader = new CodeReaderBase(code.toCharArray());
		StringToken t = null;
		do {
			t = mainLexer.scanToken(reader);
			tokenList.add(t);
		} while (t != null && t.kind() != StringTokenKind.EOF);

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