package fc.compiler.common.lexer;

import fc.compiler.common.token.Token;

import java.util.Arrays;
import java.util.function.Predicate;

import static fc.compiler.common.lexer.Constants.*;

/**
 * @author FC
 */
public class CodeReader {
	// -- text of source code and buffer information. --
	protected char[] code;  // the copy of source code from file. also input buffer.
	public char ch;      // the current CHaracter read in the source code.
	protected int bp = -1;  // Buffer Position/Pointer is the index of next char to be read.
						 // -1 indicate reading does not start.

	// -- character buffer for lexeme of multiple characters like literal/identifier.
	//StringBuilder sbLexeme = new StringBuilder();
	protected int sp = 0;   // the Start Position of lexeme in the whole code.

	// -- position information for token: file, line, column --
	public Position position;
	protected String fileName;
	protected int lineNo = 1;           // starting from 1
	protected int lineStartPosition;    // the start position of current line in the whole file.

	public CodeReader(char[] code) { this(code, null); }
	public CodeReader(char[] code, String fileName) {
		this.code = code;
		this.fileName = fileName;

		nextChar();     // MUST call this to start reading.
	}

	/**
	 * Read and return the next character.
	 * @return next character.
	 */
	public char nextChar() {
		++bp;
		if (bp < code.length) {
			ch = code[bp];
		} else {
			ch = EOF;
		}
		return ch;
	}

	/** Return true if having more characters to read. if bp is -1, */
	public boolean hasNext() {
		return bp < code.length - 1;
	}

	public char peekChar(int n) {
		ch = code[bp + n];  // TODO: check overflow
		return ch;
	}

	public String lexeme() { return String.valueOf(Arrays.copyOfRange(code, sp, bp)); }

	public void onStartToken() { onStartToken(null); }
	public void onStartToken(Token token) {
		sp = bp;
		position = new Position(fileName, lineNo, sp - lineStartPosition + 1);
		if (token != null)
			token.position(position);
	}

	public void onEndToken(Token token) {
		token.lexeme(lexeme());
	}

	// -- check type of current character --

	public boolean is(char c) { return this.ch == c; }

	public boolean isDecDigit() { return '0' <= ch && ch <= '9'; }
	public boolean isOctDigit() { return '0' <= ch && ch <= '7'; }
	public boolean isHexDigit() {
		return     '0' <= ch && ch <= '9'
				|| 'a' <= ch && ch <= 'f'
				|| 'A' <= ch && ch <= 'F';
	}

	public boolean isLetter() { return ('a' <= ch && ch <= 'z') || ('A' <= ch && ch <= 'Z'); }
	public boolean isLetterOrDigit() { return isLetter() || isDecDigit(); }

	public boolean isWhiteSpace() {
		return SPACE == ch || ch == TAB || ch == FF;
	}
	public boolean isEndOfLine() {
		return '\r' == ch || ch == '\n';
	}

	public boolean accept(Predicate<Character> predicate) {
		if (predicate.test(this.ch)) {
			nextChar();
			return true;
		}
		return false;
	}

	/**
	 * Compare the current character with the given character.
	 * If matching, read the next character.
	 * @param ch
	 * @return true if matching.
	 */
	public boolean accept(char ch) {
		if (this.ch == ch) {
			nextChar();
			return true;
		}
		return false;
	}

	/**
	 * Compare the current character with one of the given characters.
	 * If matching, read the next character.
	 * @param chars
	 * @return true if matching.
	 */
	public boolean accept(char... chars) {
		for (char c : chars) {
			if (this.ch == c) {
				nextChar();
				return true;
			}
		}

		return false;
	}

	/**
	 * Compare the current and next characters with the characters in the string.
	 */
	public boolean accept(String s) {
		int savedPosition = bp;
		for (int i = 0; i < s.length(); i++) {
			if (ch == s.charAt(i)) {
				nextChar();
			} else {
				bp = savedPosition;
				return false;
			}
		}
		return true;
	}

	public boolean acceptWhiteSpaces() {
		return skipWhitespaces() > 0;
	}

	public boolean acceptLineTerminator() {
		boolean hasCR = accept(CR);
		boolean hasLF = accept(LF);
		if (hasCR || hasLF) {
			lineNo++;
			lineStartPosition = bp;
			return true;
		} else {
			return false;
		}
	}

	public boolean acceptDigits() {
		boolean isDigit = false;
		for (; isDecDigit(); nextChar()) {
			isDigit = true;
		}
		return isDigit;
	}

	/** Skip over ASCII white space characters. */
	public int skipWhitespaces() {
		int count = 0;
		for (; isWhiteSpace(); count++) {
			nextChar();
		}
		return count;
	}

	/** Skip to end of line */
	public void skipToEndOfLine() {
		while (hasNext()) {
			if (isEndOfLine()) {
				break;
			}
			nextChar();
		}
	}
}
