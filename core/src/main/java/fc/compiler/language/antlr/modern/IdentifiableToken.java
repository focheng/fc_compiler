package fc.compiler.language.antlr.modern;

import lombok.*;
import lombok.experimental.Accessors;

import java.util.Set;

/**
 * @author FC
 */
@Getter @Setter @Accessors(fluent = true) @ToString
public class IdentifiableToken {
	String tokenKind;
	Set<String> alternatives;   // if not null, tokenKind is null.
	boolean optional;           // mandatory (default, +) or optional (?,*)

	public IdentifiableToken(String tokenKind) { this.tokenKind = tokenKind; }
	public IdentifiableToken(String tokenKind, boolean optional) {
		this.tokenKind = tokenKind;
		this.optional = optional;
	}
	public IdentifiableToken(String tokenKind, boolean optional, Set<String> alternatives) {
		this.tokenKind = tokenKind;
		this.optional = optional;
		this.alternatives = alternatives;
	}
};
