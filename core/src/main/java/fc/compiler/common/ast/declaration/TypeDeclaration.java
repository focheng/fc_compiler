package fc.compiler.common.ast.declaration;

import fc.compiler.common.ast.AstNode;
import fc.compiler.common.ast.Expression;
import fc.compiler.common.ast.Statement;
import fc.compiler.common.ast.StatementBase;
import fc.compiler.common.ast.expression.Identifier;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.lang.reflect.Member;
import java.util.List;

/**
 * @author FC
 */
@Getter @Setter @Accessors(fluent = true)
public class TypeDeclaration extends StatementBase {
	String tag; // class, interface, enum or record in Java.
	// Modifiers modifiers;
	Identifier name;
	List<Expression> parents;
	List<Expression> interfaces;
	Statement statement;
	List<VariableDeclaration> fields;
	List<FunctionDeclaration> methods;
//	List<AstNode> members;
}
