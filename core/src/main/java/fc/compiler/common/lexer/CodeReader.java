package fc.compiler.common.lexer;

/**
 * Read next character and return it to lexer.
 * @author FC
 */
@FunctionalInterface
public interface CodeReader {
	public char nextChar();
}
