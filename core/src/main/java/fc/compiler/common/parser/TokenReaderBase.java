package fc.compiler.common.parser;

import fc.compiler.common.lexer.Lexer;
import fc.compiler.common.token.Token;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @author FC
 */
@Slf4j
public class TokenReaderBase<Kind, T extends Token<Kind>> implements TokenReader<Kind> {
	protected Lexer<T> lexer;

	@Getter protected Token<Kind> token;        // current token
	@Getter protected Token<Kind> lastToken;    // last token
	protected List<Token<Kind>> lookaheadTokens = new ArrayList<>();
	@Getter protected boolean ignoreSpecialTokens = true;  // white spaces, line terminator and comments

	@Override
	public Token<Kind> nextToken() {
		lastToken = token;
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
	public Token<Kind> peekToken(int lookahead) {
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

	protected Token<Kind> doNextToken() {
		Token<Kind> t = null;
		do {
			t = lexer.scanToken();
		} while (ignoreSpecialTokens && isSpecialToken(t));
		return t;
	}

	protected boolean isSpecialToken(Token<Kind> t) {
		return false;
//		return t.kind() == LINE_COMMENT || t.kind() == BLOCK_COMMENT || t.kind() == DOC_COMMENT
//				|| t.kind() == WHITE_SPACES || t.kind() == LINE_TERMINATOR;
	}

	// call nextToken() n times
	public void nextTokens(int n) {
		for (int i = 0; i < n; i++) {
			this.token = nextToken();
		}
	}


	// -- accept/optional: match and next token --

	/**
	 * Compare the current token with the expected token to see if the current token is acceptable.
	 * If acceptable, read the next token. otherwise, report error.
	 * @param expected token kind
	 * @return
	 */
	public boolean acceptToken(Kind expected) { return acceptToken(expected, null); }
	public boolean acceptToken(Kind expected, String errorHint) {
		boolean accepted = this.token.kind() == expected;
		if (accepted) {
			nextToken();
		} else {
			syntaxError("Token kind " + expected + " is expected, but token " + token + " was parsed."
					+ (errorHint == null ? "" : errorHint));
		}
		return accepted;
	}

	/**
	 * Compare the current token with the expected token.
	 * If matching, read the next token. otherwise, just ignore.
	 * @param expected token kind
	 * @return
	 */
	public boolean optionalToken(Kind expected) {
		boolean accepted = this.token.kind() == expected;
		if (accepted) {
			nextToken();
		}
		return accepted;
	}

	/** check if equal. if yes, move to next token. otherwise, report error. */
	public boolean acceptAnyOf(Kind... expectedKinds) {
		boolean accepted = isAnyOf(this.token.kind(), expectedKinds);
		if (accepted) {
			nextToken();
		} else {
			syntaxError("Any token kind of " + expectedKinds + " is expected, but token " + token + " is parsed.");
		}
		return accepted;
	}

	/** check if equal. if yes, move to next token. otherwise, ignore. */
	public boolean optionalAnyOf(Kind... expectedKinds) {
		boolean accepted = isAnyOf(this.token.kind(), expectedKinds);
		if (accepted) {
			nextToken();
		}
		return accepted;
	}

	/** accept and advance only if the kind of current token and next tokens are same as @param tokenKinds. */
	public boolean optionalSequentialTokens(Kind... tokenKinds) {
		if (matchSequentialTokens(tokenKinds)) {
			nextTokens(tokenKinds.length); // move ahead if all matches
			return true;
		}
		return false;
	}

	// -- check kind of current token --

	/** check if the kind of current token equals the @param tokenKind. */
	public boolean isKind(Kind that) {
		return that == token.kind();
	}

	/** check if the kind of current token equals any of the @param tokenKinds. */
	public boolean isAnyOf(Kind that, Kind... kinds) {
		for (Kind kind : kinds) {
			if (that == kind)
				return true;
		}
		return false;
	}

	public boolean isNoneOf(Kind that, Kind... kinds) {
		for (Kind kind : kinds) {
			if (that == kind)
				return false;
		}
		return true;
	}

	/** check if the kind of current token and next tokens are same as @param sequentialKinds. */
	public boolean matchSequentialTokens(Kind... sequentialKinds) {
		peekToken(sequentialKinds.length - 1); // to peek n-1 next tokens.
		for (int i = 0; i < sequentialKinds.length; i++) {
			Token<Kind> t = peekToken(i);
			if (t.kind() != sequentialKinds[i]) {
				return false;
			}
		}
		return true;
	}

	public List<Token<Kind>> ignoreUntil(Kind... expectedKinds) {
		List<Token<Kind>> ignoredTokens = new ArrayList<>();
		while (isNoneOf(token.kind(), expectedKinds)) {
			ignoredTokens.add(token);
			nextToken();
		}
		return ignoredTokens;
	}

	public void skipWhitespacesAndComments() {
//		// skip white spaces and comments
//		for (Token<Kind> token = token();
//		     token.kind() == WHITE_SPACES || token.kind() == LINE_COMMENT;
//		     token = nextToken()) {
//		}
	}

	/** Skip all tokens until the given token kind. */
	public void skipTo(String tokenKind) {
//		for (Token<Kind> t = token(); tokenKind.equals(t.kind()); t = nextToken()) {
//		}
	}

	// -- error handling --

	protected void syntaxError(String hint) {
		logError(hint);
		nextToken();
	}

	protected void logError(String hint) {
		log.error("syntax error: unsupported token " + token  + " @" + lexer.position() + " " + hint);
	}

}
