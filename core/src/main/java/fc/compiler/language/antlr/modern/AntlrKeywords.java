package fc.compiler.language.antlr.modern;

import fc.compiler.common.token.StringTokenKind;

/**
 * @author FC
 */
public interface AntlrKeywords extends StringTokenKind {
	String LEXER    = "lexer";
	String PARSER   = "parser";
	String GRAMMAR  = "grammar";
//	String EOF  = "EOF";
}
