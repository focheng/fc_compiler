package fc.compiler.common.parser;

import fc.compiler.common.lexer.CodeReader;
import fc.compiler.common.lexer.Lexer;
import fc.compiler.common.token.Token;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import static fc.compiler.common.token.TokenKind.*;

/**
 * A bridge between Lexer and Parser just like CodeReader
 * @author FC
 */
@Accessors(fluent = true) @Slf4j
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
		} while (ignoreSpecialTokens && isSpecialToken(t));
		return t;
	}

	protected boolean isSpecialToken(Token t) {
		return t.kind() == LINE_COMMENT || t.kind() == BLOCK_COMMENT || t.kind() == DOC_COMMENT
				|| t.kind() == WHITE_SPACES || t.kind() == LINE_TERMINATOR;
	}

	public boolean accept(Predicate<String> predicate) {
		if (predicate.test(this.token.kind())) {
			nextToken();
			return true;
		}

		syntaxError("accept() failed for token " + token + ".");
		return false;
	}

	public boolean accept(String tokenKind) {
		if (tokenKind.equals(this.token.kind())) {
			nextToken();
			return true;
		}

		syntaxError("accept() failed for token " + token + ".");
		return false;
	}

	protected Token returnAndNextTokenIfEqual(boolean errorIfNotEqual, String... tokenKinds) {
		Token currentToken = token;
		for (String tokenKind : tokenKinds) {
			if (token.kind() == tokenKind) {
				nextToken();    // move to next token
				return currentToken;
			}
		}

		if (errorIfNotEqual) {
			if (tokenKinds.length == 1)
				syntaxError("Token kind " + tokenKinds[0] + " is expected, but token " + token + " is parsed.");
			else
				syntaxError("One of token kind " + Arrays.toString(tokenKinds) + " is expected, but token " + token + " is parsed.");
		}
		return null;
	}

	/** check if equal. if yes, move to next token. otherwise, report error. */
	public boolean acceptAnyOf(String... tokenKinds) {
		return returnAndNextTokenIfEqual(true, tokenKinds) != null;
	}

	/** return current token and move to next token if equal. otherwise, report error. */
	public Token acceptAnyOfAndReturn(String... tokenKinds) {
		return returnAndNextTokenIfEqual(true, tokenKinds);
	}

	public boolean optional(String tokenKind) {
		if (tokenKind.equals(this.token.kind())) {
			nextToken();
			return true;
		}

		return false;
	}

	/** check if equal. if yes, move to next token. otherwise, ignore. */
	public boolean optionalAnyOf(String... tokenKinds) {
		return returnAndNextTokenIfEqual(false, tokenKinds) != null;
	}

	/** return current token and move to next token if equal. otherwise, report error. */
	public Token optionalAnyOfAndReturn(String... tokenKinds) {
		return returnAndNextTokenIfEqual(false, tokenKinds);
	}

	/** accept and advance only if the kind of current token and next tokens are same as @param tokenKinds. */
	public boolean optionalNextTokens(String... tokenKinds) {
		if (isKind(tokenKinds)) {
			nextTokens(tokenKinds.length); // move ahead if all matches
			return true;
		}
		return false;
	}

	public boolean isKind(String tokenKind) {
		return tokenKind.equals(token.kind());
	}

	/** check if the kind of current token and next tokens are same as @param tokenKinds. */
	public boolean isKind(String... tokenKinds) {
		peekToken(tokenKinds.length - 1); // to peek n-1 next tokens.
		for (int i = 0; i < tokenKinds.length; i++) {
			Token t = peekToken(i);
			if (t.kind() != tokenKinds[i]) {
				return false;
			}
		}
		return true;
	}

	public boolean isKindAnyOf(String... tokenKinds) {
		for (String tokenKind : tokenKinds) {
			if (tokenKind.equals(token.kind()))
				return true;
		}
		return false;
	}

	// call nextToken() n times
	public void nextTokens(int n) {
		for (int i = 0; i < n; i++) {
			this.token = nextToken();
		}
	}

	public void skipWhitespacesAndComments() {
		// skip white spaces and comments
		for (Token token = token();
		     token.kind() == WHITE_SPACES || token.kind() == LINE_COMMENT;
		     token = nextToken()) {
		}
	}

	/** Skip all tokens until the given token kind. */
	public void skipTo(String tokenKind) {
		for (Token t = token(); tokenKind.equals(t.kind()); t = nextToken()) {
		}
	}

	protected static void syntaxError(String message) {
		log.error(message);
	}
}
