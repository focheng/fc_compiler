package fc.compiler.language.cobol;

import fc.compiler.common.ast.AstNode;
import fc.compiler.common.ast.Expression;
import fc.compiler.common.ast.ExpressionBase;
import fc.compiler.common.ast.Statement;
import fc.compiler.common.ast.expression.*;
import fc.compiler.common.ast.statement.*;
import fc.compiler.common.parser.Parser;
import fc.compiler.common.parser.ParserBase;
import fc.compiler.common.parser.ParserRegistry;
import fc.compiler.common.parser.TokenReader;
import fc.compiler.common.token.Token;
import fc.compiler.language.cobol.ast.CharacterString;
import fc.compiler.language.cobol.ast.CobolProgram;
import fc.compiler.language.cobol.ast.clause.*;
import fc.compiler.language.cobol.ast.division.*;
import fc.compiler.language.cobol.ast.statement.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static fc.compiler.common.token.TokenKind.*;
import static fc.compiler.language.cobol.CobolTokenKind.*;

/**
 * Code Hiarchical Organizations
 * IDENTIFICATION  ENVIRONMENT     DATA            PROCEDURE
 *   DIVISION       DIVISION       DIVISION        DIVISION
 * ------------------------------------------------------------
 *                 Sections        Sections        Sections
 * Paragraphs      Paragraphs                      Paragraphs
 * Entries         Entries         Entries         Sentences
 * Clauses         Clauses         Clauses         Statements
 *                 Phrases         Phrases         Phrases
 *
 * Entry is a series of clauses that ends with a separator period.
 * Clause is an ordered set of consecutive COBOL character-strings that specifies an attribute of an entry.
 * Sentence is a sequence of one or more statements that ends with a separator period.
 * Statement specifies an action to be taken by the program.
 * Phrases: Each clause or statement can be subdivided into smaller units called phrases.
 *
 * 4 categories of statements:
 * - imperative statement: either specifies an unconditional action to be taken by the program,
 *                      or is a conditional statement terminated by its explicit scope terminator.
 *      - Arithmetic: COMPUTE, ADD, ..., DIVIDE
 *      - Data movement: MOVE, SET, STRING, UNSTRING,
 *      - Input-Output: READ, WRITE,
 *      - Ending: STOP RUN
 *      - Procedure-branching: GO TO, PERFORM, CONTINUE
 *      - Program or method linkage: CALL
 * - conditional statement: specifies that the truth value of a condition is to be determined
 *              and that the subsequent action of the object program is dependent on this truth value.
 *      - Decision: IF, EVALUATE
 *      - Arithmetic: (COMPUTE | ADD ~ DIVIDE) ... [NOT] ON SIZE ERROR
 *      - Data movement: (STRING | UNSTRING) ... ON OVERFLOW
 *      - Input-output: READ ... AT END
 *      - Program or method linkage: CALL ... ON OVERFLOW
 * - delimited scope statement:  uses an explicit scope terminator to turn a conditional statement
 *              into an imperative statement.
 *      - Explicit scope terminator: END-IF, END-EVALUATE, END-PERFORM
 *      - Implicit scope terminator: is a separator period that terminates the scope of
 *                  all previous statements not yet terminated at the end of any sentence.
 * - compiler-directing statement: causes the compiler to take a specific action during compilation time.
 *      e.g. copy statement
 * @author FC
 */
@Slf4j
public class CobolParser extends ParserBase {
	CobolCompilerOptions options;

	public static ParserRegistry initRegistry() {
		ParserRegistry map = new ParserRegistry();

		map.put(LINE_TERMINATOR, ParserBase::ignore);
		map.put(WHITE_SPACES, ParserBase::ignore);
		map.put(LINE_COMMENT, ParserBase::ignore);

		map.put(IF, CobolParser::parseIfStatement);
		map.put(EVALUATE, CobolParser::parseEvaluateStatement);
		map.put(CONTINUE, CobolParser::parseContinueStatement);
		map.put(GO, CobolParser::parseGotoStatement);
		map.put(EXIT, CobolParser::parseExitStatement);
		map.put(CALL, CobolParser::parseCallStatement);
		map.put(COMPUTE, CobolParser::parseComputeStatement);

		map.put(PIC, CobolParser::parsePictureClause);
		map.put(PICTURE, CobolParser::parsePictureClause);
		map.put(VALUE, CobolParser::parseValueClause);
		map.put(DISPLAY, CobolParser::parseDisplayClause);

		map.put(IDENTIFIER, CobolParser::parseIdentifier);
		return map;
	}

	public static CobolProgram parseCompilationUnit(TokenReader reader, ParserRegistry registry) {
		return new CobolProgram()
				.idDivision(parseIdDivision(reader, registry))
				.environmentDivision(parseEnvironmentDivision(reader, registry))
				.dataDivision(parseDataDivision(reader, registry))
				.procedureDivision(parseProcedureDivision(reader, registry))
				;
	}

	public static IdDivision parseIdDivision(TokenReader reader, ParserRegistry registry) {
		// ( "IDENTIFICATION" | "ID" ) "DIVISION" "."
		reader.acceptAnyOf(IDENTIFICATION, ID);
		reader.acceptAnyOf(DIVISION);
		reader.acceptAnyOf(SEPARATOR_PERIOD);

		// "PROGRAM-ID" [ "." ] program-name [ [ "IS" ] "INITIAL" [ "PROGRAM" ] ] [ "." ]
		reader.acceptAnyOf(PROGRAM_ID);
		reader.acceptAnyOf(SEPARATOR_PERIOD);
		Identifier programName = parseIdentifier(reader, registry);
		reader.acceptAnyOf(SEPARATOR_PERIOD);

		// ( [ "AUTHOR" [ "." ] { comment-entry }* ] )

//		if (isNewDivision(reader))
		return new IdDivision().programName(programName);
	}

	/** [ "ENVIRONMENT" "DIVISION" "." environment-division-content ] */
	public static EnvironmentDivision parseEnvironmentDivision(TokenReader reader, ParserRegistry registry) {
		if (!reader.optionalNextTokens(ENVIRONMENT, DIVISION, SEPARATOR_PERIOD)) {
			return null;
		}

		return new EnvironmentDivision()
				.configurationSection(parseConfigurationSection(reader, registry))
				.inputOutputSection(parseInputOutputSection(reader, registry));
	}

	/** configuration-section	=	"CONFIGURATION" "SECTION" "." configuration-section-paragraphs */
	public static ConfigurationSection parseConfigurationSection(TokenReader reader, ParserRegistry registry) {
		if (!reader.optionalNextTokens(CONFIGURATION, SECTION, SEPARATOR_PERIOD)) {
			return null;
		}
		return new ConfigurationSection();
	}

	/** input-output-section	=	"INPUT-OUTPUT" "SECTION" "." [ file-control-paragraph ] [ i-o-control-paragraph ] */
	public static InputOutputSection parseInputOutputSection(TokenReader reader, ParserRegistry registry) {
		if (!reader.optionalNextTokens(INPUT_OUTPUT, SECTION, SEPARATOR_PERIOD)) {
			return null;
		}
		return new InputOutputSection();
	}

	/** 	[ "DATA" "DIVISION" "." data-division-content ] */
	public static DataDivision parseDataDivision(TokenReader reader, ParserRegistry registry) {
		if (!reader.optionalNextTokens(DATA, DIVISION, SEPARATOR_PERIOD)) {
			return null;
		}

		return new DataDivision()
				.fileSection(parseFileSection(reader, registry))
				.workingStorageSection(parseWorkingStorageSection(reader, registry))
				.linkageSection(parseLinkageSection(reader, registry));
	}

	/** [ "FILE" "SECTION" "." { file-and-sort-description-entry { record-description-entry }+ }* ] */
	public static FileSection parseFileSection(TokenReader reader, ParserRegistry registry) {
		if (!reader.optionalNextTokens(FILE, SECTION, SEPARATOR_PERIOD)) {
			return null;
		}
		return new FileSection();
	}

	/** [ "WORKING-STORAGE" "SECTION" "." { ( record-description-entry | data-item-description-entry ) }* ] */
	public static WorkingStorageSection parseWorkingStorageSection(TokenReader reader, ParserRegistry registry) {
		if (!reader.optionalNextTokens(WORKING_STORAGE, SECTION, SEPARATOR_PERIOD)) {
			return null;
		}

		WorkingStorageSection wsSection = new WorkingStorageSection();
		DataDescriptionEntry stmt = parseDataDescriptionEntry(reader, registry);
		wsSection.variableDeclarations().add(stmt);

		return wsSection;
	}

	/**  ["LINKAGE" "SECTION" "." { ( record-description-entry | data-item-description-entry ) }* ] */
	public static LinkageSection parseLinkageSection(TokenReader reader, ParserRegistry registry) {
		if (!reader.optionalNextTokens(LINKAGE, SECTION, SEPARATOR_PERIOD)) {
			return null;
		}
		return new LinkageSection();
	}

	/**
	 * data-item-description-entry or 77-level description-entry = data-description-entry
	 * record-description-entry	     = data-description-entry
	 * data-description-entry
	 *  defining data-names          = level-number [ ( data-name | "FILLER" ) ] data-description-entry-clauses "."
	 *  defining renaming data-names = "66" data-name renames-clause "."
	 *  defining condition-names	 = "88" condition-name condition-value-clause "."
	 */
	public static DataDescriptionEntry parseDataDescriptionEntry(TokenReader reader, ParserRegistry registry) {
		if (!acceptLevelNumber(reader)) return null;

		Identifier dataName = parseIdentifier(reader, registry); // includes FILLER
		if (dataName != null) {
			log.info("data-name is " + dataName);
		}
		DataDescriptionEntry variable = new DataDescriptionEntry().dataName(dataName);

		while (reader.token().kind() != SEPARATOR_PERIOD) {
			Parser parser = registry.get(reader.token().kind());
			if (parser != null) {
				AstNode node = parser.parse(reader, registry);
				if (node instanceof PictureClause picString) {
					variable.pictureClause(picString);
				} else if (node instanceof ValueClause value) {
					variable.valueClause(value);
				} else {
					syntaxError(reader, "");
				}
			}
			reader.nextToken();
		}
		reader.acceptAnyOf(SEPARATOR_PERIOD);

		return variable;
	}

	private static boolean acceptLevelNumber(TokenReader reader) {
		Token levelNumber = reader.token();
		if (!levelNumber.kind().equals(NUMBER_LITERAL)
				|| levelNumber.lexeme().length() != 2) {
			return false;
		}

		log.info("levelNumber is " + levelNumber);
		int n = Integer.parseInt(levelNumber.lexeme());
		if (!isValidLevelNumber(n)) {
			syntaxError(reader, "Invalid level number: " + n);
			return false;
		}

		reader.nextToken();
		return true;
	}

	public static boolean isValidLevelNumber(int n) {
		return (n > 0 && n < 50) || n == 66 || n == 77 || n == 88;
	}

	/** picture-clause = ( "PICTURE" | "PIC" ) [ "IS" ] picture-string
	 *  picture-string = currency? (picchar+ repeat?)+ (punctuation (picchar+ repeat?)+)*
	 */
	public static PictureClause parsePictureClause(TokenReader reader, ParserRegistry registry) {
		reader.acceptAnyOf(PICTURE, PIC);
		reader.optionalAnyOf(IS);
		CharacterString picString = parseCharacterString(reader, registry);
		return new PictureClause().picString(picString);
	}

	public static CharacterString parseCharacterString(TokenReader reader, ParserRegistry registry) {
		StringBuilder sb = new StringBuilder();
		Token t = null;
		while ((t = reader.optionalAnyOfAndReturn(IDENTIFIER, NUMBER_LITERAL, LEFT_PAREN, RIGHT_PAREN)) != null) {
			sb.append(t.lexeme());
		}
		return new CharacterString().format(sb.toString());
	}

	public static ValueClause parseValueClause(TokenReader reader, ParserRegistry registry) {
		if (reader.optionalAnyOf(VALUE)) {
			Token t = reader.acceptAnyOfAndReturn(NUMBER_LITERAL, ZERO, ZEROS, ZEROES, SPACE, SPACES);
			if (t != null) {
				return new ValueClause().value(t.lexeme());
			}
		}
		return null;
	}

	/**
	 * procedure-division = "PROCEDURE" "DIVISION" [ "USING" { data-name }+ ] "."
	 *                      [ "DECLARATIVES" "." { section-header "." use-statement "." paragraphs }+ "END" "DECLARATIVES" "." ]
	 *                      sections
	 * procedure-division = "PROCEDURE" "DIVISION" [ "USING" { data-name }+ ] "."
	 *                      paragraphs
	 */
	public static ProcedureDivision parseProcedureDivision(TokenReader reader, ParserRegistry registry) {
		// ignore tokens until "PROCEDURE" "DIVISION"
		while (!reader.isKind(PROCEDURE, DIVISION)) {
			reader.nextToken();
		}

		ProcedureDivision division = new ProcedureDivision();
		if (reader.optionalNextTokens(PROCEDURE, DIVISION, SEPARATOR_PERIOD)) {
			// do nothing
		} else if (reader.optionalNextTokens(PROCEDURE, DIVISION, USING)) {
			division.usingClause(parseUsingClause(reader, registry));
		} else {
			return null;
		}

		while (!reader.optionalAnyOf(EOF) && !reader.optionalNextTokens(END, PROGRAM)) {
			Parser parser = registry.get(reader.token().kind());
			if (parser != null) {
				AstNode node = parser.parse(reader, registry);
				if (node instanceof Statement stmt) {
					division.statements().add(stmt);
				} else {
					syntaxError(reader, "");
				}
			}
			reader.nextToken();
		}
		return division;
	}

	public static UsingClause parseUsingClause(TokenReader reader, ParserRegistry registry) {
		UsingClause usingClause = new UsingClause();
		while (reader.token().kind() == IDENTIFIER) {
			usingClause.parameters().add(parseIdentifier(reader, registry));
		}
		return usingClause;
	}

	public static DisplayClause parseDisplayClause(TokenReader reader, ParserRegistry registry) {
		reader.acceptAnyOf(DISPLAY);
		Token token = reader.acceptAnyOfAndReturn(STRING_LITERAL);
		return new DisplayClause().value(token.lexeme());
	}

	public static boolean isNewDivision(TokenReader reader) {
		Token token1 = reader.peekToken(1);
		Token token2 = reader.peekToken(2);
		return token2.kind() == DIVISION
				&& (token1.kind() == ENVIRONMENT || token1.kind() == DATA || token1.kind() == PROCEDURE);
	}


	// == sentences = statement-list "." ==


	// == statement-list = { statement }+ ==
	// statement = ( if-statement | evaluate-statement | ... | exit-statement )
	/**
	 * if-statement = "IF" condition "THEN"? ( statement+ | "NEXT" "SENTENCE" )
	 *                 [ "ELSE"              ( statement+ | "NEXT" "SENTENCE" ) ]
	 *                 [ "END-IF" ]
	 */
	public static IfStatement parseIfStatement(TokenReader reader, ParserRegistry registry) {
		if (!reader.optionalAnyOf(IF)) return null;

		IfStatement ifstmt = new IfStatement()
				.condition(parseConditionalExpression(reader, registry));
		reader.optionalAnyOf(THEN);
		if (reader.optionalNextTokens(NEXT, SENTENCE)) {
		} else {
			ifstmt.thenStatement(parseStatement(reader, registry));
		}
		if (reader.optionalAnyOf(ELSE)) {
			if (reader.optionalNextTokens(NEXT, SENTENCE)) {
			} else {
				ifstmt.elseStatement(parseStatement(reader, registry));
			}
		}
		reader.optionalAnyOf(END_IF);
		return ifstmt;
	}

	/**
	 * evaluate-statement:
	 *  "EVALUATE" evaluate-select	 { "ALSO" evaluate-select }*
	 *    { { "WHEN" evaluate-phrase { "ALSO" evaluate-phrase }* }+ statement-list }+
	 *    [ "WHEN" "OTHER" statement-list ] [ "END-EVALUATE" ]
	 * evaluate-select: ( identifier | literal | arithmetic-expression | conditional-expression | boolean-literal )
	 * evaluate-phrase:	( "ANY" | conditional-expression | boolean-literal | [ "NOT" ] evaluate-value [ ( "THROUGH" | "THRU" ) evaluate-value ] )
	 * evaluate-value: ( identifier | literal | arithmetic-expression )
	 */
	public static EvaluateStatement parseEvaluateStatement(TokenReader reader, ParserRegistry registry) {
		if (!reader.optional(EVALUATE)) return null;
		return new EvaluateStatement();
	}

	public static Statement parseExitStatement(TokenReader reader, ParserRegistry registry) {
		if (!reader.optional(EXIT)) return null;

		return new ExpressionStatement().expression(Identifier.get("exit"));
	}

	public static Statement parseStopStatement(TokenReader reader, ParserRegistry registry) {
		if (!reader.optional(STOP)) return null;

		if (reader.optional(RUN)) {
		} else if (reader.isKindAnyOf(NUMBER_LITERAL, STRING_LITERAL)) {
			Literal literal = parseLiteral(reader, registry);
		}

		return new ExpressionStatement().expression(Identifier.get("stop"));
	}

	public static ContinueStatement parseContinueStatement(TokenReader reader, ParserRegistry registry) {
		if (!reader.optional(CONTINUE)) return null;

		return new ContinueStatement();
	}

	/** goto-statement: (unconditional-goto | conditional-goto | altered-goto)
	 * unconditional-goto: 'GO' 'TO'? procedure-name
	 */
	public static GotoStatement parseGotoStatement(TokenReader reader, ParserRegistry registry) {
		if (!reader.optionalAnyOf(GO)) return null;
		reader.optionalAnyOf(TO);
		Identifier label = parseIdentifier(reader, registry);
		return new GotoStatement().label(label);
	}

	public static ExpressionStatement parseCallStatement(TokenReader reader, ParserRegistry registry) {
		if (!reader.optionalAnyOf(CALL)) return null;

		FunctionCall call = new FunctionCall();
		call.functionSelect(parseIdentifierOrLiteral(reader, registry));

		if (reader.optionalAnyOf(USING)) {
			call.arguments(parseArguments(reader, registry));
		}

		reader.optionalAnyOf(END_CALL);
		return new ExpressionStatement().expression(call);
	}

	public static List<Expression> parseArguments(TokenReader reader, ParserRegistry registry) {
		throw new RuntimeException("Not Implemented");
	}

	public static ExpressionStatement parseComputeStatement(TokenReader reader, ParserRegistry registry) {
		if (!reader.optionalAnyOf(COMPUTE)) return null;

		Identifier variable = parseIdentifier(reader, registry);
		boolean rounded = reader.optionalAnyOf(ROUNDED);
		reader.acceptAnyOf(EQUAL);
		Expression expr = parseArithmeticExpression(reader, registry);
		reader.optionalAnyOf(END_COMPUTE);
		Assignment assignment = new Assignment().expression(expr).variable(variable);
		return new ExpressionStatement().expression(assignment);
	}

	/** move-statement: 'MOVE' (identifier | literal) 'TO' identifier
	 *                | 'MOVE' ('CORRESPONDING' | 'CORR') identifier 'TO' identifier */
	public static ExpressionStatement parseMoveStatement(TokenReader reader, ParserRegistry registry) {
		if (!reader.optional(MOVE)) return null;

		boolean corresponding = reader.optionalAnyOf(CORRESPONDING, CORR);
		Expression expr = parseIdentifierOrLiteral(reader, registry);
		reader.accept(TO);
		Identifier variable = parseIdentifier(reader, registry);
		Assignment assignment = new Assignment().expression(expr).variable(variable);
		return new ExpressionStatement().expression(assignment);
	}

	// == expression = (arithmetic expression | conditional expression) ==

	/**-- arithmetic expression --
	 * - An identifier described as a numeric elementary item (including numeric functions)
	 * - A numeric literal
	 * - The figurative constant ZERO
	 * - Identifiers and literals, as defined in items 1, 2, and 3, separated by arithmetic operators
	 * - Two arithmetic expressions, as defined in items 1, 2, 3, or 4, separated by an arithmetic operator
	 * - An arithmetic expression, as defined in items 1, 2, 3, 4, or 5, enclosed in parentheses
	 * Any arithmetic expression can be preceded by a unary operator.
	 *
	 * arithmetic operators
	 * > Binary operator
	 *	 +  	Addition
	 *	 -  	Subtraction
	 *	 *  	Multiplication
	 *	 /  	Division
	 *	 ** 	Exponentiation
	 *
	 * > Unary operator
	 *	 + 	Multiplication by +1
	 *	 - 	Multiplication by -1
	 */
	public static Expression parseArithmeticExpression(TokenReader reader, ParserRegistry registry) {
		Token unaryOperator = reader.optionalAnyOfAndReturn(PLUS, MINUS);

		AstNode node = parseAny(reader, registry);
		return new ExpressionBase();
	}

	/** -- conditional expression = (simple condition | combined condition)
	 * Simple condition:
	 *        Relation condition         IF AGE < 18 THEN...
	 *      | Condition-name condition   IF IS-CHILD THEN...
	 *      | Class condition
	 *      | Sign condition
	 *      | Switch-status condition
	 * Combined condition:
	 *
	 */
	public static Expression parseConditionalExpression(TokenReader reader, ParserRegistry registry) {
		Expression leftOperand = parseUnaryExpression(reader, registry);
		boolean is = reader.optionalAnyOf(IS, ARE);
		String operator = parserRationalOperator(reader);
		Expression rightOperand = parseUnaryExpression(reader, registry);
		return new BinaryExpression().leftOperand(leftOperand).rightOperand(rightOperand).operator(operator);
	}

	/**
	 * Relational-Operators: (IS | ARE)? (
	 *         NOT? ('GREATER' 'THAN'? | '>' | 'LESS' 'THAN'? | '<' | 'EQUAL' TO'? | '=')
	 *         | '<>'
	 *         | 'GREATER' 'THAN'? 'OR' 'EQUAL' TO'?
	 *         | '>='
	 *         | 'LESS' THAN? 'OR' 'EQUAL' TO'??
	 *         | '<='
	 *     )
	 */
	protected static String parserRationalOperator(TokenReader reader) {
		String operator = null;
		if (reader.optionalAnyOf(NOT)) {
			if (reader.optionalAnyOf(GREATER)) {
				reader.optionalAnyOf(THAN);
				operator = "<=";    // not greater than = less than or equal to
			} else if (reader.optionalAnyOf(LESS)) {
				reader.optionalAnyOf(THAN);
				operator = ">=";    // not less than = less than or equal to
			} else if (reader.optionalAnyOf(EQUAL)) {
				reader.optionalAnyOf(TO);
				operator = "<>";    // not equals
			}
		} else if (reader.optionalAnyOf(GREATER)) {
			reader.optionalAnyOf(THAN);
			if (reader.optionalAnyOf(OR)) {
				reader.acceptAnyOf(EQUAL);
				reader.optionalAnyOf(TO);
				operator = ">=";
			} else {
				operator = ">";
			}
		} else if (reader.optionalAnyOf(LESS)) {
			reader.optionalAnyOf(THAN);
			if (reader.optionalAnyOf(OR)) {
				reader.acceptAnyOf(EQUAL);
				reader.optionalAnyOf(TO);
				operator = "<=";
			} else {
				operator = "<";
			}
		} else if (reader.optionalAnyOf(EQUAL)) {
			operator = "=";
		} else {
			Token t = reader.optionalAnyOfAndReturn(GT, GT_EQUAL, LT, LT_EQUAL, EQUAL, NOT_EQUAL);
			if (t != null) {
				operator = t.lexeme();
			}
		}
		return operator;
	}

	public static Expression parseIdentifierOrLiteral(TokenReader reader, ParserRegistry registry) {
		if (reader.isKind(IDENTIFIER)) {
			return parseIdentifier(reader, registry);
		} else if (reader.isKind(NUMBER_LITERAL, STRING_LITERAL)) {
			return parseLiteral(reader, registry);
		}
		return null; // syntaxError(reader, "Neither IDENTIFIER nor LITERAL");
	}
}
