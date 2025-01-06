package fc.compiler.language.pli;

import fc.compiler.common.lexer.Position;
import fc.compiler.common.token.TokenBase;
import lombok.ToString;

import java.util.HashMap;

/**
 * @author FC
 */
public class PliToken extends TokenBase<PliTokenKind> {

	public PliToken(PliTokenKind kind, String lexeme) {
		this.kind = kind;
		this.lexeme = lexeme;
	}

	public PliToken(PliTokenKind kind, String lexeme, Position position, HashMap<String, Object> attributes) {
		super(kind, lexeme, position, attributes);
	}

	public String toString() {
		return super.toString();
//		String s;
//		if (kind == PliTokenKind.NEW_LINE) {
//			s = kind.name() + "('\\n')";
//		} else if (kind.tag() == PliTokenKindTag.KEYWORD) {
//			s = kind.name();
//		} else if (lexeme == null) {
//			if (kind.lexeme() == null)
//				s = kind.name();
//			else
//				s = kind.name() + "(" + kind.lexeme() + ")";
//		} else {
//			s = kind.name() + "(" + lexeme + ")";
//		}
//
//		return s;
	}
}
