package fc.compiler.language.antlr;

import fc.compiler.common.token.TokenKind;

/**
 * @author FC
 */
public interface AntlrKeywords extends TokenKind {
	String LEXER    = "lexer";
	String PARSER   = "parser";
	String GRAMMAR  = "grammar";
//	String EOF  = "EOF";
}
