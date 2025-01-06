package fc.compiler.language.antlr.traditional;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author FC
 */
@Getter @Setter @Accessors(fluent = true, chain = true)
public class CodeGeneratorOptions {
	private String packageName;
	private String lang;
	private String indentUnit = "\t";

	// -- variables from antlr file --
	private boolean caseSensitive;

}
