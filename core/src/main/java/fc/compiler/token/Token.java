package fc.compiler.token;

import fc.compiler.lexer.Position;
import lombok.*;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Token is the output of lexer and the input of parser.
 * Token consists of a token kind (named in dragon book) and optional attribute values.
 * A lexeme is a sequence of characters in the source code that matches the pattern for a token
 * and is identified by the lexer as an instance of that token.
 *
 * @author FC
 */

@ToString @Getter @Setter @Accessors(chain = true)
@NoArgsConstructor @RequiredArgsConstructor @AllArgsConstructor
public class Token implements Cloneable {
	@NonNull protected String kind;
	@NonNull protected Position position;
	protected String lexeme;
	protected HashMap<String, Object> attributes;

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
}
