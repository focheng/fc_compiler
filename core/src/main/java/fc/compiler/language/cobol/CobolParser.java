package fc.compiler.language.cobol;

import fc.compiler.common.ast.AstNode;
import fc.compiler.common.ast.Statement;
import fc.compiler.common.ast.expression.Identifier;
import fc.compiler.common.parser.Parser;
import fc.compiler.common.parser.ParserBase;
import fc.compiler.common.parser.ParserRegistry;
import fc.compiler.common.parser.TokenReader;
import fc.compiler.common.token.Token;
import fc.compiler.language.cobol.ast.CharacterString;
import fc.compiler.language.cobol.ast.CobolProgram;
import fc.compiler.language.cobol.ast.division.*;
import fc.compiler.language.cobol.ast.statement.*;
import lombok.extern.slf4j.Slf4j;

import static fc.compiler.common.token.TokenKind.*;
import static fc.compiler.language.cobol.CobolTokenKind.*;

/**
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

		map.put(IF, ParserBase::parseIfStatement);

		map.put(PIC, CobolParser::parsePictureClause);
		map.put(PICTURE, CobolParser::parsePictureClause);
		map.put(VALUE, CobolParser::parseValueClause);
		map.put(DISPLAY, CobolParser::parseDisplayClause);

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
		reader.accept(IDENTIFICATION, ID);
		reader.accept(DIVISION);
		reader.accept(SEPARATOR_PERIOD);

		// "PROGRAM-ID" [ "." ] program-name [ [ "IS" ] "INITIAL" [ "PROGRAM" ] ] [ "." ]
		reader.accept(PROGRAM_ID);
		reader.accept(SEPARATOR_PERIOD);
		Identifier programName = parseIdentifier(reader, registry);
		reader.accept(SEPARATOR_PERIOD);

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
		reader.accept(SEPARATOR_PERIOD);

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
		reader.accept(PICTURE, PIC);
		reader.optional(IS);
		CharacterString picString = parseCharacterString(reader, registry);
		return new PictureClause().picString(picString);
	}

	public static CharacterString parseCharacterString(TokenReader reader, ParserRegistry registry) {
		StringBuilder sb = new StringBuilder();
		Token t = null;
		while ((t = reader.optionalAndReturn(IDENTIFIER, NUMBER_LITERAL, LEFT_PAREN, RIGHT_PAREN)) != null) {
			sb.append(t.lexeme());
		}
		return new CharacterString().format(sb.toString());
	}

	public static ValueClause parseValueClause(TokenReader reader, ParserRegistry registry) {
		if (reader.optional(VALUE)) {
			Token t = reader.acceptAndReturn(NUMBER_LITERAL, ZERO, ZEROS, ZEROES, SPACE, SPACES);
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

		while (!reader.optional(EOF) && !reader.optionalNextTokens(END, PROGRAM)) {
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
		reader.accept(DISPLAY);
		Token token = reader.acceptAndReturn(STRING_LITERAL);
		reader.accept(SEPARATOR_PERIOD);
		return new DisplayClause().value(token.lexeme());
	}

	public static boolean isNewDivision(TokenReader reader) {
		Token token1 = reader.peekToken(1);
		Token token2 = reader.peekToken(2);
		return token2.kind() == DIVISION
				&& (token1.kind() == ENVIRONMENT || token1.kind() == DATA || token1.kind() == PROCEDURE);
	}

	public static Identifier parseIdentifier(TokenReader reader, ParserRegistry registry) {
		Token token = reader.acceptAndReturn(IDENTIFIER);
		if (token != null) {
			return new Identifier().id(token.lexeme());
		} else {
			syntaxError(reader, "failed to parse identifier");
			return null;
		}
	}
}
