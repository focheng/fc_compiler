package fc.compiler.language.cobol;

import fc.compiler.common.ast.Expression;
import fc.compiler.common.ast.expression.*;
import fc.compiler.common.ast.statement.IfStatement;
import fc.compiler.language.cobol.ast.*;
import fc.compiler.language.cobol.ast.clause.DataDescriptionEntry;
import fc.compiler.language.cobol.ast.clause.DataPictureClause;
import fc.compiler.language.cobol.ast.clause.DataValueClause;
import fc.compiler.language.cobol.ast.clause.ProcedureUsingClause;
import fc.compiler.language.cobol.ast.division.*;
import fc.compiler.language.cobol.ast.statement.ElseStatement;
import fc.compiler.language.cobol.ast.statement.ExitStatement;
import fc.compiler.language.cobol.ast.statement.StopStatement;
import fc.compiler.language.cobol.ast.statement.ThenStatement;

import java.util.ArrayList;
import java.util.List;

import static fc.compiler.language.cobol.CobolTokenKind.*;
import static fc.compiler.language.cobol.CobolTokenKindTag.*;

/**
 * @author FC
 */
public class CobolParser {
	protected CobolLexer lexer;
	protected CobolToken token;

	public CobolParser(CobolLexer lexer) {
		this.lexer = lexer;
		nextToken();
	}

	public CobolCompilationUnit parseCobolCompilationUnit() {
		if (!token.kind.isAnyOf(IDENTIFICATION, ID)) return null;

		CobolCompilationUnit result = new CobolCompilationUnit();
		result.cobolProgramList(parseCobolProgramList());
		return result;
	}

	public CobolProgram parseCobolProgram() {
		if (!token.kind.isAnyOf(IDENTIFICATION, ID)) return null;

		CobolProgram result = new CobolProgram();
		result.idDivision(parseIdDivision());
		result.environmentDivision(parseEnvironmentDivision());
		result.dataDivision(parseDataDivision());
		result.procedureDivision(parseProcedureDivision());
		result.cobolProgramList(parseCobolProgramList());
		result.endProgram(parseEndProgram());
		return result;
	}

	public EndProgram parseEndProgram() {
		if (!token.kind.isAnyOf(END)) return null;

		EndProgram result = new EndProgram();
		acceptToken(END);
		acceptToken(PROGRAM);
		result.programName(parseIdentifier());
		acceptToken(DOT);
		return result;
	}

	public IdDivision parseIdDivision() {
		if (!token.kind.isAnyOf(IDENTIFICATION, ID)) return null;

		IdDivision result = new IdDivision();
		acceptAnyToken(IDENTIFICATION, ID);
		acceptToken(DIVISION);
		acceptToken(DOT);
		result.programIdParagraph(parseProgramIdParagraph());
		result.idDivisionOptionalParagraphList(parseIdDivisionOptionalParagraphList());
		return result;
	}

	public ProgramIdParagraph parseProgramIdParagraph() {
		if (!token.kind.isAnyOf(PROGRAM_ID)) return null;

		ProgramIdParagraph result = new ProgramIdParagraph();
		acceptToken(PROGRAM_ID);
		acceptToken(DOT);
		result.programName(parseIdentifier());
		acceptToken(DOT);
		return result;
	}

	public IdDivisionOptionalParagraph parseIdDivisionOptionalParagraph() {
		if (!token.kind.isAnyOf(_____, ___________, ____MINUS_______, ____MINUS________, _______, ______)) return null;

		IdDivisionOptionalParagraph result = new IdDivisionOptionalParagraph();
		acceptAnyToken(_____, ___________, ____MINUS_______, ____MINUS________, _______, ______);
		return result;
	}

	public EnvironmentDivision parseEnvironmentDivision() {
		if (!token.kind.isAnyOf(ENVIRONMENT)) return null;

		EnvironmentDivision result = new EnvironmentDivision();
		acceptToken(ENVIRONMENT);
		acceptToken(DIVISION);
		acceptToken(DOT);
		result.configurationSection(parseConfigurationSection());
		result.inputOutputSection(parseInputOutputSection());
		return result;
	}

	public ConfigurationSection parseConfigurationSection() {
		if (!token.kind.isAnyOf(CONFIGURATION)) return null;

		ConfigurationSection result = new ConfigurationSection();
		acceptToken(CONFIGURATION);
		acceptToken(SECTION);
		acceptToken(DOT);
		result.sourceComputerParagraph(parseSourceComputerParagraph());
		result.objectComputerParagraph(parseObjectComputerParagraph());
		return result;
	}

	public SourceComputerParagraph parseSourceComputerParagraph() {
		if (!token.kind.isAnyOf(SOURCE_COMPUTER)) return null;

		SourceComputerParagraph result = new SourceComputerParagraph();
		acceptToken(SOURCE_COMPUTER);
		acceptToken(DOT);
		result.computerName(parseIdentifier());
		acceptToken(DOT);
		return result;
	}

	public ObjectComputerParagraph parseObjectComputerParagraph() {
		if (!token.kind.isAnyOf(OBJECT_COMPUTER)) return null;

		ObjectComputerParagraph result = new ObjectComputerParagraph();
		acceptToken(OBJECT_COMPUTER);
		acceptToken(DOT);
		result.computerName(parseIdentifier());
		acceptToken(DOT);
		return result;
	}

	public InputOutputSection parseInputOutputSection() {
		if (!token.kind.isAnyOf(INPUT_OUTPUT)) return null;

		InputOutputSection result = new InputOutputSection();
		acceptToken(INPUT_OUTPUT);
		acceptToken(SECTION);
		acceptToken(DOT);
		result.fileControlParagraph(parseFileControlParagraph());
		result.ioControlParagraph(parseIoControlParagraph());
		return result;
	}

	public FileControlParagraph parseFileControlParagraph() {
		if (!token.kind.isAnyOf(FILE_CONTROL)) return null;

		FileControlParagraph result = new FileControlParagraph();
		acceptToken(FILE_CONTROL);
		acceptToken(DOT);
		result.fileControlEntryList(parseFileControlEntryList());
		return result;
	}

	public FileControlEntry parseFileControlEntry() {
		if (!token.kind.isAnyOf(SELECT)) return null;

		FileControlEntry result = new FileControlEntry();
		acceptToken(SELECT);
		optionalToken(OPTIONAL);
		result.fileName(parseIdentifier());
		switch (token.kind()) {
			case PASSWORD: 
				result.passwordClause(parsePasswordClause());
				break;
			case RELATIVE: 
				result.relativeKeyClause(parseRelativeKeyClause());
				break;
		}
		acceptToken(DOT);
		return result;
	}

	public PasswordClause parsePasswordClause() {
		if (!token.kind.isAnyOf(PASSWORD)) return null;

		PasswordClause result = new PasswordClause();
		acceptToken(PASSWORD);
		optionalToken(IS);
		result.dataName(parseIdentifier());
		return result;
	}

	public RelativeKeyClause parseRelativeKeyClause() {
		if (!token.kind.isAnyOf(RELATIVE)) return null;

		RelativeKeyClause result = new RelativeKeyClause();
		acceptToken(RELATIVE);
		optionalToken(KEY);
		optionalToken(IS);
		result.qualifiedDataName(parseIdentifier());
		return result;
	}

	public IoControlParagraph parseIoControlParagraph() {
		IoControlParagraph result = new IoControlParagraph();
		return result;
	}

	public DataDivision parseDataDivision() {
		if (!token.kind.isAnyOf(DATA)) return null;

		DataDivision result = new DataDivision();
		acceptToken(DATA);
		acceptToken(DIVISION);
		acceptToken(DOT);
		switch (token.kind()) {
			case FILE: 
				result.fileSection(parseFileSection());
				break;
			case WORKING_STORAGE: 
				result.workingStorageSection(parseWorkingStorageSection());
				break;
			case LINKAGE: 
				result.linkageSection(parseLinkageSection());
				break;
		}
		return result;
	}

	public WorkingStorageSection parseWorkingStorageSection() {
		if (!token.kind.isAnyOf(WORKING_STORAGE)) return null;

		WorkingStorageSection result = new WorkingStorageSection();
		acceptToken(WORKING_STORAGE);
		acceptToken(SECTION);
		acceptToken(DOT);
		result.dataDescriptionEntryList(parseDataDescriptionEntryList());
		return result;
	}

	public LinkageSection parseLinkageSection() {
		if (!token.kind.isAnyOf(LINKAGE)) return null;

		LinkageSection result = new LinkageSection();
		acceptToken(LINKAGE);
		acceptToken(SECTION);
		acceptToken(DOT);
		result.dataDescriptionEntryList(parseDataDescriptionEntryList());
		return result;
	}

	public FileSection parseFileSection() {
		if (!token.kind.isAnyOf(FILE)) return null;

		FileSection result = new FileSection();
		acceptToken(FILE);
		acceptToken(SECTION);
		acceptToken(DOT);
		return result;
	}

	public DataDescriptionEntry parseDataDescriptionEntry() {
		DataDescriptionEntry result = new DataDescriptionEntry();
		result.dataName(parseIdentifier());
		result.dummy1(parseIdentifier());
		result.dummy2(parseIdentifier());
		acceptToken(DOT);
		return result;
	}

	public DataPictureClause parseDataPictureClause() {
		if (!token.kind.isAnyOf(PICTURE, PIC)) return null;

		DataPictureClause result = new DataPictureClause();
		acceptAnyToken(PICTURE, PIC);
		optionalToken(IS);
		result.pictureString(parseIdentifier());
		return result;
	}

	public DataValueClause parseDataValueClause() {
		DataValueClause result = new DataValueClause();
		result.literal(parseIdentifier());
		return result;
	}

	public ProcedureDivision parseProcedureDivision() {
		if (!token.kind.isAnyOf(PROCEDURE)) return null;

		ProcedureDivision result = new ProcedureDivision();
		acceptToken(PROCEDURE);
		acceptToken(DIVISION);
		result.procedureUsingClause(parseProcedureUsingClause());
		result.procedureGivingClause(parseProcedureGivingClause());
		acceptToken(DOT);
		result.procedureDeclaratives(parseIdentifier());
		result.statementList(parseStatementList());
		return result;
	}

	public ProcedureGivingClause parseProcedureGivingClause() {
		if (!token.kind.isAnyOf(GIVING, RETURNING)) return null;

		ProcedureGivingClause result = new ProcedureGivingClause();
		acceptAnyToken(GIVING, RETURNING);
		result.dataName(parseIdentifier());
		return result;
	}

	public ProcedureUsingClause parseProcedureUsingClause() {
		if (!token.kind.isAnyOf(USING)) return null;

		ProcedureUsingClause result = new ProcedureUsingClause();
		acceptToken(USING);
		result.procedureParameter(parseIdentifier());
		return result;
	}

	public Statement parseStatement() {
		if (!token.kind.isAnyOf(IF, EXIT, STOP, CONTINUE)) return null;

		switch (token.kind()) {
			case IF: 
				return parseIfStatement();
			case EXIT: 
				return parseExitStatement();
			case STOP: 
				return parseStopStatement();
			case CONTINUE: 
		}
		return null;
	}

	public IfStatement parseIfStatement() {
		if (!token.kind.isAnyOf(IF)) return null;

		IfStatement result = new IfStatement();
		acceptToken(IF);
		result.condition(parseIdentifier());
		optionalToken(THEN);
		result.thenStatement(parseThenStatement());
		optionalToken(END_IF);
		return result;
	}

	public ThenStatement parseThenStatement() {
		if (!token.kind.isAnyOf(NEXT, IF, EXIT, STOP, CONTINUE)) return null;

		ThenStatement result = new ThenStatement();
		switch (token.kind()) {
			case NEXT: 
				acceptToken(NEXT);
				acceptToken(SENTENCE);
				break;
			case IF: 
			case EXIT: 
			case STOP: 
			case CONTINUE: 
				result.statementList(parseStatementList());
				break;
		}
		return result;
	}

	public ElseStatement parseElseStatement() {
		if (!token.kind.isAnyOf(NEXT, IF, EXIT, STOP, CONTINUE)) return null;

		ElseStatement result = new ElseStatement();
		switch (token.kind()) {
			case NEXT: 
				acceptToken(NEXT);
				acceptToken(SENTENCE);
				break;
			case IF: 
			case EXIT: 
			case STOP: 
			case CONTINUE: 
				result.statementList(parseStatementList());
				break;
		}
		return result;
	}

	public ExitStatement parseExitStatement() {
		if (!token.kind.isAnyOf(EXIT)) return null;

		ExitStatement result = new ExitStatement();
		acceptToken(EXIT);
		optionalToken(PROGRAM);
		return result;
	}

	public StopStatement parseStopStatement() {
		if (!token.kind.isAnyOf(STOP)) return null;

		StopStatement result = new StopStatement();
		acceptToken(STOP);
		acceptAnyToken(RUN, literal);
		return result;
	}

	public static List<CobolProgram> parseCobolProgramList() {
		List<CobolProgram> list = new ArrayList<>();
		while (true) {
			CobolProgram o = parseCobolProgram();
			if (o == null)
				break;
			list.add(o);
		}
		return list
	}

	public static List<IdDivisionOptionalParagraph> parseIdDivisionOptionalParagraphList() {
		List<IdDivisionOptionalParagraph> list = new ArrayList<>();
		while (true) {
			IdDivisionOptionalParagraph o = parseIdDivisionOptionalParagraph();
			if (o == null)
				break;
			list.add(o);
		}
		return list
	}

	public static List<FileControlEntry> parseFileControlEntryList() {
		List<FileControlEntry> list = new ArrayList<>();
		while (true) {
			FileControlEntry o = parseFileControlEntry();
			if (o == null)
				break;
			list.add(o);
		}
		return list
	}

	public static List<DataDescriptionEntry> parseDataDescriptionEntryList() {
		List<DataDescriptionEntry> list = new ArrayList<>();
		while (true) {
			DataDescriptionEntry o = parseDataDescriptionEntry();
			if (o == null)
				break;
			list.add(o);
		}
		return list
	}

			public static List<Statement> parseStatementList() {
				List<Statement> list = new ArrayList<>();
				while (true) {
					Statement o = parseStatement();
					if (o == null)
						break;
					list.add(o);
				}
				return list
			}

	protected Identifier parseIdentifier() {
		String lexeme = token.lexeme();
		acceptToken(IDENTIFIER);
		return Identifier.of(lexeme);
	}


	// -- token stream --

	CobolToken nextToken() {
		do {
			token = lexer.scanToken();
		} while (token.kind() == WHITE_SPACES
				|| token.kind() == NEW_LINE
				|| token.kind().tag() == COMMENT);
		return token;
	}

	CobolToken peekToken() {
		token = lexer.scanToken();
		return token;
	}

	boolean optionalToken(CobolTokenKind expected) {
		boolean accepted = token.kind() == expected;
		if (accepted)
			nextToken();
		return accepted;
	}

	boolean acceptToken(CobolTokenKind expected) {
		boolean accepted = token.kind() == expected;
		if (accepted)
			nextToken();
		else
			syntaxError("Token " + expected + " is expected");
		return accepted;
	}

	private void syntaxError(String hint) {
		logError(hint);
		nextToken();
	}

	private void logError(String hint) {
		System.out.println("syntax error: unsupported token " + token + " " + hint);
	}
}
