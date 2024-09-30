package fc.compiler.common.token;

import fc.compiler.common.lexer.Position;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.HashMap;

@Getter @Setter @Accessors(fluent = true, chain = true)
@NoArgsConstructor @RequiredArgsConstructor @AllArgsConstructor
public class TokenBase<Kind> implements Token<Kind> {
	@NonNull
	protected Kind kind;
	protected String lexeme;
	protected HashMap<String, Object> attributes;
	@NonNull
	protected Position position;

	public TokenBase<Kind> radix(int radix) {
		if (attributes == null) {
			attributes = new HashMap<>();
		}
		attributes.put("radix", radix);
		return this;
	}

	public Object attribute(String key) {
		return attributes == null ? null : attributes.get(key);
	}

	public TokenBase<Kind> attribute(String key, Object value) {
		if (attributes == null) {
			attributes = new HashMap<>();
		}
		attributes.put(key, value);
		return this;
	}

	@SuppressWarnings("unchecked")
	public Object clone() {
		try {
			StringToken copy = (StringToken)super.clone();
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
