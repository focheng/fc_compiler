package fc.compiler.common.parser;

import fc.compiler.common.lexer.CodeReader;
import fc.compiler.common.lexer.Lexer;
import fc.compiler.common.token.Token;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static fc.compiler.common.token.TokenKind.*;

/**
 * @author FC
 */
@Accessors(chain = true) @Slf4j
public class TokenReader {
	@Getter @Setter	protected Lexer lexer;
	@Getter @Setter	protected CodeReader codeReader;
	@Getter protected Token token;  // current token
	protected List<Token> lookaheadTokens = new ArrayList<>();
	@Getter protected boolean ignoreSpecialTokens = true;  // white spaces, line terminator and comments

	public TokenReader(Lexer lexer, CodeReader codeReader) {
		this.codeReader = codeReader;
		this.lexer = lexer;

		nextToken();
	}

	public Token nextToken() {
		if (!lookaheadTokens.isEmpty()) {
			token = lookaheadTokens.remove(0);    // always the bottom one
		} else {
			token = doNextToken();
		}
		return token;
	}

	/**
	 *
	 * @param lookahead current token if 0.
	 * @return
	 */
	public Token peekToken(int lookahead) {
		if (lookahead == 0) {
			return token;
		} else {
			// append to the end.
			for (int i = lookaheadTokens.size(); i < lookahead; i++) {
				lookaheadTokens.add(doNextToken());
			}
			return lookaheadTokens.get(lookahead - 1);
		}
	}

	protected Token doNextToken() {
		Token t = null;
		do {
			t = lexer.scan(codeReader);
		} while (ignoreSpecialTokens
				&& (t.getKind() == LINE_COMMENT || t.getKind() == BLOCK_COMMENT || t.getKind() == DOC_COMMENT
					|| t.getKind() == WHITE_SPACES || t.getKind() == LINE_TERMINATOR));
		return t;
	}

	public boolean accept(Predicate<String> predicate) {
		if (predicate.test(this.token.getKind())) {
			nextToken();
			return true;
		}

		syntaxError("accept() failed for token " + token + ".");
		return false;
	}

	public boolean accept(String tokenKind) {
		if (token.getKind() == tokenKind) {
			nextToken();
			return true;
		}

		syntaxError("accept(): " + tokenKind + " is expected. but token " + token + " is parsed");
		return false;
	}

	public boolean accept(String... tokenKinds) {
		for (String tokenKind : tokenKinds) {
			if (token.getKind() == tokenKind) {
				nextToken();
				return true;
			}
		}

		syntaxError("accept(): " + tokenKinds + " is expected. but token " + token + " is parsed");
		return false;
	}

	public Token acceptAndReturn(String tokenKind) {
		Token result = token;
		if (token.getKind() == tokenKind) {
			nextToken();
			return result;
		}

		syntaxError("acceptAndReturn(): " + tokenKind + " is expected. but token " + token + " is parsed");
		return null;
	}

	public Token acceptAndReturn(String... tokenKinds) {
		Token result = token;
		for (String tokenKind : tokenKinds) {
			if (token.getKind() == tokenKind) {
				nextToken();
				return result;
			}
		}

		syntaxError("acceptAndReturn(): " + tokenKinds + " is expected. but token " + token + " is parsed");
		return null;
	}

	public void skipWhitespacesAndComments() {
		// skip white spaces and comments
		for (Token token = getToken();
		     token.getKind() == WHITE_SPACES || token.getKind() == LINE_COMMENT;
		     token = nextToken()) {
		}
	}

	public boolean optional(String tokenKind) {
		if (token.getKind() == tokenKind) {
			nextToken();
			return true;
		}
		return false;
	}

//	public boolean is(String tokenKind) {
//
//	}

//	public boolean isAhead(String... tokenKinds) {
//		int n = tokenKinds.length;
//		peekToken(n);
//		for (int i = 0; i < n; i++) {
//			Token t = peekToken(i+1);
//			if (t.getKind() != tokenKinds[i]) {
//				return false;
//			}
//		}
//		return true;
//	}

	/** Check token kind of current token and next tokens are same as @param tokenKinds. */
	public boolean acceptMultiTokens(String... tokenKinds) {
		int n = tokenKinds.length - 1;  // exclude the current token.
		peekToken(n);
		for (int i = 0; i < tokenKinds.length; i++) {
			Token t = peekToken(i);
			if (t.getKind() != tokenKinds[i]) {
				return false;
			}
		}

		// move ahead if all matches
		for (int i = 0; i < tokenKinds.length; i++) {
			this.token = nextToken();
		}
		return true;
	}

	protected static void syntaxError(String message) {
		log.error(message);
	}
}
