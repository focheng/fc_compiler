package fc.compiler.language.cobol;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * @author FC
 */
public class Preprocessor {
	public List<String> proprocess(List<String> lines) {
		Function<String, String> f = Preprocessor::removeSequenceNo;
		f = f.andThen(Preprocessor::removeProgramIdNumber);
		return convert(lines, f);
	}
	public static List<String> convert(List<String> lines, Function<String, String> f) {
		List<String> newLines = new ArrayList<>();
		for (String line : lines) {
			newLines.add(f.apply(line));
		}
		return newLines;
	}

	public static String removeSequenceNo(String line) {
		if (line == null || line.length() < 7)
			return "";
		return "      " + line.substring(6);
	}

	public static String removeProgramIdNumber(String line) {
		if (line == null || line.length() < 73)
			return line;
		return line.substring(0, 72);
	}
}
