package fc.compiler.language.pli;

import org.junit.jupiter.api.Test;

import static fc.compiler.language.pli.PliTokenKind.*;
import static org.junit.jupiter.api.Assertions.*;

class PliLexerTest {

    @Test
    void testStringLiteral() {
        // 'Shakespeare''s "Hamlet"' is identical to "Shakespeare's ""Hamlet""
        String expected = "Shakespeare's \"Hamlet\"";
        assertEquals(22, expected.length());
        eq(expected, "'Shakespeare''s \"Hamlet\"'");
        eq(expected, "\"Shakespeare's \"\"Hamlet\"\"");

        eq("", "''");
//        eq("Walla Walla ", "(2)'Walla '");
//
        eq("", "''X");      // length is 0
        eq("0d0A", "'0d0A'x");  // length is 2

        eq("", "''B");      // length is 0
        eq("0", "'0'B");     // length is 1
        eq("1100_1010_11", "'1100_1010_11'B");  // length is 10
//        eq("", "(64)'1'B");         // length is 64
        eq("100", "'100'XN");      /* same as ’00000100’XN with value 256 */
        eq("100", "'100'XU");      /* same as ’00000100’XU with value 256 */
        eq("ffff_ffff", "'ffff_ffff'XN");      /* is the value -1 */
        eq("ffff_ffff", "'ffff_ffff'XU");      /* is the value 2**32-1 */

        eq("CA", "'CA'B4");   // "1100_1010"B B4 (hex) bit constant
        eq("22", "'22'B3");   // "010_010"B   B3 (octal) bit constant

        eq("81a1", "'81a1'gx");      // one DBCS character

    }

    @Test
    void testNumberLiteral() {
        // Decimal fixed-point constant Precision
        eq(NUMBER_LITERAL, 1234567, 10, "1234567");
        eq(NUMBER_LITERAL, 1234567, 10, "1_234_567");
        eq(NUMBER_LITERAL, 3.1415926, 10, "3.1415926");
        eq(NUMBER_LITERAL, 3, 10, "003");
        eq(NUMBER_LITERAL, .0012, 10, ".0012");

        // Binary fixed-point constant
        eq(NUMBER_LITERAL, 22, 2, "1011_0B");
        eq(NUMBER_LITERAL, 11.7, 2, "1011.111B");

        // Decimal floating-point constant
        eq(NUMBER_LITERAL, 438E0,   10, "438E0");
        eq(NUMBER_LITERAL, 4E-3,    10, "4E-3");
        eq(NUMBER_LITERAL, 15E-23,  10, "15E-23");
        eq(NUMBER_LITERAL, 15E23,   10, "15E23");
        eq(NUMBER_LITERAL, 1.96E+07,10, "1.96E+07");
        eq(NUMBER_LITERAL, 3_141_593E-6,    10, "3_141_593E-6");
        eq(NUMBER_LITERAL, .003_141_593E3,  10, ".003_141_593E3");

        // Binary floating-point constant
        eq(NUMBER_LITERAL, 45E5,   2, "101101E5B");
        eq(NUMBER_LITERAL, 5.5E2,   2, "101.101E2B");
        eq(NUMBER_LITERAL, 29E-28,   2, "11101E-28B");
        eq(NUMBER_LITERAL, 3.1E+42,   2, "11.01E+42B"); // ?
    }

    @Test
    void testComplexLiteral() {
        // Imaginary
        // 27I
        // 3.968E10I
        // 11011.01BI
        // 38+27I
    }

    void eq(String expected, String code) {
        PliToken token = code2Token(code);
        assertEquals(expected, token.lexeme());
    }

    void eq(PliTokenKind expectedKind, Object expectedValue, int expectedRadix, String code) {
        PliToken token = code2Token(code);
        //assertEquals(code, token.lexeme());
        assertEquals(expectedKind, token.kind());
        assertEquals(expectedValue, token.value());
        System.out.println(expectedValue);
        assertEquals(expectedRadix, token.radix());
    }

    private static PliToken code2Token(String code) {
        PliLexer lexer = new PliLexer(code);
        PliToken token = lexer.scanToken();
        return token;
    }
}