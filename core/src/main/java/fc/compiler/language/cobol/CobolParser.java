package fc.compiler.language.cobol;

import fc.compiler.common.ast.expression.CompositeExpression;
import fc.compiler.common.ast.Expression;
import fc.compiler.common.ast.Statement;
import fc.compiler.common.ast.expression.*;
import fc.compiler.common.ast.expression.Identifier;
import fc.compiler.common.ast.expression.Literal;
import fc.compiler.common.ast.statement.*;
import fc.compiler.common.parser.StringTokenParserBase;
import fc.compiler.common.parser.StringTokenParserRegistry;
import fc.compiler.common.parser.StringTokenReader;
import fc.compiler.common.token.StringToken;
import fc.compiler.language.cobol.ast.CharacterString;
import fc.compiler.language.cobol.ast.CobolCompilationUnit;
import fc.compiler.language.cobol.ast.CobolProgram;
import fc.compiler.language.cobol.ast.clause.*;
import fc.compiler.language.cobol.ast.division.*;
import fc.compiler.language.cobol.ast.expression.RoundedIdentifier;
import fc.compiler.language.cobol.ast.statement.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import static fc.compiler.common.token.StringTokenKind.*;
import static fc.compiler.language.cobol.CobolTokenKind.*;


@Slf4j
public class CobolParser extends StringTokenParserBase {
	CobolCompilerOptions options;

	public static StringTokenParserRegistry initRegistry() {
		StringTokenParserRegistry map = new StringTokenParserRegistry();

		map.put(LINE_TERMINATOR, StringTokenParserBase::ignore);
		map.put(WHITE_SPACES, StringTokenParserBase::ignore);
		map.put(LINE_COMMENT, StringTokenParserBase::ignore);

		map.put(ASSIGN, CobolParser::parseAssignClause);

		map.put(IF, CobolParser::parseIfStatement);
		map.put(EVALUATE, CobolParser::parseEvaluateStatement);
		map.put(CONTINUE, CobolParser::parseContinueStatement);
		map.put(GO, CobolParser::parseGotoStatement);
		map.put(GOBACK, CobolParser::parseGoBackStatement);
		map.put(EXIT, CobolParser::parseExitStatement);

		map.put(CALL, CobolParser::parseCallStatement);
		map.put(PERFORM, CobolParser::parsePerformStatement);

		map.put(SET, CobolParser::parseSetStatement);
		map.put(MOVE, CobolParser::parseMoveStatement);

		map.put(COMPUTE, CobolParser::parseComputeStatement);
		map.put(ADD, CobolParser::parseAddStatement);
		map.put(SUBTRACT, CobolParser::parseSubtractStatement);
		map.put(MULTIPLY, CobolParser::parseMultiplyStatement);
		map.put(DIVIDE, CobolParser::parseDivideStatement);

		map.put(PIC, CobolParser::parsePictureClause);
		map.put(PICTURE, CobolParser::parsePictureClause);
		map.put(VALUE, CobolParser::parseValueClause);
		map.put(DISPLAY, CobolParser::parseDisplayClause);

		map.put(IDENTIFIER, CobolParser::parseIdentifier);
		return map;
	}

	public static CobolCompilationUnit parseCompilationUnit(StringTokenReader reader, StringTokenParserRegistry registry) {
		List<CobolProgram> list = parsePrograms(reader, registry);
		return new CobolCompilationUnit().cobolProgramList(list);
	}

	public static List<CobolProgram> parsePrograms(StringTokenReader reader, StringTokenParserRegistry registry) {
		List<CobolProgram> programs = new ArrayList<>();
		while (true) {
			if (!isIdDivision(reader))
				break;
			programs.add(parseProgram(reader, registry));
		}
		return programs;
	}

	public static CobolProgram parseProgram(StringTokenReader reader, StringTokenParserRegistry registry) {
		CobolProgram program = new CobolProgram()
				.idDivision(parseIdDivision(reader, registry))
				.environmentDivision(parseEnvironmentDivision(reader, registry))
				.dataDivision(parseDataDivision(reader, registry))
				.procedureDivision(parseProcedureDivision(reader, registry))
				.cobolProgramList(parsePrograms(reader, registry))
				;
		Identifier programName = parseEndProgramStatement(reader, registry);
		// TODO: check end program name with program id paragraph.
		return program;
	}

	private static Identifier parseEndProgramStatement(StringTokenReader reader, StringTokenParserRegistry registry) {
		if (!reader.isKindNextTokens(END, PROGRAM, IDENTIFIER)) return null;

		reader.accept(END);
		reader.accept(PROGRAM);
		return parseIdentifier(reader, registry);
	}

	/**
	 * idDivision: ('IDENTIFICATION' | 'ID') 'DIVISION' '.'
	 *              programIdParagraph
	 *              idDivisionOptionalParagraph*
	 */
	public static IdDivision parseIdDivision(StringTokenReader reader, StringTokenParserRegistry registry) {
		reader.acceptAnyOf(IDENTIFICATION, ID);
		reader.accept(DIVISION);
		reader.accept(DOT);

		IdDivision idDivision = new IdDivision()
				.programIdParagraph(parseProgramIdParagraph(reader, registry));

		parseIdDivisionOptionalParagraph(reader, idDivision);

		return idDivision;
	}

	public static boolean isIdDivision(StringTokenReader reader) {
		return reader.isKindNextTokens(IDENTIFICATION, DIVISION, DOT)
				|| reader.isKindNextTokens(ID, DIVISION, DOT);
	}

	/** programIdParagraph: "PROGRAM-ID" "."? program-name [ [ "IS" ] "INITIAL" [ "PROGRAM" ] ] "."? */
	private static Identifier parseProgramIdParagraph(StringTokenReader reader, StringTokenParserRegistry registry) {
		reader.accept(PROGRAM_ID);
		reader.optional(DOT);
		Identifier programName = parseIdentifier(reader, registry);
		reader.optional(DOT);
		return programName;
	}

	private static void parseIdDivisionOptionalParagraph(StringTokenReader reader, IdDivision idDivision) {
		for (StringToken token = null; ; ) {
			token = reader.optionalAnyOfAndReturn(AUTHOR, INSTALLATION, DATE_WRITTEN, DATE_COMPILED, SECURITY);
			if (token == null) {
				break;
			}
//			idDivision.attributes().put(token.kind(), (String)token.attribute(token.kind()));
		}
	}


	/** [ "ENVIRONMENT" "DIVISION" "." environment-division-content ] */
	public static EnvironmentDivision parseEnvironmentDivision(StringTokenReader reader, StringTokenParserRegistry registry) {
		if (!reader.optionalNextTokens(ENVIRONMENT, DIVISION, DOT)) return null;

		return new EnvironmentDivision()
				.configurationSection(parseConfigurationSection(reader, registry))
				.inputOutputSection(parseInputOutputSection(reader, registry));
	}

	/** configuration-section	=	"CONFIGURATION" "SECTION" "." configuration-section-paragraphs */
	public static ConfigurationSection parseConfigurationSection(StringTokenReader reader, StringTokenParserRegistry registry) {
		if (!reader.optionalNextTokens(CONFIGURATION, SECTION, DOT)) return null;

		ConfigurationSection config = new ConfigurationSection();
		// SOURCE_COMPUTER '.' computerName (WITH? DEBUGGING MODE)? '.'
		if (reader.optionalNextTokens(SOURCE_COMPUTER, DOT)) {
			config.sourceComputerParagraph(parseIdentifier(reader, registry));
			reader.optional(WITH);
			reader.optionalNextTokens(DEBUGGING, MODE);
			reader.optional(DOT);
		}

		// OBJECT_COMPUTER '.' computerName objectComputerClause* '.'
		if (reader.optionalNextTokens(OBJECT_COMPUTER, DOT)) {
			config.objectComputerParagraph(parseIdentifier(reader, registry));
			reader.optional(DOT);
		}

		return config;
	}

	/** input-output-section	=	"INPUT-OUTPUT" "SECTION" "." [ file-control-paragraph ] [ i-o-control-paragraph ] */
	public static InputOutputSection parseInputOutputSection(StringTokenReader reader, StringTokenParserRegistry registry) {
		if (!reader.optionalNextTokens(INPUT_OUTPUT, SECTION, DOT)) return null;

		FileControlParagraph fileControlParagraph = parseFileControlParagraph(reader, registry);
		IoControlParagraph ioControlParagraph = parseIoControlParagraph(reader, registry);
		return new InputOutputSection().fileControlParagraph(fileControlParagraph)
				.ioControlParagraph(ioControlParagraph);
	}

	/** fileControlParagraph: FILE_CONTROL '.' fileControlEntry*
	 * fileControlEntry: SELECT OPTIONAL? fileName
	 *     ( assignClause
	 *     | reserveClause
	 *     | organizationClause
	 *     | paddingCharacterClause
	 *     | recordDelimiterClause
	 *     | accessModeClause
	 *     | recordKeyClause
	 *     | alternateRecordKeyClause
	 *     | fileStatusClause
	 *     | passwordClause
	 *     | relativeKeyClause
	 *     )
	 */
	private static FileControlParagraph parseFileControlParagraph(StringTokenReader reader, StringTokenParserRegistry registry) {
		if (!reader.optionalNextTokens(FILE_CONTROL, DOT)) return null;

		List<FileControlEntry> list = parseFileControlEntries(reader, registry);

		return new FileControlParagraph().fileControlEntryList(list);
	}

	private static List<FileControlEntry> parseFileControlEntries(StringTokenReader reader, StringTokenParserRegistry registry) {
		List<FileControlEntry> list = new ArrayList<>();
		for (; reader.optional(SELECT); ) {
			reader.optional(OPTIONAL);
			Identifier fileName = parseIdentifier(reader, registry);
			CompositeStatement<Statement> statementList = parseCompositeStatement(reader, registry);
			list.add(new FileControlEntry().fileName(fileName)
					.statementList(statementList));
		}
		return list;
	}

	private static AssignClause parseAssignClause(StringTokenReader reader, StringTokenParserRegistry registry) {
		if (!reader.optional(ASSIGN)) return null;

		reader.optional(TO);
		Expression assignee = parseIdentifierOrLiteral(reader, registry);
		return new AssignClause().assignmentName(assignee);
	}

	private static IoControlParagraph parseIoControlParagraph(StringTokenReader reader, StringTokenParserRegistry registry) {
		return null;
	}

	/** dataDivision: "DATA" "DIVISION" "."
	 *      fileSection?
	 *      workingStorageSection?
	 *      linkageSection?
	 *      dataBaseSection?
	 *      communicationSection?
	 *      localStorageSection?
	 *      screenSection?
	 *      reportSection?
	 *      programLibrarySection?
	 */
	public static DataDivision parseDataDivision(StringTokenReader reader, StringTokenParserRegistry registry) {
		if (!reader.optionalNextTokens(DATA, DIVISION, DOT)) return null;

		return new DataDivision()
				.fileSection(parseFileSection(reader, registry))
				.workingStorageSection(parseWorkingStorageSection(reader, registry))
				.linkageSection(parseLinkageSection(reader, registry));
	}

	/** fileSection:
	 *      - FILE SECTION '.' [fileDescriptionEntry record-description-entry?]
	 *      - FILE SECTION '.' sortFileDescriptionEntry?
	 */
	public static FileSection parseFileSection(StringTokenReader reader, StringTokenParserRegistry registry) {
		if (!reader.optionalNextTokens(FILE, SECTION, DOT)) return null;

		return new FileSection();
	}

	/** [ "WORKING-STORAGE" "SECTION" "." { ( record-description-entry | data-item-description-entry ) }* ] */
	public static WorkingStorageSection parseWorkingStorageSection(StringTokenReader reader, StringTokenParserRegistry registry) {
		if (!reader.optionalNextTokens(WORKING_STORAGE, SECTION, DOT)) {
			return null;
		}

		WorkingStorageSection wsSection = new WorkingStorageSection();
		DataDescriptionEntry stmt = parseDataDescriptionEntry(reader, registry);
		wsSection.dataDescriptionEntryList().add(stmt);

		return wsSection;
	}

	/**  ["LINKAGE" "SECTION" "." { ( record-description-entry | data-item-description-entry ) }* ] */
	public static LinkageSection parseLinkageSection(StringTokenReader reader, StringTokenParserRegistry registry) {
		if (!reader.optionalNextTokens(LINKAGE, SECTION, DOT)) {
			return null;
		}
		return new LinkageSection();
	}

	/**
	 * File Description (FD) Entry (or Sort File Description (SD) Entry for sort/merge files)
	 * fileDescriptionEntry: FD fileName fileDescriptionEntryClause*
	 *      - sequential file description entry
	 *      - relative or indexed file description entry
	 *      - line-sequential file description entry
	 * sortFileDescriptionEntry: SD fileName fileDescriptionEntryClause*
	 *      - sort/merge file description entry
	 * fileDescriptionEntryClause:
	 *       externalClause
	 *     | globalClause
	 *     | blockContainsClause
	 *     | recordContainsClause
	 *     | labelRecordsClause
	 *     | valueOfClause
	 *     | dataRecordsClause
	 *     | linageClause
	 *     | codeSetClause
	 *     | reportClause
	 *     | recordingModeClause
	 *     ;
	 */
	public static FileDescriptionEntry parseFileDescriptionEntry(StringTokenReader reader, StringTokenParserRegistry registry) {
		if (!reader.optional(FD)) return null;

		FileDescriptionEntry fdEntry = new FileDescriptionEntry()
				.fileName(parseIdentifier(reader, registry));
		fdEntry.clause(parseStatement(reader, registry));
		return fdEntry;
	}

	public static FileDescriptionEntry parseSortFileDescriptionEntry(StringTokenReader reader, StringTokenParserRegistry registry) {
		if (!reader.optional(SD)) return null;

		FileDescriptionEntry fdEntry = new FileDescriptionEntry().isSort(true)
				.fileName(parseIdentifier(reader, registry));
		fdEntry.clause(parseStatement(reader, registry));
		return fdEntry;
	}

	public static Statement parseLabelRecordsClause(StringTokenReader reader, StringTokenParserRegistry registry) {
		if (!reader.optional(LABEL)) return null;

		return null;
	}

	/**
	 * data-item-description-entry or 77-level description-entry = data-description-entry
	 * record-description-entry	     = data-description-entry
	 * data-description-entry
	 *  defining data-names          = level-number [ ( data-name | "FILLER" ) ] data-description-entry-clauses "."
	 *  defining renaming data-names = "66" data-name renames-clause "."
	 *  defining condition-names	 = "88" condition-name condition-value-clause "."
	 */
	public static DataDescriptionEntry parseDataDescriptionEntry(StringTokenReader reader, StringTokenParserRegistry registry) {
		if (!acceptLevelNumber(reader)) return null;

		Identifier dataName = parseIdentifier(reader, registry); // includes FILLER
		if (dataName != null) {
			log.info("data-name is " + dataName);
		}
		DataDescriptionEntry variable = new DataDescriptionEntry().dataName(dataName);

		while (reader.token().kind() != DOT) {
			Statement statement = parseStatement(reader, registry);
			if (statement instanceof DataPictureClause picString) {
				variable.dataPictureClause(picString);
			} else if (statement instanceof DataValueClause value) {
				variable.dataValueClause(value);
			} else {
				syntaxError(reader, "");
			}
			reader.nextToken(); // FIXME
		}
		reader.acceptAnyOf(DOT);

		return variable;
	}

	private static boolean acceptLevelNumber(StringTokenReader reader) {
		StringToken levelNumber = reader.token();
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
	public static DataPictureClause parsePictureClause(StringTokenReader reader, StringTokenParserRegistry registry) {
		reader.acceptAnyOf(PICTURE, PIC);
		reader.optionalAnyOf(IS);
		CharacterString picString = parseCharacterString(reader, registry);
		return new DataPictureClause().picString(picString);
	}

	public static CharacterString parseCharacterString(StringTokenReader reader, StringTokenParserRegistry registry) {
		StringBuilder sb = new StringBuilder();
		StringToken t = null;
		while ((t = reader.optionalAnyOfAndReturn(IDENTIFIER, NUMBER_LITERAL, LEFT_PAREN, RIGHT_PAREN)) != null) {
			sb.append(t.lexeme());
		}
		return new CharacterString().format(sb.toString());
	}

	public static DataValueClause parseValueClause(StringTokenReader reader, StringTokenParserRegistry registry) {
		if (reader.optionalAnyOf(VALUE)) {
			StringToken t = reader.acceptAnyOfAndReturn(NUMBER_LITERAL, ZERO, ZEROS, ZEROES, SPACE, SPACES);
			if (t != null) {
				return new DataValueClause().value(t.lexeme());
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
	public static ProcedureDivision parseProcedureDivision(StringTokenReader reader, StringTokenParserRegistry registry) {
		// ignore tokens until "PROCEDURE" "DIVISION"
//		while (!reader.isKindNextTokens(PROCEDURE, DIVISION)) {
//			reader.nextToken();
//		}
		if (!reader.isKindNextTokens(PROCEDURE, DIVISION)) return null;

		ProcedureDivision division = new ProcedureDivision();
		if (reader.optionalNextTokens(PROCEDURE, DIVISION, DOT)) {
			// do nothing
		} else if (reader.optionalNextTokens(PROCEDURE, DIVISION, USING)) {
			division.procedureUsingClause(parseUsingClause(reader, registry));
		} else {
			return null;
		}

		while (!reader.optionalAnyOf(EOF) && !reader.optionalNextTokens(END, PROGRAM)) {
			Statement statement = parseStatement(reader, registry);
			division.statementList().add(statement);
		}
		return division;
	}

	public static ProcedureUsingClause parseUsingClause(StringTokenReader reader, StringTokenParserRegistry registry) {
		ProcedureUsingClause procedureUsingClause = new ProcedureUsingClause();
		while (reader.token().kind() == IDENTIFIER) {
			procedureUsingClause.procedureParameterList().add(parseIdentifier(reader, registry));
		}
		return procedureUsingClause;
	}

	public static DisplayClause parseDisplayClause(StringTokenReader reader, StringTokenParserRegistry registry) {
		reader.acceptAnyOf(DISPLAY);
		StringToken token = reader.acceptAnyOfAndReturn(STRING_LITERAL);
		return new DisplayClause().value(token.lexeme());
	}

	public static boolean isNewDivision(StringTokenReader reader) {
		StringToken token1 = reader.peekToken(1);
		StringToken token2 = reader.peekToken(2);
		return token2.kind() == DIVISION
				&& (token1.kind() == ENVIRONMENT || token1.kind() == DATA || token1.kind() == PROCEDURE);
	}


	// == sentences: statement-list "." ==


	// == statement-list: statement+ ==
	// statement = (if-statement | evaluate-statement | ... | exit-statement)
	/**
	 * if-statement = "IF" condition "THEN"? ( statement+ | "NEXT" "SENTENCE" )
	 *                 [ "ELSE"              ( statement+ | "NEXT" "SENTENCE" ) ]
	 *                 [ "END-IF" ]
	 */
	public static IfStatement parseIfStatement(StringTokenReader reader, StringTokenParserRegistry registry) {
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
	 *    [ "WHEN" "OTHER"                                          statement-list ]
	 *    "END-EVALUATE"?
	 * evaluate-select: identifier | literal | arithmetic-expression | conditional-expression | boolean-literal
	 * evaluate-phrase:	"ANY" | conditional-expression | boolean-literal
	 *      | [ "NOT" ] evaluate-value [ ( "THROUGH" | "THRU" ) evaluate-value ]
	 * evaluate-value: identifier | literal | arithmetic-expression
	 */
	public static EvaluateStatement parseEvaluateStatement(StringTokenReader reader, StringTokenParserRegistry registry) {
		if (!reader.optional(EVALUATE)) return null;

		EvaluateStatement evaluateStatement = new EvaluateStatement();
		evaluateStatement.expression(parseEvaluateSelect(reader, registry));
		evaluateStatement.alsoSelects(parseEvaluateAlsoSelects(reader, registry));

		while (reader.optional(WHEN)) {
			evaluateStatement.caseStatements().add(parseEvaluateWhenStatement(reader, registry));
		}

		reader.optional(END_EVALUATE);
		return evaluateStatement;
	}

	private static EvaluateWhenStatement parseEvaluateWhenStatement(StringTokenReader reader, StringTokenParserRegistry registry) {
		EvaluateWhenStatement caseStatement = new EvaluateWhenStatement();
		if (!reader.optional(OTHER)) {
			caseStatement.value(parseEvaluatePhrase(reader, registry));
			caseStatement.alsoSelects(parseEvaluateAlsoSelects(reader, registry));
		}
		caseStatement.statements(parseCompositeStatement(reader, registry));
		return caseStatement;
	}

	private static Expression parseEvaluateSelect(StringTokenReader reader, StringTokenParserRegistry registry) {
		return parseExpression(reader, registry);
	}

	private static List<Expression> parseEvaluateAlsoSelects(StringTokenReader reader, StringTokenParserRegistry registry) {
		return parseExpressionListZeroOrMore(reader, registry, ALSO);
	}

	/** evaluate-phrase:	( "ANY" | conditional-expression | boolean-literal
	 *      | [ "NOT" ] evaluate-value [ ( "THROUGH" | "THRU" ) evaluate-value ] ) */
	private static EvaluatePhrase parseEvaluatePhrase(StringTokenReader reader, StringTokenParserRegistry registry) {
		EvaluatePhrase valuePhrase = new EvaluatePhrase();
		if (reader.optional(ANY)) {
			valuePhrase.isAny(true);
		} else if (reader.optional(TRUE)) {
			valuePhrase.isTrue(true);
		} else if (reader.optional(FALSE)) {
			valuePhrase.isFalse(true);
		} else if (reader.optional(NOT)) {
			valuePhrase.isNot(true);
			valuePhrase.value(parseEvaluateValue(reader, registry));
			if (reader.optionalAnyOf(THROUGH, THRU)) {
				valuePhrase.through(parseEvaluateValue(reader, registry));
			}
		} else {    // conditional-expression | evaluate-value [ ( "THROUGH" | "THRU" ) evaluate-value ]
			Expression condition = parseConditionalExpression(reader, registry);
			if (condition != null) {
				valuePhrase.condition(condition);
			} else {
				valuePhrase.value(parseEvaluateValue(reader, registry));
				if (reader.optionalAnyOf(THROUGH, THRU)) {
					valuePhrase.through(parseEvaluateValue(reader, registry));
				}
			}
		}
		return valuePhrase;
	}

	private static Expression parseEvaluateValue(StringTokenReader reader, StringTokenParserRegistry registry) {
		if (reader.isKind(IDENTIFIER)) {
			return parseIdentifier(reader, registry);
		} else if (reader.isKindAnyOf(STRING_LITERAL, NUMBER_LITERAL, BOOLEAN_LITERAL)) {
			return parseLiteral(reader, registry);
		} else {
			return parseArithmeticExpression(reader, registry);
		}
	}

	public static Statement parseExitStatement(StringTokenReader reader, StringTokenParserRegistry registry) {
		if (!reader.optional(EXIT)) return null;

		return new ExpressionStatement().expression(Identifier.of("exit"));
	}

	public static Statement parseStopStatement(StringTokenReader reader, StringTokenParserRegistry registry) {
		if (!reader.optional(STOP)) return null;

		if (reader.optional(RUN)) {
		} else if (reader.isKindAnyOf(NUMBER_LITERAL, STRING_LITERAL)) {
			Literal literal = parseLiteral(reader, registry);
		}

		return new ExpressionStatement().expression(Identifier.of("stop"));
	}

	public static ContinueStatement parseContinueStatement(StringTokenReader reader, StringTokenParserRegistry registry) {
		if (!reader.optional(CONTINUE)) return null;

		return new ContinueStatement();
	}

	/** goto-statement: (unconditional-goto | conditional-goto | altered-goto)
	 * unconditional-goto: 'GO' 'TO'? procedure-name
	 */
	public static GotoStatement parseGotoStatement(StringTokenReader reader, StringTokenParserRegistry registry) {
		if (!reader.optionalAnyOf(GO)) return null;
		reader.optionalAnyOf(TO);
		Identifier label = parseIdentifier(reader, registry);
		return new GotoStatement().label(label);
	}

	public static ExpressionStatement parseGoBackStatement(StringTokenReader reader, StringTokenParserRegistry registry) {
		if (!reader.optionalAnyOf(GOBACK)) return null;
		return new ExpressionStatement().expression(Identifier.of("goback"));
	}

	public static ExpressionStatement parseCallStatement(StringTokenReader reader, StringTokenParserRegistry registry) {
		if (!reader.optionalAnyOf(CALL)) return null;

		FunctionCall call = new FunctionCall();
		call.functionSelect(parseIdentifierOrLiteral(reader, registry));

		if (reader.optionalAnyOf(USING)) {
			call.arguments(parseArguments(reader, registry));
		}

		reader.optionalAnyOf(END_CALL);
		return new ExpressionStatement().expression(call);
	}

	/**
	 * The PERFORM statement transfers control explicitly to one or more procedures
	 * and implicitly returns control to the next executable statement
	 * after execution of the specified procedures is completed.
	 * The PERFORM statement is an inline PERFORM statement, when procedure-name-1 is omitted.
	 * - perform unconditionally N times.
	 * - perform until condition.
	 *
	 * - test before: condition will be tested before execution.
	 * - test after: execution is executed at least once before condition is tested.
	 *
	 * perform-statement: (out-of-line-perform-statement | inline-perform-statement)
	 * out-of-line-perform-statement: "PERFORM" procedure-name [ ( "THROUGH" | "THRU" ) procedure-name ] (basic | times | until | varying)
	 *      basic:
	 *      times:   (identifier | integer) "TIMES"
	 * 	    varying: perform-varying-phrase perform-after-phrase
	 * 	    until:   perform-until-phrase
	 * inline-perform-statement: "PERFORM" (basic | times | until | varying) statement-list "END-PERFORM"
	 *      basic:
	 *      times:   (identifier | integer) "TIMES"
	 *      varying: perform-varying-phrase
	 *      until:   perform-until-phrase
	 * perform-until-phrase:   [ "WITH"? "TEST" ( "BEFORE" | "AFTER" ) ] "UNTIL" condition
	 * perform-varying-phrase: [ "WITH"? "TEST" ( "BEFORE" | "AFTER" ) ] "VARYING" ( identifier | index-name )
	 *      "FROM" ( identifier | index-name | literal ) "BY" ( identifier | literal ) "UNTIL" condition
	 * perform-after-phrase: { "AFTER" ( identifier | index-name )
	 *      "FROM" ( identifier | index-name | literal ) "BY" ( identifier | literal ) "UNTIL" condition }*
	 */
	public static PerformStatement parsePerformStatement(StringTokenReader reader, StringTokenParserRegistry registry) {
		if (!reader.optionalAnyOf(PERFORM)) return null;

		PerformStatement stmt = new PerformStatement();
		if (varyingOrUntilPhrase(reader, registry, stmt)) { // inline varying or until
			stmt.statementList(parseCompositeStatement(reader, registry));
			reader.accept(END_PERFORM);
		} else if (timesPhrase(reader, registry, stmt)) {   // inline times
			stmt.statementList(parseCompositeStatement(reader, registry));
			reader.accept(END_PERFORM);
		} else if (outOfLineThroughPhrase(reader, registry, stmt)) {
		} else if (outOfLinePhrase(reader, registry, stmt)) {
		} else {    // inline basic
			stmt.statementList(parseCompositeStatement(reader, registry));
			reader.accept(END_PERFORM);
		}

		reader.optionalAnyOf(END_PERFORM);
		return stmt;
	}

	private static boolean outOfLinePhrase(StringTokenReader reader, StringTokenParserRegistry registry, PerformStatement stmt) {
		if (reader.isKind(IDENTIFIER)) {
			stmt.procedureName(parseIdentifier(reader, registry));
			if (varyingOrUntilPhrase(reader, registry, stmt)) { // varying or until
			}  else if (timesPhrase(reader, registry, stmt)) {  // times
			} else {    // basic
			}
			return true;
		}
		return false;
	}

	private static boolean outOfLineThroughPhrase(StringTokenReader reader, StringTokenParserRegistry registry, PerformStatement stmt) {
		if (reader.isKindNextTokens(IDENTIFIER, THROUGH)
				|| reader.isKindNextTokens(IDENTIFIER, THRU)) {
			stmt.procedureName(parseIdentifier(reader, registry));
			reader.optionalAnyOf(THROUGH, THRU);
			stmt.throughProcedureName(parseIdentifier(reader, registry));
			if (varyingOrUntilPhrase(reader, registry, stmt)) { // varying or until
			}  else if (timesPhrase(reader, registry, stmt)) {  // times
			} else {    // basic
			}
			return true;
		}
		return false;
	}

	private static boolean timesPhrase(StringTokenReader reader, StringTokenParserRegistry registry, PerformStatement stmt) {
		if (reader.isKindNextTokens(IDENTIFIER, TIMES)) {
			stmt.timesExpression(parseIdentifier(reader, registry));
			reader.accept(TIMES);
			return true;
		} else if (reader.isKindNextTokens(NUMBER_LITERAL, TIMES)) {
			stmt.timesExpression(parseLiteral(reader, registry));
			reader.accept(TIMES);
			return true;
		}
		return false;
	}

	private static boolean varyingOrUntilPhrase(StringTokenReader reader, StringTokenParserRegistry registry, PerformStatement stmt) {
		boolean with = reader.optional(WITH);
		boolean test = reader.optional(TEST);
		if (with || test) {
			StringToken t = reader.acceptAnyOfAndReturn(BEFORE, AFTER);
			stmt.beforeTest(t.kind() == BEFORE);
			if (reader.optional(UNTIL)) {
				stmt.untilExpression(parseConditionalExpression(reader, registry));
			} else if (reader.optional(VARYING)) {
				stmt.varyingIdentifier(parseIdentifier(reader, registry));
				reader.accept(FROM);
				stmt.fromExpression(parseIdentifierOrLiteral(reader, registry));
				reader.accept(BY);
				stmt.byExpression(parseIdentifierOrLiteral(reader, registry));
				reader.accept(UNTIL);
				stmt.untilExpression(parseConditionalExpression(reader, registry));
			}
			return true;
		}
		return false;
	}

	public static List<Expression> parseArguments(StringTokenReader reader, StringTokenParserRegistry registry) {
		throw new RuntimeException("Not Implemented");
	}

	/** move-statement: 'MOVE'                (identifier | literal) 'TO' identifier
	 *                | 'MOVE' ('CORRESPONDING' | 'CORR') identifier 'TO' identifier */
	public static ExpressionStatement parseMoveStatement(StringTokenReader reader, StringTokenParserRegistry registry) {
		if (!reader.optional(MOVE)) return null;

		boolean corresponding = reader.optionalAnyOf(CORRESPONDING, CORR);
		Expression expr = corresponding
				? parseIdentifier(reader, registry)
				: parseIdentifierOrLiteral(reader, registry);
		reader.accept(TO);
		Identifier variable = parseIdentifier(reader, registry);
		Assignment assignment = new Assignment().expression(expr).variable(variable);
		return new ExpressionStatement().expression(assignment);
	}

	/**
	 * set-statement:
	 *       "SET" { ( index-name | identifier ) }+ 			    "TO" ( index-name | identifier | integer )
	 *     | "SET" { { mnemonic-name }+ 				            "TO" ( "ON" | "OFF" ) }+
	 *     | "SET" { condition-name-reference }+ 			        "TO" "TRUE"
	 *     | "SET" { ( identifier | "ADDRESS" "OF" identifier ) }+	"TO" ( identifier | "ADDRESS" "OF" identifier | "NULL" | "NULLS" )
	 *     | "SET" { index-name }+ ( "UP" "BY" | "DOWN" "BY" ) ( identifier | integer )
	 */
	public static SetStatement parseSetStatement(StringTokenReader reader, StringTokenParserRegistry registry) {
		if (!reader.optional(SET)) return null;

		SetStatement stmt = new SetStatement();
		Assignment assignment = new Assignment();
		if (reader.optional(ADDRESS)) {
			reader.accept(OF);
			stmt.addressOf(true);
			assignment.variable(parseIdentifier(reader, registry));
			reader.accept(TO);
			if (reader.optional(ADDRESS)) {
				reader.accept(OF);
				assignment.expression(parseIdentifier(reader, registry));
			} else if (reader.optionalAnyOf(NULL, NULLS)) {
				assignment.expression(new Identifier().id("NULL"));
			} else {
				assignment.expression(parseIdentifier(reader, registry));
			}
		} else {
			assignment.variable(parseIdentifier(reader, registry));
			if (reader.optional(UP)) {
				reader.accept(BY);
				stmt.up(true);
				assignment.expression(parseIdentifierOrLiteral(reader, registry));
			} else if (reader.optional(DOWN)) {
				reader.accept(BY);
				stmt.down(true);
				assignment.expression(parseIdentifierOrLiteral(reader, registry));
			} else if (reader.accept(TO)) {
				if (reader.optional(ON)) {
					stmt.on(true);
				} else if (reader.optional(OFF)) {
					stmt.off(true);
				} else if (reader.optional(TRUE)) {
					stmt.isTrue(true);
				} else {
					assignment.expression(parseIdentifierOrLiteral(reader, registry));
				}
			}
		}
		stmt.expression(assignment);
		return stmt;
	}

	/**
	 * compute-statement: "COMPUTE" { identifier [ "ROUNDED" ] }+ ( "=" | "EQUAL" ) arithmetic-expression
	 *      [ "NOT"? "ON"? "SIZE" "ERROR" statement-list ]
	 *      [ "END-COMPUTE" ]
	 */
	public static ExpressionStatement parseComputeStatement(StringTokenReader reader, StringTokenParserRegistry registry) {
		if (!reader.optionalAnyOf(COMPUTE)) return null;

		List<RoundedIdentifier> ids = parseRoundedIdentifiers(reader, registry);
		CompositeExpression variable = new CompositeExpression().children(ids);
		reader.acceptAnyOf(EQUAL);
		Expression expr = parseArithmeticExpression(reader, registry);
		reader.optionalAnyOf(END_COMPUTE);
		Assignment assignment = new Assignment().expression(expr).variable(variable);
		return new ExpressionStatement().expression(assignment);
	}

	/** -- add to / subtract from / multiply by / divide by statements --
	 * add-statement: 'ADD' (addToStatement | addToGivingStatement | addCorrespondingStatement) onSizeErrorPhrase? notOnSizeErrorPhrase? "END-ADD"?
	 * addToStatement:            ( identifier | literal )+ "TO" { identifier "ROUNDED"? }+
	 * addToGivingStatement:      ( identifier | literal )+ "TO"? ( identifier | literal ) GIVING { identifier "ROUNDED"? }+
	 * addCorrespondingStatement: ('CORRESPONDING' | 'CORR') identifier "TO" identifier
	 */
	public static AddSubtractMultiplyDivideStatement parseAddStatement(StringTokenReader reader, StringTokenParserRegistry registry) {
		return parseAddOrSubtractStatement(reader, registry, ADD, TO, END_ADD).operator("+");
	}
	public static AddSubtractMultiplyDivideStatement parseSubtractStatement(StringTokenReader reader, StringTokenParserRegistry registry) {
		return parseAddOrSubtractStatement(reader, registry, SUBTRACT, FROM, END_SUBTRACT).operator("-");
	}
	public static AddSubtractMultiplyDivideStatement parseAddOrSubtractStatement(StringTokenReader reader, StringTokenParserRegistry registry,
	                                                                             String kindStart, String kindMiddle, String kindEnd) {
		if (!reader.optional(kindStart)) return null;

		AddSubtractMultiplyDivideStatement stmt = new AddSubtractMultiplyDivideStatement();
		if (reader.optionalAnyOf(CORRESPONDING, CORR)) {    // addCorrespondingStatement
			stmt.corresponding(true);
			stmt.rightOperand(parseIdentifier(reader, registry));
			reader.accept(kindMiddle);
			stmt.leftOperand(parseIdentifier(reader, registry));
		} else {
			List<Expression> list = parseExpressionListOneOrMore(reader, registry, TO);
			stmt.rightOperand(new CompositeExpression().children(list));
			reader.optional(kindMiddle);

			boolean isGiving = false;
			if (reader.isKindAnyOf(STRING_LITERAL, NUMBER_LITERAL)) {   // addToGivingStatement
				stmt.giving(true);
				reader.accept(GIVING);
				List<RoundedIdentifier> ids = parseRoundedIdentifiers(reader, registry);
				stmt.givingIdentifiers(new CompositeExpression().children(ids));
			} else {
				givingOrRegular(reader, registry, stmt);
			}
		}
		reader.optional(kindEnd);
		return stmt;
	}

	private static void givingOrRegular(StringTokenReader reader, StringTokenParserRegistry registry, AddSubtractMultiplyDivideStatement stmt) {
		boolean isGiving;
		Identifier id = parseIdentifier(reader, registry);
		isGiving = reader.optional(GIVING);
		if (isGiving) { // addToGivingStatement
			List<RoundedIdentifier> ids = parseRoundedIdentifiers(reader, registry);
			stmt.givingIdentifiers(new CompositeExpression().children(ids));
		} else {        // addToStatement
			boolean rounded = reader.optional(ROUNDED);
			RoundedIdentifier ri = new RoundedIdentifier().rounded(rounded);
			ri.id(id.id());
			List<RoundedIdentifier> ids = parseRoundedIdentifiers(reader, registry);
			ids.add(0, ri); // insert the 1st id at the beginning.
			stmt.leftOperand(new CompositeExpression().children(ids));
		}
	}

	/**
	 * multiplyStatement: 'MULTIPLY' (identifier | literal) 'BY' (multiplyRegular | multiplyGiving) onSizeErrorPhrase? notOnSizeErrorPhrase? 'END-MULTIPLY'?
	 * multiplyRegular: { identifier "ROUNDED"? }+
	 * multiplyGiving:  ( identifier | literal ) "GIVING" { identifier "ROUNDED"? }+
	 */
	public static AddSubtractMultiplyDivideStatement parseMultiplyStatement(StringTokenReader reader, StringTokenParserRegistry registry) {
		if (!reader.optional(MULTIPLY)) return null;

		AddSubtractMultiplyDivideStatement stmt = new AddSubtractMultiplyDivideStatement();
		stmt.rightOperand(parseIdentifierOrLiteral(reader, registry));
		reader.accept(BY);
		givingOrRegular(reader, registry, stmt);
		reader.optional(END_MULTIPLY);
		return stmt.operator("*");
	}

	/**
	 * divideStatement: 'DIVIDE' (identifier | literal) (
	 *           divideIntoStatement
	 *         | divideIntoGivingStatement
	 *         | divideByGivingStatement
	 *     ) 'REMAINDER' identifier onSizeErrorPhrase? notOnSizeErrorPhrase? 'END-DIVIDE'?
	 * divideIntoStatement:       'INTO' {identifier 'ROUNDED'?}+
	 * divideIntoGivingStatement: 'INTO' (identifier | literal) 'GIVING' {identifier 'ROUNDED'?}+
	 * divideByGivingStatement:     'BY' (identifier | literal) 'GIVING' {identifier 'ROUNDED'?}+
	 */
	public static AddSubtractMultiplyDivideStatement parseDivideStatement(StringTokenReader reader, StringTokenParserRegistry registry) {
		if (!reader.optional(DIVIDE)) return null;

		AddSubtractMultiplyDivideStatement stmt = new AddSubtractMultiplyDivideStatement();
		Expression expr1 = parseIdentifierOrLiteral(reader, registry);
		if (reader.optional(BY)) {  // divideByGivingStatement
			Expression expr2 = parseIdentifierOrLiteral(reader, registry);
			reader.accept(GIVING);
			List<RoundedIdentifier> ids = parseRoundedIdentifiers(reader, registry);
			stmt.givingIdentifiers(new CompositeExpression().children(ids));
		} else if (reader.accept(INTO)) {  // divideIntoStatement or divideIntoGivingStatement
			givingOrRegular(reader, registry, stmt);
		}
		reader.optional(END_DIVIDE);
		return stmt.operator("/");
	}

	public static List<RoundedIdentifier> parseRoundedIdentifiers(StringTokenReader reader, StringTokenParserRegistry registry) {
		List<RoundedIdentifier> list = new ArrayList<>();
		while (reader.isKind(IDENTIFIER)) {
			list.add(parseRoundedIdentifier(reader, registry));
		};
		return list;
	}

	private static RoundedIdentifier parseRoundedIdentifier(StringTokenReader reader, StringTokenParserRegistry registry) {
		RoundedIdentifier ri = new RoundedIdentifier();
		ri.id(parseIdentifier(reader, registry).id());
		ri.rounded(reader.optional(ROUNDED));
		return ri;
	}

	// == expression: (arithmetic-expression | conditional-expression) ==

	/**-- arithmetic-expression: unary-expression [binary-operator unary-expression] --
	 * 1. An identifier described as a numeric elementary item (including numeric functions)
	 * 2. A numeric literal
	 * 3. The figurative constant ZERO
	 * 4. Identifiers and literals, as defined in items 1, 2, and 3, separated by arithmetic operators
	 * 5. Two arithmetic expressions, as defined in items 1, 2, 3, or 4, separated by an arithmetic operator
	 * 6. An arithmetic expression, as defined in items 1, 2, 3, 4, or 5, enclosed in parentheses
	 * Any arithmetic expression can be preceded by a unary operator.
	 *
	 * arithmetic operators
	 * > Binary operator    Meaning
	 *	    +  	            Addition
	 *	    -  	            Subtraction
	 *	    *  	            Multiplication
	 *	    /  	            Division
	 *	    ** 	            Exponentiation
	 *
	 * > Unary operator     Meaning
	 *	    + 	            Multiplication by +1
	 *	    - 	            Multiplication by -1
	 *
	 * Precedence:
	 * 1. Parentheses
	 * 2. Unary operator
	 * 3. Exponentiation
	 * 4. Multiplication and division
	 * 5. Addition and subtraction
	 */
	public static Expression parseArithmeticExpression(StringTokenReader reader, StringTokenParserRegistry registry) {
		return parseBinaryExpression(reader, registry);
	}

	/** -- conditional-expression: (simple-condition | combined-condition) --
	 * Simple condition:
	 *        Relation condition         IF AGE < 18 THEN...
	 *      | Condition-name condition   IF IS-CHILD THEN...
	 *      | Class condition
	 *      | Sign condition
	 *      | Switch-status condition
	 * Combined condition:
	 *
	 */
	public static Expression parseConditionalExpression(StringTokenReader reader, StringTokenParserRegistry registry) {
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
	protected static String parserRationalOperator(StringTokenReader reader) {
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
			StringToken t = reader.optionalAnyOfAndReturn(GT, GT_EQUAL, LT, LT_EQUAL, EQUAL, NOT_EQUAL);
			if (t != null) {
				operator = t.lexeme();
			}
		}
		return operator;
	}

	public static Expression parseBinaryExpression(StringTokenReader reader, StringTokenParserRegistry registry) {
		Expression expr = parseUnaryExpression(reader, registry);
		StringToken t = reader.optionalAnyOfAndReturn(PLUS, MINUS, STAR, SLASH);
		if (t == null)
			return expr;

		BinaryExpression binaryExpression = new BinaryExpression().leftOperand(expr);
		if (t.kind() == STAR && reader.optionalAnyOf(STAR)) {
			binaryExpression.operator("**");
		} else {
			binaryExpression.operator(t.lexeme());
		}
		binaryExpression.rightOperand(parseUnaryExpression(reader, registry));
		return binaryExpression;
	}

	public static Expression parseUnaryExpression(StringTokenReader reader, StringTokenParserRegistry registry) {
		StringToken token = reader.optionalAnyOfAndReturn(PLUS, MINUS);
		if (token != null) {
			return new PrefixUnaryExpression().operator(token.lexeme())
					.expression(parseUnaryExpression(reader, registry));
		} else {
			return parsePrimaryExpression(reader, registry);
		}
	}

	public static Expression parsePrimaryExpression(StringTokenReader reader, StringTokenParserRegistry registry) {
		StringToken t = null;
		Expression expr = null;
		if (reader.isKind(IDENTIFIER)) {
			expr = parseIdentifier(reader, registry);
		} else if ((t = reader.optionalAnyOfAndReturn(STRING_LITERAL)) != null) {
			expr = new Literal<String>().value(t.lexeme());
		} else if ((t = reader.optionalAnyOfAndReturn(NUMBER_LITERAL)) != null) {
			expr = new Literal<String>().value(t.lexeme());
		} else if (reader.isKind(LEFT_PAREN)) {
			expr = parseParenExpression(reader, registry);
		}
		return expr;
	}

	public static ParenthesizedExpression parseParenExpression(StringTokenReader reader, StringTokenParserRegistry registry) {
		reader.acceptAnyOf(LEFT_PAREN);
		Expression expr = parseArithmeticExpression(reader, registry);  //
		reader.acceptAnyOf(RIGHT_PAREN);
		return new ParenthesizedExpression().expression(expr);
	}

	public static Expression parseIdentifierOrLiteral(StringTokenReader reader, StringTokenParserRegistry registry) {
		if (reader.isKind(IDENTIFIER)) {
			return parseIdentifier(reader, registry);
		} else if (reader.isKindNextTokens(NUMBER_LITERAL, STRING_LITERAL)) {
			return parseLiteral(reader, registry);
		}
		return syntaxError(reader, "Neither IDENTIFIER nor LITERAL");
	}
}
