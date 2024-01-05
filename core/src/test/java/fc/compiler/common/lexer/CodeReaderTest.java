package fc.compiler.common.lexer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author FC
 */
class CodeReaderTest {

	@Test
	void hasNext() {
		assertEquals(false, new CodeReader("".toCharArray()).hasNext());
		assertEquals(false, new CodeReader("a".toCharArray()).hasNext());
		assertEquals(true, new CodeReader("ab".toCharArray()).hasNext());
	}
}