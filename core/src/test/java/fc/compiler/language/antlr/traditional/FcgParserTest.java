package fc.compiler.language.antlr.traditional;

import fc.compiler.common.ast.Expression;
import fc.compiler.common.ast.expression.CompositeExpression;
import fc.compiler.common.ast.expression.EmptyExpression;
import fc.compiler.common.ast.expression.Identifier;
import fc.compiler.common.ast.expression.ParenthesizedExpression;
import fc.compiler.language.antlr.ast.Rule;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author FC
 */
class FcgParserTest {

	@Test
	void parseRule() {
		Expression expr = getRule("ruleId: (B|C);").expression();
		assertInstanceOf(CompositeExpression.class, expr);
		assertEquals(2,  ((CompositeExpression)expr).children().size());

		expr = getRule("ruleId: 'A' | ASDF | (B|C) | ;").expression();
		assertInstanceOf(CompositeExpression.class, expr);
		assertEquals(4,  ((CompositeExpression)expr).children().size());
		assertInstanceOf(CompositeExpression.class, ((CompositeExpression)expr).children().get(2));
//		assertEquals("Literal(value='A')", getRule("ruleId: 'A';").expression().toString());
//
//		assertEquals("ruleId", getRule("ruleId: ASDF;").name().id());
//		assertEquals("Identifier(id=ASDF)", getRule("ruleId: ASDF;").expression().toString());
//
//		assertEquals("ruleEmpty", getRule("ruleEmpty: ;").name().id());
//		assertEquals(EmptyExpression.singleton, getRule("ruleEmpty: ;").expression());
	}

	private static Rule getRule(String code) {
		FcgLexer lexer = new FcgLexer(code);
		FcgParser parser = new FcgParser(lexer);
		Rule rule = parser.parseRule();
		return rule;
	}
}