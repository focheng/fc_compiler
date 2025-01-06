package fc.compiler.language.antlr.traditional;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static fc.compiler.language.antlr.traditional.LexerBuilder.*;

/**
 * @author FC
 */
class LexerBuilderTest {

	@Test
	void test() {
		List<CharNode> nodes = new ArrayList<>();
		// case - single char: ';'
		nodes.add(new CharNode(";", "SEMICOLON"));
		// case - optional compound assignment operator: '~', '~='
		nodes.add(new CharNode("~", "TILDE"));
		nodes.add(new CharNode("~=", "TILDE_EQ"));
		// case - : '+', '++', '+='
		nodes.add(new CharNode("+", "PLUS"));
		nodes.add(new CharNode("++", "PLUS_PLUS"));
		nodes.add(new CharNode("+=", "PLUS_EQ"));
		// case - : '/', '/*', '/**', '/='
		nodes.add(new CharNode("/", "SLASH"));
		nodes.add(new CharNode("/*", "BLOCK_COMMENT"));
		nodes.add(new CharNode("/**", "DOC_COMMENT"));
		nodes.add(new CharNode("/=", "SLASH_EQ"));
		// case - : '>', '>=', '>>', '>>=', '>>>', '>>>='
		nodes.add(new CharNode(">", "GT"));
		nodes.add(new CharNode(">=", "GT_EQ"));
		nodes.add(new CharNode(">>", "GT_GT"));
		nodes.add(new CharNode(">>=", "GT_GT_EQ"));
		nodes.add(new CharNode(">>>", "GT_GT_GT"));
		nodes.add(new CharNode(">>>=", "GT_GT_GT_EQ"));

		LexerBuilder lb = new LexerBuilder();
		lb.options(new CodeGeneratorOptions().packageName("foo.fox").lang("Abc"));
		lb.rootCharNodes(nodes);
		System.out.println(lb.toCode());
	}
}