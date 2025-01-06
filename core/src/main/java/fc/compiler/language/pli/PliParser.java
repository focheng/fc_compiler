package fc.compiler.language.pli;

import fc.compiler.common.ast.AstNode;
import fc.compiler.common.ast.CompilationUnit;
import fc.compiler.common.ast.Expression;
import fc.compiler.common.ast.Statement;
import fc.compiler.common.ast.declaration.VariableDeclaration;
import fc.compiler.common.ast.expression.*;
import fc.compiler.common.ast.statement.*;
import fc.compiler.common.parser.Parser;
import fc.compiler.common.parser.TokenReaderBase;
import fc.compiler.common.token.Token;
import fc.compiler.language.pli.ast.expression.StarExpression;
import fc.compiler.language.pli.ast.preprocessor.IncludeStatement;
import fc.compiler.language.pli.ast.preprocessor.PageStatement;
import fc.compiler.language.pli.ast.statement.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static fc.compiler.language.pli.PliTokenKind.*;
import static fc.compiler.language.pli.PliTokenKindTag.BINARY_OPERATOR;

/**
 * @author FC
 */
public class PliParser extends TokenReaderBase<PliTokenKind, PliToken>
		implements Parser<AstNode> {
	public PliParser(PliLexer lexer) {
		super.lexer = lexer;

		nextToken();
	}

	@Override
	public AstNode parse() {
		return parseCompilationUnit();
	}

	// == compilation unit ==

	public CompilationUnit parseCompilationUnit() {
		CompilationUnit cu = new CompilationUnit();
		cu.statementList(parseStatementList());
		return cu;
	}

	public List<Statement> parseStatementList() {
		List<Statement> statements = new ArrayList<>();
		while (token.kind() != EOF) {
			Statement statement = parseStatement();
			statements.add(statement);
		}
		return statements;
	}

	public List<Statement> parseBlock(Identifier name) {
		List<Statement> statements = new ArrayList<>();
		while (token.kind() != EOF) {
			if (optionalToken(END)) {
				if (token.kind() == IDENTIFIER) {
					if (name != null && !name.id().equals(token.lexeme())) {
						syntaxError("The end name " + token.lexeme() + " does not match " + name.id());
					} else {
						nextToken();
					}
				}
				acceptToken(SEMICOLON);
				break;
			}
			Statement statement = parseStatement();
			statements.add(statement);
		}
		return statements;
	}

	// == statements ==

	private Statement parseStatement() {
		switch (token.kind()) {
			case DECLARE:       return parseDeclareStatement();
			case DEFINE:       	return parseDefineStatement();
			case CALL:          return parseCallStatement();
			case RETURN:        return parseReturnStatement();
			case STOP:          return parseStopStatement();
			case EXIT:          return parseExitStatement();
			case ON:          	return parseOnStatement();
			//case EXEC:          return parseExecStatement();
			case IF:            return parseIfStatement();
			case SELECT:        return parseSelectStatement();
			case DO:            return parseDoStatement();
			case OPEN:          return parseOpenStatement();
			case CLOSE:         return parseCloseFileStatement();
			case DISPLAY:       return parseDisplayStatement();
			case PERCENT:       return parsePreprocessorStatement();
			case SEMICOLON:     return parseEmptyStatement();
			case IDENTIFIER:
				if (peekToken(1).kind() == COLON) {
					Identifier name = parseIdentifier();
					acceptToken(COLON);
					switch (token.kind()) {
						case PROCEDURE: return parseProcedureDeclaration(name);
						case PACKAGE:   return parsePackageDeclaration(name);
						case BEGIN:     return parseBeginBlock(name);
					}
				} else {
					return parseAssignmentStatement();
				}
			default:
				syntaxError("to identify statement type.");
				ignoreUntil(SEMICOLON, EOF);
				optionalToken(SEMICOLON);
		}
		return null;
	}

	private Statement parseEmptyStatement() {
		if (!acceptToken(SEMICOLON)) return null;
		return new EmptyStatement();
	}

	// -- program control statements --

	private Statement parseIfStatement() {
		if (!acceptToken(IF)) return null;

		IfStatement result = new IfStatement();
		result.condition(parseExpression());
		acceptToken(THEN);
		result.thenStatement(parseStatement());
		if (optionalToken(ELSE))
			result.elseStatement(parseStatement());
		return result;
	}

	private Statement parseSelectStatement() {
		if (!acceptToken(SELECT)) return null;

		SwitchStatement<SwitchCaseStatement<Statement>> result = new SwitchStatement<>();
		if (optionalToken(LEFT_PAREN)) {
			result.expression(parseExpression());
			acceptToken(RIGHT_PAREN);
		}
		acceptToken(SEMICOLON);

		while (optionalToken(WHEN)) {
			SwitchCaseStatement<Statement> when = new SwitchCaseStatement<>();
			if (acceptToken(LEFT_PAREN)) {
				when.expression(parseCommaExpression(COMMA));
				acceptToken(RIGHT_PAREN);
			}

			CompositeStatement<Statement> cs = new CompositeStatement<>();
			cs.statementList(List.of(parseStatement()));
			when.statements(cs);

			result.caseStatements().add(when);
		}

		if (optionalToken(OTHERWISE)) {
			SwitchCaseStatement<Statement> when = new SwitchCaseStatement<>();
			CompositeStatement<Statement> cs = new CompositeStatement<>();
			cs.statementList(List.of(parseStatement()));
			when.statements(cs);
			result.caseStatements().add(when);
		}

		acceptToken(END);
		acceptToken(SEMICOLON);
		return result;
	}

	private Statement parseDoStatement() {
		if (!acceptToken(DO)) return null;

		PliDoStatement result = new PliDoStatement();
		switch (token.kind()) {
			case SEMICOLON:
			case LOOP:
			case IDENTIFIER:
				result.reference(parseIdentifier());
				acceptToken(EQ);
				result.initialExpr(parseExpression());
			case TO:
			case BY: {
				boolean toIsFirst = token.kind() == TO;
				nextToken();
				Expression expr1 = parseExpression();
				Expression expr2 = null;
				if (optionalAnyOf(TO, BY)) {
					expr2 = parseExpression();
				}
				result.toExpr(toIsFirst ? expr1 : expr2);
				result.byExpr(toIsFirst ? expr2 : expr1);
			}
			case WHILE:
			case UNTIL:
				if (token.kind().isAnyOf(WHILE, UNTIL)) {
					boolean whileIsFirst = token.kind() == WHILE;
					nextToken();
					Expression expr1 = parseExpressionInParenthesis();
					Expression expr2 = null;
					if (optionalAnyOf(WHILE, UNTIL)) {
						expr2 = parseExpressionInParenthesis();
					}
					result.whileExpr(whileIsFirst ? expr1 : expr2);
					result.untilExpr(whileIsFirst ? expr2 : expr1);
				}
		}
		ignoreUntil(SEMICOLON, EOF);
		acceptToken(SEMICOLON);

		// parse until "END;"
		result.statementList(parseBlock(null));

		return result;
	}


	private Statement parseCallStatement() {
		if (!acceptToken(CALL)) return null;

		ExpressionStatement result = new ExpressionStatement();
		Identifier name = parseIdentifier();
//		result.expression(parseFunctionCall(name));

		ignoreUntil(SEMICOLON, EOF);
		acceptToken(SEMICOLON);
		return result;
	}

	private Statement parseReturnStatement() {
		ReturnStatement result = new ReturnStatement();
		result.expression(parseExpression());
		return result;
	}

	private Statement parseExitStatement() {
		ReturnStatement result = new ReturnStatement();
		result.expression(parseExpression());
		return result;
	}

	private Statement parseStopStatement() {
		if (!acceptToken(STOP)) return null;
		PliStopStatement result = new PliStopStatement();
		acceptToken(SEMICOLON);
		return result;
	}


	// -- program organization statements --

	private Statement parseProcedureDeclaration(Identifier name) {
		if (!acceptToken(PROCEDURE)) return null;

		PliProcedure result = new PliProcedure();
		result.name(name);
		if (optionalToken(OPTIONS)) {
			acceptToken(LEFT_PAREN);
			if (optionalToken(MAIN)) {
				result.main(true);
			}
			acceptToken(RIGHT_PAREN);
		}
		acceptToken(SEMICOLON);

		// parse until "END procedureName;"
		result.statementList(parseBlock(name));

		return result;
	}

	public Statement parseBeginBlock(Identifier name) {
		if (!acceptToken(BEGIN)) return null;

		PliBeginBlock result = new PliBeginBlock();
		result.name(name);
		if (optionalToken(OPTIONS)) {
			acceptToken(LEFT_PAREN);
			acceptToken(RIGHT_PAREN);
		}
		acceptToken(SEMICOLON);

		// parse until "END procedureName;"
		result.statementList(parseBlock(name));

		return result;
	}

	private Statement parsePackageDeclaration(Identifier name) {
		if (!acceptToken(PACKAGE)) return null;

		PliPackage result = new PliPackage();
		result.name(name);
		if (optionalToken(EXPORTS)) {
			acceptToken(LEFT_PAREN);
			List<Identifier> list = new ArrayList<>();
			do {
				Identifier export = parseIdentifier();
				list.add(export);
			} while (optionalToken(COMMA));
			result.exports(list);
			acceptToken(RIGHT_PAREN);
		}
		acceptToken(SEMICOLON);

		// parse until "END procedureName;"
		result.statementList(parseBlock(name));

		return result;
	}

	// -- type definition statements --

	private Statement parseDefineStatement() {
		if (!acceptToken(DEFINE)) return null;

		switch (token.kind()) {
			case ALIAS:     return parseDefineAliasStatement();
			case ORDINAL:   return parseDefineOrdinalStatement();
			case STRUCTURE: return parseDefineStructureStatement();
		}
		return null;
	}

	private Statement parseDefineAliasStatement() {
		if (!acceptToken(ALIAS)) return null;

		DefineAliasStatement result = new DefineAliasStatement();
		result.name(parseIdentifier());
		ignoreUntil(SEMICOLON, EOF);
		acceptToken(SEMICOLON);
		return result;
	}

	private Statement parseDefineOrdinalStatement() {
		if (!acceptToken(ORDINAL)) return null;

		DefineOrdinalStatement result = new DefineOrdinalStatement();
		result.name(parseIdentifier());
		ignoreUntil(SEMICOLON, EOF);
		acceptToken(SEMICOLON);
		return result;
	}

	private Statement parseDefineStructureStatement() {
		if (!acceptToken(STRUCTURE)) return null;

		DefineStructureStatement result = new DefineStructureStatement();
		result.name(parseIdentifier());
		ignoreUntil(SEMICOLON, EOF);
		acceptToken(SEMICOLON);
		return result;
	}

	// -- Data declaration statements --

	private Statement parseDeclareStatement() {
		if (!acceptToken(DECLARE)) return null;

		CompositeStatement<VariableDeclaration> result = new CompositeStatement<>();
		do {
			result.add(parseVariableDeclaration());
		} while (optionalToken(COMMA));

		ignoreUntil(SEMICOLON, EOF);
		acceptToken(SEMICOLON);
		return result;
	}

	private PliVariableDeclaration parseVariableDeclaration() {
		PliVariableDeclaration result = new PliVariableDeclaration();

		// optional level.
		if (optionalToken(NUMBER_LITERAL)) {
			result.level(parseNumberLiteral());
		}

		result.name(parseIdentifier());
		parseOptionalArrayDimensions(result);

		boolean isAttributeToken = true;
		while (isAttributeToken) {
			switch (token.kind()) {
//				case TYPE:  nextToken();
//					result = new DeclareStatement();
//					result.type(parseIdentifier());
//					break;

				case INITIAL:
					nextToken();
					acceptToken(LEFT_PAREN);
					result.initializer(parseExpression());
					acceptToken(RIGHT_PAREN);
					break;

				case NONVARYING:
				case VARYING:
				case VARYINGZ:
					result.varying(token.lexeme());
					nextToken();
					break;

				case CHARACTER:
				case WIDECHAR:
				case BIT:
				case GRAPHIC:
					result.type(parseIdentifier());
					// optional length.
					if (optionalToken(LEFT_PAREN)) {
						result.length(parseExpression());
						acceptToken(RIGHT_PAREN);
					}
					break;

				case BINARY:
				case DECIMAL:
				case FIXED:
				case FLOAT:
				case REAL:
				case COMPLEX:
					result.type(parseIdentifier());
					break;

				default:
					isAttributeToken = false;
			}
		}

		return result;
	}

	/** If the DIMENSION keyword is omitted, the dimension must immediately
	 * follow the name (or the parenthesized list of names)
	 */
	private void parseOptionalArrayDimensions(PliVariableDeclaration result) {
		if (optionalToken(LEFT_PAREN)) {
			List<Expression> dimensions = new ArrayList<>();
			do {
				dimensions.add(parseExpression());
			} while (optionalToken(COMMA));
			acceptToken(RIGHT_PAREN);
			result.dimensions(dimensions);
		}
	}

	// -- preprocessor statements --

	private Statement parsePreprocessorStatement() {
		if (!acceptToken(PERCENT)) return null;

		switch (token.kind()) {
			case INCLUDE:       return parseIncludeStatement();
			case PAGE:          return parsePageStatement();
			default:
				String message = String.format("Unsupported token %s to identify preprocessor statement type.", token);
				syntaxError(message);
		}
		return null;
	}

	private Statement parseIncludeStatement() {
		if (!acceptToken(INCLUDE))  return null;

		IncludeStatement statement = new IncludeStatement();
		switch (token.kind()) {
			case STRING_LITERAL:    statement.fileName(parseStringLiteral().value());   break;
			case IDENTIFIER:        Identifier member = parseIdentifier();
				if (token.kind().isAnyOf(LEFT_PAREN)) {
					acceptToken(LEFT_PAREN);
					Identifier ddName = member;
					statement.ddName(ddName);
					member = parseIdentifier();
					acceptToken(RIGHT_PAREN);
				}
				statement.member(member);
		}

		acceptToken(SEMICOLON);
		return statement;
	}

	private Statement parsePageStatement() {
		if (!acceptToken(PAGE))  return null;

		PageStatement result = new PageStatement();
		acceptToken(SEMICOLON);
		return result;
	}

	// -- function-like statements --

	// -- file statements --

	private Statement parseOpenStatement() {
		if (!acceptToken(OPEN)) return null;

		CompositeStatement<Statement> result = new CompositeStatement<>();
		do {
			result.statementList().add(parseOpenFileStatement());
		} while (optionalToken(COMMA));
		return result;
	}

	private Statement parseOpenFileStatement() {
		if (!acceptToken(FILE)) return null;

		OpenFileStatement result = new OpenFileStatement();
		acceptToken(LEFT_PAREN);
		result.name(parseIdentifier());
		acceptToken(RIGHT_PAREN);

		while (token.kind().isAnyOf(STREAM, RECORD, INPUT, OUTPUT, UPDATE, SEQUENTIAL, DIRECT, BUFFERED,
				UNBUFFERED, KEYED, PRINT, TITLE, LINESIZE, PAGESIZE)) {
			if (token.kind().isAnyOf(TITLE, LINESIZE, PAGESIZE)) {
				Token<PliTokenKind> optionToken = token;
				nextToken();
				acceptToken(LEFT_PAREN);
				Expression expr = parseExpression();
				acceptToken(RIGHT_PAREN);
				switch (optionToken.kind()) {
					case TITLE:     result.titleExpr(expr);
					case LINESIZE:  result.lineSizeExpr(expr);
					case PAGESIZE:  result.pageSizeExpr(expr);
				}
			} else {
				result.options().add(token.kind().lexeme());
				nextToken();
			}
		}

		return result;
	}

	private Statement parseCloseFileStatement() {
		if (!acceptToken(FILE)) return null;

		CloseFileStatement result = new CloseFileStatement();
		acceptToken(LEFT_PAREN);
		result.name(parseIdentifier());
		acceptToken(RIGHT_PAREN);

		if (optionalToken(ENVIRONMENT)) {
			acceptToken(LEFT_PAREN);
			if (acceptAnyOf(LEAVE, REREAD)) {
				result.option(lastToken.lexeme());
			}
			acceptToken(RIGHT_PAREN);
		}
		return result;
	}

	private Statement parseDisplayStatement() {
		if (!acceptToken(DISPLAY)) return null;
		DisplayStatement result = new DisplayStatement();
		result.expression(parseExpression());
		acceptToken(SEMICOLON);
		return result;
	}

	// -- Condition / On statement --

	private Statement parseOnStatement() {
		if (!acceptToken(ON)) return null;
		OnStatement result = new OnStatement();
		result.condition(parseOnCondition());
		if (isKind(BEGIN)) {
			result.action(parseBeginBlock(null));
		} else {
			if (optionalToken(SYSTEM)) {
				acceptToken(SEMICOLON);
			} else {
				result.action(parseStatement());
			}
		}
		return result;
	}

	private String parseOnCondition() {
		if (optionalAnyOf(ERROR, FINISH, ANYCONDITION)) {
			return lastToken.lexeme();
		} else {
			syntaxError("unsupported on condition");
			return null;
		}
	}


	// -- expression statement --

	private Statement parseAssignmentStatement() {
		List<Expression> targets = parseExpressionList(COMMA);

		return null;
	}

	private Statement parseExpressionStatement() {
		ExpressionStatement result = new ExpressionStatement();
		result.expression(parseExpression());
		ignoreUntil(SEMICOLON, EOF);
		acceptToken(SEMICOLON);
		return result;
	}

	// ====== expressions ======

	private Expression parseExpression() {
//		return parseAssignmentExpression();
//	}
//
//	// -- assignment expression --
//
//	public Expression parseAssignmentExpression() {
		Expression expr = parseBinaryExpression();
		if (optionalAnyOf(EQ, PLUS_EQ, MINUS_EQ, STAR_EQ, SLASH_EQ, STAR_STAR_EQ, AND_EQ, BAR_EQ, BAR_BAR_EQ)) {
			String operator = lastToken.lexeme();
			Expression rhs = parseBinaryExpression();
			return new Assignment(expr, rhs, operator);
		} else {
			return expr;
		}
	}

	// -- binary expression --

	public Expression parseBinaryExpression() {
		Expression expr = parseUnaryExpression();
		if (token.kind().tag() == BINARY_OPERATOR) {
			return parseBinaryExpressionLoop(expr);
		} else {
			return expr;
		}
	}

	// TODO: binary operator priority.
	private Expression parseBinaryExpressionLoop(Expression expr) {
		Stack<Token<PliTokenKind>> operatorStack = new Stack<>();
		while (token.kind().tag() == BINARY_OPERATOR) {
			Token<PliTokenKind> operator = token;
			nextToken();
			Expression rightOperand = parseUnaryExpression();
			// if this operator is higher priority than previous operators in stack
			if (!operatorStack.isEmpty()
					&& operatorStack.peek().kind().priority().value >= operator.kind().priority().value) {

			}
			// combination of binary operations
			if (token.kind().tag() == BINARY_OPERATOR) {
				Token<PliTokenKind> operator2nd = token;
				nextToken();
				if (operator.kind().priority().value >= operator2nd.kind().priority().value) {
					expr = new BinaryExpression(expr, operator.lexeme(), rightOperand);
					operator = operator2nd;
					rightOperand = parseUnaryExpression();
				} else {
					rightOperand = new BinaryExpression(expr, operator2nd.lexeme(), parseBinaryExpression());
				}
			}
			expr = new BinaryExpression(expr, operator.lexeme(), rightOperand);
		}
		return expr;
	}

	// -- unary expression (only optional prefix. no optional postfix unary) --

	public Expression parseUnaryExpression() {
//		return parsePrefixUnaryExpression();
//	}
//
//	public Expression parsePrefixUnaryExpression() {
		if (optionalAnyOf(PLUS, MINUS, NOT)) {
			PrefixUnaryExpression result = new PrefixUnaryExpression();
			result.operator(lastToken.lexeme());
			result.expression(parsePrimaryExpression());
			return result;
		} else {
			return parsePrimaryExpression();
		}
	}

	// -- primary expression --

	public Expression parsePrimaryExpression() {
		switch (token.kind()) {
			case STRING_LITERAL:    return parseStringLiteral();
			case NUMBER_LITERAL:    return parseNumberLiteral();
			case LEFT_PAREN:		return parseParenExpression();
			case IDENTIFIER:		return parseComponentSelect();
			case STAR: nextToken();	return new StarExpression();
			default:
				syntaxError("not implemented yet in parsePrimaryExpression");
				return null;
		}
	}

	private Expression parseComponentSelect() {
		Expression result = parseIdentifier();
		result = parseArrayAccess(result);
		result = parseFunctionCall(result);
		while (optionalAnyOf(DOT, LOCATOR_POINTER, LOCATOR_HANDLE)) {
			String selector = lastToken.lexeme();
			Identifier id = parseIdentifier();
			result = new ComponentSelect().expression(result).identifier(id).selector(selector);
			result = parseArrayAccess(result);
			result = parseFunctionCall(result);
		}
		return result;
	}

	private Expression parseArrayAccess(Expression expr) {
		Expression result = expr;
		// the 1st parenthesized expression could be array access or function call.
		if (optionalToken(LEFT_PAREN)) {
			Expression subscripts = parseCommaExpression(COMMA);
			acceptToken(RIGHT_PAREN);
			result = new ArrayAccess(result, subscripts);
		}
		return result;
	}

	private Expression parseFunctionCall(Expression expr) {
		Expression result = expr;
		// the second parenthesized expression must be arguments.
		if (optionalToken(LEFT_PAREN)) {
			CommaExpression arguments = parseCommaExpression(COMMA);
			acceptToken(RIGHT_PAREN);
			result = new FunctionCall().functionSelect(result).arguments(arguments.expressions());
		}
		return result;
	}

	public ParenthesizedExpression parseParenExpression() {
		acceptAnyOf(LEFT_PAREN);
		Expression expr = parseExpression();
		acceptAnyOf(RIGHT_PAREN);
		return new ParenthesizedExpression().expression(expr);
	}

	public Expression parseExpressionInParenthesis() {
		acceptAnyOf(LEFT_PAREN);
		Expression expr = parseExpression();
		acceptAnyOf(RIGHT_PAREN);
		return expr;
	}

	private Identifier parseIdentifier() {
		if (token.kind().isAnyOf(IDENTIFIER) || token.kind().tag() == PliTokenKindTag.KEYWORD) {
			nextToken();
		}
		return new Identifier().id(lastToken.lexeme());
	}

	private StringLiteral parseStringLiteral() {
		acceptToken(STRING_LITERAL);
		return new StringLiteral(lastToken.lexeme());
	}

	private NumberLiteral parseNumberLiteral() {
		acceptToken(NUMBER_LITERAL);
		return new NumberLiteral();
	}

	// -- common expression parsing --

	protected List<Expression> parseExpressionList(PliTokenKind separator) {
		List<Expression> list = new ArrayList<>();
		do {
			list.add(parseExpression());
		} while (optionalToken(separator));
		return list;
	}

	protected CommaExpression parseCommaExpression(PliTokenKind separator) {
		CommaExpression result = new CommaExpression();
		result.expressions(parseExpressionList(separator));
		return result;
	}



	// == token reader customization ==

	@Override
	protected boolean isSpecialToken(Token<PliTokenKind> t) {
		return t.kind().isAnyOf(WHITE_SPACES, NEW_LINE, BLOCK_COMMENT);
	}
}
