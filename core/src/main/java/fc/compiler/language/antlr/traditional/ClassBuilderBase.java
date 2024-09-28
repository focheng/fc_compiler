package fc.compiler.language.antlr.traditional;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * @author FC
 */
@Accessors(fluent = true, chain = true)
public class ClassBuilderBase {
	@Getter @Setter protected String packageName;
	@Getter @Setter protected String lang;
	@Getter @Setter protected String indentUnit = "\t";

	protected StringBuilder sb = new StringBuilder();
	protected int indentCount = 0;
	protected List<String> indentCache = new ArrayList<>();

	protected void add(String line)   { sb.append(getIndent()).append(line).append("\n"); }
	protected void add1(String line)  { sb.append(getIndent()).append("\t").append(line).append("\n"); }
	protected void add2(String line)  { sb.append(getIndent()).append("\t\t").append(line).append("\n"); }
	protected void add3(String line)  { sb.append(getIndent()).append("\t\t\t").append(line).append("\n"); }
	protected void add4(String line)  { sb.append(getIndent()).append("\t\t\t\t").append(line).append("\n"); }
	protected void add5(String line)  { sb.append(getIndent()).append("\t\t\t\t\t").append(line).append("\n"); }
	protected void addEmptyLine()     { sb.append("\n"); }

	protected void add(String line, StringBuilder sb)   { sb.append(getIndent()).append(line).append("\n"); }
	protected void add1(String line, StringBuilder sb)  { sb.append(getIndent()).append("\t").append(line).append("\n"); }
	protected void add2(String line, StringBuilder sb)  { sb.append(getIndent()).append("\t\t").append(line).append("\n"); }
	protected void add3(String line, StringBuilder sb)  { sb.append(getIndent()).append("\t\t\t").append(line).append("\n"); }
	protected void add4(String line, StringBuilder sb)  { sb.append(getIndent()).append("\t\t\t\t").append(line).append("\n"); }
	protected void addEmptyLine(StringBuilder sb)       { sb.append("\n"); }

	protected void increaseIndent() {
		this.indentCount++;
		if (indentCount > indentCache.size())
			indentCache.add(indentUnit.repeat(indentCount));
	}

	protected void decreaseIndent() {
		this.indentCount--;
	}

	protected String getIndent() {
		return indentCount == 0 ? "" : indentCache.get(indentCount-1);
	}
}
