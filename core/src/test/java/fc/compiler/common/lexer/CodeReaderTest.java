package fc.compiler.common.lexer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author FC
 */
class CodeReaderTest {

	@Test
	void hasNext() {
		assertEquals(false, new CodeReaderBase("".toCharArray()).hasNext());
		assertEquals(false, new CodeReaderBase("a".toCharArray()).hasNext());
		assertEquals(true, new CodeReaderBase("ab".toCharArray()).hasNext());
	}
}