package fc.compiler.common.lexer;

import java.util.Arrays;
import java.util.function.Predicate;

import static fc.compiler.common.lexer.Constants.*;

/**
 * @author FC
 */
public class CodeReader {
	// -- text of source code and buffer information. --
	public char[] code;  // the copy of source code from file. also input buffer.
	public char ch;      // the current CHaracter read in the source code.
	public int bp = -1;  // Buffer Position/Pointer is the index of next char to be read.

	// -- character buffer for lexeme of multiple characters like literal/identifier.
	public int sp = 0;   // the Start Position of lexeme.

	// -- position information for token: file, line, column --
	public Position position;
	public String fileName;
	public int lineNo = 1;
	public int lineStartPosition;    // the start position of one line in the whole file.

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

	public boolean hasNext() {
		return bp < code.length - 1;
	}

	public char peekChar(int n) {
		ch = code[bp + n];
		return ch;
	}

	public String lexeme() { return String.valueOf(Arrays.copyOfRange(code, sp, bp)); }

	public void newPosition() {
		position = new Position(fileName, lineNo, sp - lineStartPosition + 1);
	}

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
	public void skipWhitespace() {
		while (accept(SPACE, TAB, FF)) {
			// accept() already read the next character
		}
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
