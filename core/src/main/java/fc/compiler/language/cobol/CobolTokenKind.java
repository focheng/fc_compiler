package fc.compiler.language.cobol;

/**
 * @author FC
 */
public interface CobolTokenKind {
	// -- separators --
	public static final String SEPARATOR_COMMA = "SEPARATOR_COMMA";
	public static final String SEPARATOR_SEMICOLON = "SEPARATOR_SEMICOLON";
	public static final String SEPARATOR_PERIOD = "SEPARATOR_PERIOD";

	// -- reserved keywords --
	public static final String DIVISION = "DIVISION";
	public static final String IDENTIFICATION = "IDENTIFICATION";
	public static final String ID = "ID";
	public static final String DATA = "DATA";
	public static final String ENVIRONMENT = "ENVIRONMENT";
	public static final String PROCEDURE = "PROCEDURE";
	public static final String PROGRAM_ID = "PROGRAM-ID";

	public static final String SECTION = "SECTION";
	public static final String FILE = "FILE";
	public static final String WORKING_STORAGE = "WORKING-STORAGE";
	public static final String LINKAGE = "LINKAGE";

	public static final String PICTURE = "PICTURE";
	public static final String PIC = "PIC";
	public static final String VALUE = "VALUE";

	public static final String FILLER = "FILLER";

	public static final String IS = "IS";
	public static final String ZERO = "ZERO";
}
