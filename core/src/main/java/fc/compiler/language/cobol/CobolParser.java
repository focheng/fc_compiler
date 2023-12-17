package fc.compiler.language.cobol;

import fc.compiler.common.ast.AstNode;
import fc.compiler.common.ast.Statement;
import fc.compiler.common.ast.expression.Identifier;
import fc.compiler.common.ast.statement.CompositeStatement;
import fc.compiler.common.parser.Parser;
import fc.compiler.common.parser.ParserBase;
import fc.compiler.common.parser.ParserRegistry;
import fc.compiler.common.parser.TokenReader;
import fc.compiler.common.token.Token;
import fc.compiler.language.cobol.ast.division.*;
import fc.compiler.language.cobol.ast.statement.PictureClause;
import lombok.extern.slf4j.Slf4j;

import javax.xml.crypto.Data;
import java.util.HashMap;
import java.util.Map;

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

		return map;
	}

	public static CompositeStatement parseCompilationUnit(TokenReader reader, ParserRegistry registry) {
		CompositeStatement program = new CompositeStatement();
		program.getStatements().add(parseIdDivision(reader, registry));
		program.getStatements().add(parseDataDivision(reader, registry));
		return program;
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

	public static DataDivision parseDataDivision(TokenReader reader, ParserRegistry registry) {
		// "DATA" "DIVISION" "."
		if (!reader.acceptMultiTokens(DATA, DIVISION, SEPARATOR_PERIOD)) {
			return null;
		}

		FileSection fileSection = parseFileSection(reader, registry);
		WorkingStorageSection workingStorageSection = parseWorkingStorageSection(reader, registry);
		LinkageSection linkageSection = parseLinkageSection(reader, registry);
		return new DataDivision().fileSection(fileSection)
				.workingStorageSection(workingStorageSection)
				.linkageSection(linkageSection);
	}

	/** [ "FILE" "SECTION" "." { file-and-sort-description-entry { record-description-entry }+ }* ] */
	public static FileSection parseFileSection(TokenReader reader, ParserRegistry registry) {
		if (!reader.acceptMultiTokens(FILE, SECTION, SEPARATOR_PERIOD)) {
			return null;
		}
		return new FileSection();
	}

	/** [ "WORKING-STORAGE" "SECTION" "." { ( record-description-entry | data-item-description-entry ) }* ] */
	public static WorkingStorageSection parseWorkingStorageSection(TokenReader reader, ParserRegistry registry) {
		if (!reader.acceptMultiTokens(WORKING_STORAGE, SECTION, SEPARATOR_PERIOD)) {
			return null;
		}

		Statement stmt = parseDataDescriptionEntry(reader, registry);

		return new WorkingStorageSection();
	}

	/**  ["LINKAGE" "SECTION" "." { ( record-description-entry | data-item-description-entry ) }* ] */
	public static LinkageSection parseLinkageSection(TokenReader reader, ParserRegistry registry) {
		if (!reader.acceptMultiTokens(LINKAGE, SECTION, SEPARATOR_PERIOD)) {
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
	public static Statement parseDataDescriptionEntry(TokenReader reader, ParserRegistry registry) {
		if (!acceptLevelNumber(reader)) return null;

		Token dataName = reader.acceptAndReturn(IDENTIFIER, FILLER);
		if (dataName != null) {
			log.info("data-name is " + dataName);
		}

//		while (reader.getToken().getKind() != SEPARATOR_PERIOD) {
//			Parser parser = registry.get(reader.getToken().getKind());
//			if (parser != null) {
//				AstNode node = parser.parse(reader, registry);
//				if (node instanceof Statement) {
//
//				} else {
//					syntaxError(reader, "");
//				}
//			}
//			reader.nextToken();
//		}
//		reader.accept(SEPARATOR_PERIOD);

		return new DataDescriptionEntry();
	}

	private static boolean acceptLevelNumber(TokenReader reader) {
		Token levelNumber = reader.getToken();
		if (!levelNumber.getKind().equals(NUMBER_LITERAL)
				|| levelNumber.getLexeme().length() != 2) {
			return false;
		}

		log.info("levelNumber is " + levelNumber);
		int n = Integer.parseInt(levelNumber.getLexeme());
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
	 public static Statement parsePictureClause(TokenReader reader, ParserRegistry registry) {
		reader.accept(PICTURE, PIC);
		reader.optional(IS);

		// parse picture-string

		return new PictureClause();
	}

	public static boolean isNewDivision(TokenReader reader) {
		Token token1 = reader.peekToken(1);
		Token token2 = reader.peekToken(2);
		return token2.getKind() == DIVISION
				&& (token1.getKind() == ENVIRONMENT || token1.getKind() == DATA || token1.getKind() == PROCEDURE);
	}

	public static Identifier parseIdentifier(TokenReader reader, ParserRegistry registry) {
		Token token = reader.acceptAndReturn(IDENTIFIER);
		if (token != null) {
			return new Identifier().id(token.getLexeme());
		} else {
			syntaxError(reader, "failed to parse identifier");
			return null;
		}
	}
}
