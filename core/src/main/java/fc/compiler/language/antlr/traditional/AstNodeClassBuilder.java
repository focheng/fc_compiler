package fc.compiler.language.antlr.traditional;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * @author FC
 */
@Accessors(fluent = true, chain = true)
public class AstNodeClassBuilder extends ClassBuilderBase {
	@Getter private Map<String, String> types = new LinkedHashMap<>();  // type name -> code

	public void addType(String ruleName, String variable) {
		sb.setLength(0);
		String typeName = StringUtils.capitalize(ruleName);
		add(STR."public class \{typeName} {");
		add1(STR."private Identifier \{variable};");
		add("}");
		types.put(typeName, sb.toString());
	}

	public void addType(String ruleName, Map<String, String> map) {
		sb.setLength(0);
		String typeName = StringUtils.capitalize(ruleName);
//		addPackage();
//		addEmptyLine();
//
//		add("import fc.compiler.common.ast.*;");
//		add("import fc.compiler.common.ast.declaration.*;");
//		add("import fc.compiler.common.ast.expression.*;");
//		add("import fc.compiler.common.ast.statement.*;");
//		addEmptyLine();

		add(STR."public class \{typeName} {");

		map.forEach((variable, variableType) -> {
			add1(STR."private \{variableType} \{variable};");
		});

		add("}");
		types.put(typeName, sb.toString());
	}

	public void addBaseClass(String ruleName) {
		sb.setLength(0);
		String typeName = StringUtils.capitalize(ruleName);
		add(STR."public class \{typeName} {");
		add("}");
		types.put(typeName, sb.toString());
	}

	public void startType(String typeName) {
		sb.setLength(0);
		add(STR."public class \{typeName} {");
	}

	public void addField(String variable, String variableType) {
		add1(STR."private \{variableType} \{variable};");
	}

	public void endType(String typeName) {
		add("}");
		types.put(typeName, sb.toString());
	}

	public String toCode() {
		return sb.toString();
	}
}
