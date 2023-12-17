package fc.compiler.common.token;

import fc.compiler.common.lexer.Position;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.HashMap;

/**
 * Token is the output of lexer and the input of parser.
 * Token consists of a token kind (named in dragon book) and optional attribute values.
 * A lexeme is a sequence of characters in the source code that matches the pattern for a token
 * and is identified by the lexer as an instance of that token.
 *
 * @author FC
 */

@Getter @Setter @Accessors(chain = true)
@NoArgsConstructor @RequiredArgsConstructor @AllArgsConstructor
public class Token implements Cloneable {
	@NonNull protected String kind;
	protected String lexeme;
	protected HashMap<String, Object> attributes;
	@NonNull protected Position position;

	public Token setRadix(int radix) {
		if (attributes == null) {
			attributes = new HashMap<>();
		}
		attributes.put("radix", radix);
		return this;
	}
	@SuppressWarnings("unchecked")
	public Object clone() {
		try {
			Token copy = (Token)super.clone();
			copy.attributes = (HashMap<String, Object>)this.attributes.clone();
			return copy;
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Token(").append(kind).append(", ");
		if (lexeme != null) {
			sb.append("'").append(lexeme).append("'");
		}
		sb.append(", ").append(position);
		if (attributes != null)
			sb.append(", attributes=").append(attributes);
		sb.append(")");
		return sb.toString();
	}
}
