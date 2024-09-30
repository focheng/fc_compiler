package fc.compiler.common.lexer;

import java.util.function.Predicate;

/**
 * A reader class will read source code and provide characters in stream.
 *
 * @author FC
 */
public class CodeReaderBase {
	protected static final char EOF_CHAR   = 0x1A;   // control-z, End of File.
	protected static final char SPACE      = ' ';
	protected static final char TAB        = '\t';
	protected static final char VT         = 0x0B;
	protected static final char FF         = 0x0C;
	protected static final char CR         = '\r';
	protected static final char LF         = '\n';

	// -- text of source code and buffer information. --
	protected char[] code;  // the copy of source code from file. also input buffer.
	public char ch;         // the current CHaracter read in the source code.
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

	public CodeReaderBase(String code) { this(toCharArrayPlusEof(code), null); }
	public CodeReaderBase(char[] code) { this(code, null); }
	public CodeReaderBase(String code, String fileName) {
		this(toCharArrayPlusEof(code), fileName);
	}
	public CodeReaderBase(char[] code, String fileName) {
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
			ch = EOF_CHAR;
		}
		return ch;
	}

	/** Return true if having more characters to read. if bp is -1, */
	public boolean hasNext() {
		return bp < code.length - 1;
	}

	/** peek and return the current + N character without moving bp forward. */
	public char peekChar(int n) {
		if (bp + n >= code.length)
			return EOF_CHAR;
		ch = code[bp + n];
		return ch;
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
		return ch == SPACE || ch == TAB || ch == FF || ch == VT;
	}
	public boolean isEndOfLine() {
		return '\r' == ch || ch == '\n';
	}

	// -- accept/optional: match and next char --

	public boolean optionalChar(Predicate<Character> predicate) {
		if (predicate.test(this.ch)) {
			nextChar();
			return true;
		}
		return false;
	}

	/**
	 * Compare the current character with the expected character.
	 * If matching, read the next character.
	 * @return true if matching.
	 */
	public boolean optionalChar(char expected) {
		if (this.ch == expected) {
			nextChar();
			return true;
		}
		return false;
	}

	private boolean optionalChar(char... expectedChars) {
		int savedPosition = bp;
		for (char expected : expectedChars) {
			if (expected != ch) {
				bp = savedPosition;
				return false;
			}
			nextChar();
		}
		return true;
	}

	/**
	 * Compare the current and next characters with the characters in the string.
	 */
	public boolean optionalChar(String expectedChars) {
		return optionalChar(expectedChars.toCharArray());
	}

	/**
	 * Compare the current character with one of the expected characters.
	 * If matching, read the next character.
	 * @return true if matching.
	 */
	public boolean acceptAnyChar(char... expectedChars) {
		for (char expected : expectedChars) {
			if (this.ch == expected) {
				nextChar();
				return true;
			}
		}

		return false;
	}

	public boolean acceptChar(Predicate<Character> predicate) {
		if (predicate.test(this.ch)) {
			nextChar();
			return true;
		}
		logError(ch + " scanned");
		return false;
	}

	/**
	 * Compare the current character with the expected character.
	 * If matching, read the next character.
	 * @return true if matching.
	 */
	public boolean acceptChar(char expected) {
		if (this.ch == expected) {
			nextChar();
			return true;
		}
		logError(expected + " is expected, but " + ch + " scanned");
		return false;
	}

	private boolean acceptChar(char... expectedChars) {
		int savedPosition = bp;
		for (char expected : expectedChars) {
			if (expected != ch) {
				bp = savedPosition;
				logError(expected + " is expected, but " + ch + " scanned");
				return false;
			}
			nextChar();
		}
		return true;
	}

	/**
	 * Compare the current and next characters with the characters in the string.
	 */
	public boolean acceptChar(String expectedChars) {
		return acceptChar(expectedChars.toCharArray());
	}

	public boolean acceptWhiteSpaces() {
		return skipWhitespaces() > 0;
	}

	public boolean acceptLineTerminator() {
		boolean hasCR = optionalChar(CR);
		boolean hasLF = optionalChar(LF);
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

	// -- helper --

	public void onStartToken() {
		sp = bp;
		position = new Position(fileName, lineNo, sp - lineStartPosition + 1);
	}

	public String lexeme() { return String.copyValueOf(code, sp, bp - sp); }
	//public String lexeme() { return String.valueOf(Arrays.copyOfRange(code, sp, bp)); }

	public String stringLiteralLexeme() {
		return String.copyValueOf(code, sp + 1, bp - sp - 2);
	}


	protected void logError(String hint) {
		System.out.println("lex error: unsupported char " + ch + " " + hint);
	}

	public static char[] toCharArrayPlusEof(String code) {
		char[] array = new char[code.length() + 1];
		code.getChars(0, code.length(), array, 0);
		array[code.length()] = EOF_CHAR;
		return array;
	}
}
