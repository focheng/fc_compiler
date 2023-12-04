package fc.compiler.lexer;

import java.util.Arrays;

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
		ch = code[++bp];
		return ch;
	}

	public char peekChar(int n) {
		ch = code[bp + n];
		return ch;
	}

	public String getLexeme() { return String.valueOf(Arrays.copyOfRange(code, sp, bp)); }

	public void newPosition() {
		position = new Position(fileName, sp - lineStartPosition, lineNo);
	}

	/**
	 * Compare the current character with the given character. If matching, read the next character.
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
	 * @param ch1
	 * @param chars
	 * @return true if matching.
	 */
	public boolean accept(char ch1, char... chars) {
		if (this.ch == ch1) {
			nextChar();
			return true;
		}

		for (char c : chars) {
			if (this.ch == c) {
				nextChar();
				return true;
			}
		}

		return false;
	}
}
