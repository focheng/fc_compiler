/** COBOL 85 Grammar for FCC.{{{
 *
 * Code Hierarchical Organizations
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
 * Within the PROCEDURE DIVISION, a procedure consists of a section or a group of sections,
 * and a paragraph or group of paragraphs.
 *
 * statements can be divided into 4 categories:
 * - imperative statement: either specifies an unconditional action to be taken by the program,
 *                      or is a conditional statement terminated by its explicit scope terminator.
 *      - Arithmetic: COMPUTE, ADD, SUBTRACT, MULTIPLY, DIVIDE
 *      - Data movement: MOVE, SET, STRING, UNSTRING, ...
 *      - Input-Output: READ, WRITE,
 *      - Ending: STOP RUN, EXIT PROGRAM, EXIT METHOD, GOBACK
 *      - Procedure-branching: GO TO, PERFORM, CONTINUE, ALTER
 *      - Program or method linkage: CALL, CANCEL, INVOKE
 *      - Table-handling: SET
 * - conditional statement: specifies that the truth value of a condition is to be determined
 *              and that the subsequent action of the object program is dependent on this truth value.
 *      - Decision: IF, EVALUATE
 *      - Arithmetic: (COMPUTE | ADD | SUBTRACT | MULTIPLY | DIVIDE) ... [NOT] ON SIZE ERROR
 *      - Data movement: (STRING | UNSTRING) ... [NOT] ON OVERFLOW
 *      - Input-output: READ ... AT END
 *      - Program or method linkage: CALL ... ON OVERFLOW
 *      - Table-handling: SEARCH
 * - delimited scope statement:  uses an explicit scope terminator to turn a conditional statement
 *              into an imperative statement.
 *      - Explicit scope terminator: END-IF, END-EVALUATE, END-PERFORM, ...
 *      - Implicit scope terminator: is a separator period that terminates the scope of
 *                  all previous statements not yet terminated at the end of any sentence.
 * - compiler-directing statement: causes the compiler to take a specific action during compilation time.
 *      e.g. copy statement
 *
 * Expression can be divided into 2 categories:
 * - Arithmetic expressions are used as operands of certain conditional and arithmetic statements.
 * - Conditional expression causes the object program to select alternative paths of control,
 * depending on the truth value of a test. specified in EVALUATE, IF, PERFORM, and SEARCH statements.
 *
 * @author FC
 *//*}}}*/
grammar Cobol85;

// Parser rules ==================================={{{1

// --- cobolCompilationUnit -------------------------------------------{{{1
/** A program contained within another program is called a "nested program".
 * A nested program can itself contain a nested program.
 * The outermost program is called the "compilation unit".
 */
cobolCompilationUnit : cobolProgram+;
cobolProgram : idDivision environmentDivision? dataDivision? procedureDivision? cobolProgram* endProgram?;
endProgram : END PROGRAM programName '.' ;

// --- identification division ----------------------------------------------------------------{{{1

idDivision : (IDENTIFICATION | ID) DIVISION '.' programIdParagraph idDivisionOptionalParagraph* ;

programIdParagraph : PROGRAM_ID '.' programName '.';
//programIdParagraph
//    : PROGRAM_ID '.' programName (
//        IS? (COMMON | INITIAL | LIBRARY | DEFINITION | RECURSIVE) PROGRAM?
//    )? '.'? commentEntry?
//    ;

idDivisionOptionalParagraph:
    ( 'AUTHOR'
    | 'INSTALLATION'
    | 'DATE-WRITTEN'
    | 'DATE-COMPILED'
    | 'SECURITY'
    | 'REMARKS'
    )
//    ) '.' commentEntry*
    ;

/**
 * A comment entry is an IDENTIFICATION DIVISION entry.
 * It consists of any characters belonging to the computer character set.
 * A comment entry is an obsolete element. Avoid using this element when creating new programs.
 */
//commentEntry : COMMENTENTRYLINE+ ;
//COMMENTENTRYLINE : COMMENTENTRYTAG WS ~('\n' | '\r')* ;
//COMMENTENTRYTAG : '*>CE' ;



// --- environment division ----------------------------------------------------------------{{{1

environmentDivision : ENVIRONMENT DIVISION '.' configurationSection? inputOutputSection?;
//environmentDivision : ENVIRONMENT DIVISION '.' environmentDivisionBody* ;

//environmentDivisionBody
//    : configurationSection
//    | specialNamesParagraph
//    | inputOutputSection
//    ;
//
// -- configuration section ------------------------------{{{2

configurationSection : CONFIGURATION SECTION '.' sourceComputerParagraph? objectComputerParagraph?;
//configurationSection : CONFIGURATION SECTION '.' sourceComputerParagraph? objectComputerParagraph? specialNamesParagraph?;
//    // strictly, specialNamesParagraph does not belong into configurationSectionParagraph, but ibm-cobol allows this

sourceComputerParagraph : SOURCE_COMPUTER '.' computerName (WITH? DEBUGGING MODE)? '.' ;
objectComputerParagraph : OBJECT_COMPUTER '.' computerName  '.' ;
//objectComputerParagraph : OBJECT_COMPUTER '.' computerName objectComputerClause* '.' ;

/*
objectComputerClause
    : memorySizeClause
    | diskSizeClause
    | collatingSequenceClause
    | segmentLimitClause
    | characterSetClause
    ;

memorySizeClause : MEMORY SIZE? (integerLiteral | cobolWord) (WORDS | CHARACTERS | MODULES)?  ;
diskSizeClause : DISK SIZE? IS? (integerLiteral | cobolWord) (WORDS | MODULES)?  ;
collatingSequenceClause : PROGRAM? COLLATING? SEQUENCE (IS? alphabetName+) collatingSequenceClauseAlphanumeric? collatingSequenceClauseNational?  ;
collatingSequenceClauseAlphanumeric : FOR? ALPHANUMERIC IS? alphabetName ;
collatingSequenceClauseNational : FOR? NATIONAL IS? alphabetName ;
segmentLimitClause : SEGMENT_LIMIT IS? integerLiteral ;
characterSetClause : CHARACTER SET '.' ;
*/

//// - special names paragraph ----------------------------------

//specialNamesParagraph : SPECIAL_NAMES '.' (specialNameClause+ '.')?  ;

//specialNameClause
//    : channelClause
//    | odtClause
//    | alphabetClause
//    | classClause
//    | currencySignClause
//    | decimalPointClause
//    | symbolicCharactersClause
//    | environmentSwitchNameClause
//    | defaultDisplaySignClause
//    | defaultComputationalSignClause
//    | reserveNetworkClause
//    ;
//
//alphabetClause
//    : alphabetClauseFormat1
//    | alphabetClauseFormat2
//    ;
//
//alphabetClauseFormat1
//    : ALPHABET alphabetName (FOR ALPHANUMERIC)? IS? (
//        EBCDIC
//        | ASCII
//        | STANDARD_1
//        | STANDARD_2
//        | NATIVE
//        | cobolWord
//        | alphabetLiterals+
//    )
//    ;
//
//alphabetLiterals
//    : literal (alphabetThrough | alphabetAlso+)?
//    ;
//
//alphabetThrough
//    : (THROUGH | THRU) literal
//    ;
//
//alphabetAlso
//    : ALSO literal+
//    ;
//
//alphabetClauseFormat2
//    : ALPHABET alphabetName FOR? NATIONAL IS? (NATIVE | CCSVERSION literal)
//    ;
//
//channelClause
//    : CHANNEL integerLiteral IS? mnemonicName
//    ;
//
//classClause
//    : CLASS className (FOR? (ALPHANUMERIC | NATIONAL))? IS? classClauseThrough+
//    ;
//
//classClauseThrough
//    : classClauseFrom ((THROUGH | THRU) classClauseTo)?
//    ;
//
//classClauseFrom
//    : identifier
//    | literal
//    ;
//
//classClauseTo
//    : identifier
//    | literal
//    ;
//
//currencySignClause
//    : CURRENCY SIGN? IS? literal (WITH? PICTURE SYMBOL literal)?
//    ;
//
//decimalPointClause
//    : DECIMAL_POINT IS? COMMA
//    ;
//
//defaultComputationalSignClause
//    : DEFAULT (COMPUTATIONAL | COMP)? (SIGN IS?)? (LEADING | TRAILING)? (SEPARATE CHARACTER?)
//    ;
//
//defaultDisplaySignClause
//    : DEFAULT_DISPLAY (SIGN IS?)? (LEADING | TRAILING) (SEPARATE CHARACTER?)?
//    ;
//
//environmentSwitchNameClause
//    : environmentName IS? mnemonicName environmentSwitchNameSpecialNamesStatusPhrase?
//    | environmentSwitchNameSpecialNamesStatusPhrase
//    ;
//
//environmentSwitchNameSpecialNamesStatusPhrase
//    : ON STATUS? IS? condition (OFF STATUS? IS? condition)?
//    | OFF STATUS? IS? condition (ON STATUS? IS? condition)?
//    ;
//
//odtClause
//    : ODT IS? mnemonicName
//    ;
//
//reserveNetworkClause
//    : RESERVE WORDS? LIST? IS? NETWORK CAPABLE?
//    ;
//
//symbolicCharactersClause
//    : SYMBOLIC CHARACTERS? (FOR? (ALPHANUMERIC | NATIONAL))? symbolicCharacters+ (IN alphabetName)?
//    ;
//
//symbolicCharacters
//    : symbolicCharacter+ (IS | ARE)? integerLiteral+
//    ;
//
// -- input output section ------------------------------{{{2

inputOutputSection : INPUT_OUTPUT SECTION '.' fileControlParagraph? ioControlParagraph? ;

// - file control paragraph ------------------------------{{{3

fileControlParagraph : FILE_CONTROL '.' fileControlEntry*;
fileControlEntry : SELECT OPTIONAL? fileName (
//      assignClause
//    | reserveClause
//    | organizationClause
//    | paddingCharacterClause
//    | recordDelimiterClause
//    | accessModeClause
//    | recordKeyClause
//    | alternateRecordKeyClause
//    | fileStatusClause
    passwordClause
    | relativeKeyClause
    )
    '.'
    ;

//assignClause: ASSIGN TO? ( DISK | DISPLAY | KEYBOARD | PORT | PRINTER | READER | REMOTE | TAPE | VIRTUAL | assignmentName | literal) ;
//reserveClause : RESERVE (NO | integerLiteral) ALTERNATE? (AREA | AREAS)?  ;
//organizationClause : (ORGANIZATION IS?)? (LINE | RECORD BINARY | RECORD | BINARY)? ( SEQUENTIAL | RELATIVE | INDEXED) ;
//paddingCharacterClause : PADDING CHARACTER? IS? (qualifiedDataName | literal) ;
//recordDelimiterClause : RECORD DELIMITER IS? (STANDARD_1 | IMPLICIT | assignmentName) ;
//accessModeClause : ACCESS MODE? IS? (SEQUENTIAL | RANDOM | DYNAMIC | EXCLUSIVE) ;
//recordKeyClause : RECORD KEY? IS? qualifiedDataName passwordClause? (WITH? DUPLICATES)?  ;
//alternateRecordKeyClause : ALTERNATE RECORD KEY? IS? qualifiedDataName passwordClause? (WITH? DUPLICATES)?  ;
passwordClause : PASSWORD IS? dataName ;
//fileStatusClause : FILE? STATUS IS? qualifiedDataName qualifiedDataName?  ;
relativeKeyClause : RELATIVE KEY? IS? qualifiedDataName ;

// - io control paragraph ------------------------------{{{3

ioControlParagraph: ;
//ioControlParagraph : I_O_CONTROL '.' (fileName '.')? (ioControlClause* '.')?  ;
//ioControlClause
//    : rerunClause
//    | sameClause
//    | multipleFileClause
//    | commitmentControlClause
//    ;
//
//rerunClause
//    : RERUN (ON (assignmentName | fileName))? EVERY (
//        rerunEveryRecords
//        | rerunEveryOf
//        | rerunEveryClock
//    )
//    ;
//
//rerunEveryRecords
//    : integerLiteral RECORDS
//    ;
//
//rerunEveryOf
//    : END? OF? (REEL | UNIT) OF fileName
//    ;
//
//rerunEveryClock
//    : integerLiteral CLOCK_UNITS?
//    ;
//
//sameClause
//    : SAME (RECORD | SORT | SORT_MERGE)? AREA? FOR? fileName+
//    ;
//
//multipleFileClause
//    : MULTIPLE FILE TAPE? CONTAINS? multipleFilePosition+
//    ;
//
//multipleFilePosition
//    : fileName (POSITION integerLiteral)?
//    ;
//
//commitmentControlClause
//    : COMMITMENT CONTROL FOR? fileName
//    ;
//
// --- data division ----------------------------------------------------------------{{{1

dataDivision : DATA DIVISION '.'
    ( fileSection?
//    | dataBaseSection?
    | workingStorageSection?
//    | localStorageSection?
    | linkageSection?
//    | communicationSection?
//    | screenSection?
//    | reportSection?
//    | programLibrarySection?
    );

workingStorageSection : WORKING_STORAGE SECTION '.' dataDescriptionEntry* ;
linkageSection : 	LINKAGE 	SECTION '.' dataDescriptionEntry* ;
//localStorageSection :   LOCAL_STORAGE	SECTION '.' (LD localName '.')? dataDescriptionEntry* ;

// -- file section ----------------------------------

fileSection : FILE SECTION '.' (fileDescriptionEntry | sortFileDescriptionEntry)* ;
//fileDescriptionEntry : (FD | SD) fileName ('.'? fileDescriptionEntryClause)* '.' dataDescriptionEntry* ;
//fileDescriptionEntryClause
//    : externalClause
//    | globalClause
//    | blockContainsClause
//    | recordContainsClause
//    | labelRecordsClause
//    | valueOfClause
//    | dataRecordsClause
//    | linageClause
//    | codeSetClause
//    | reportClause
//    | recordingModeClause
//    ;
//
//externalClause
//    : IS? EXTERNAL
//    ;
//
//globalClause
//    : IS? GLOBAL
//    ;
//
//blockContainsClause
//    : BLOCK CONTAINS? integerLiteral blockContainsTo? (RECORDS | CHARACTERS)?
//    ;
//
//blockContainsTo
//    : TO integerLiteral
//    ;
//
//recordContainsClause
//    : RECORD (
//        recordContainsClauseFormat1
//        | recordContainsClauseFormat2
//        | recordContainsClauseFormat3
//    )
//    ;
//
//recordContainsClauseFormat1
//    : CONTAINS? integerLiteral CHARACTERS?
//    ;
//
//recordContainsClauseFormat2
//    : IS? VARYING IN? SIZE? (FROM? integerLiteral recordContainsTo? CHARACTERS?)? (
//        DEPENDING ON? qualifiedDataName
//    )?
//    ;
//
//recordContainsClauseFormat3
//    : CONTAINS? integerLiteral recordContainsTo CHARACTERS?
//    ;
//
//recordContainsTo
//    : TO integerLiteral
//    ;
//
//labelRecordsClause
//    : LABEL (RECORD IS? | RECORDS ARE?) (OMITTED | STANDARD | dataName+)
//    ;
//
//valueOfClause
//    : VALUE OF valuePair+
//    ;
//
//valuePair
//    : systemName IS? (qualifiedDataName | literal)
//    ;
//
//dataRecordsClause
//    : DATA (RECORD IS? | RECORDS ARE?) dataName+
//    ;
//
//linageClause
//    : LINAGE IS? (dataName | integerLiteral) LINES? linageAt*
//    ;
//
//linageAt
//    : linageFootingAt
//    | linageLinesAtTop
//    | linageLinesAtBottom
//    ;
//
//linageFootingAt
//    : WITH? FOOTING AT? (dataName | integerLiteral)
//    ;
//
//linageLinesAtTop
//    : LINES? AT? TOP (dataName | integerLiteral)
//    ;
//
//linageLinesAtBottom
//    : LINES? AT? BOTTOM (dataName | integerLiteral)
//    ;
//
//recordingModeClause
//    : RECORDING MODE? IS? modeStatement
//    ;
//
//modeStatement
//    : cobolWord
//    ;
//
//codeSetClause
//    : CODE_SET IS? alphabetName
//    ;
//
//reportClause
//    : (REPORT IS? | REPORTS ARE?) reportName+
//    ;
//
//// -- data base section ----------------------------------
//
//dataBaseSection
//    : DATA_BASE SECTION '.' dataBaseSectionEntry*
//    ;
//
//dataBaseSectionEntry
//    : integerLiteral literal INVOKE literal
//    ;
//

//// -- communication section ----------------------------------
//
//communicationSection
//    : COMMUNICATION SECTION '.' (communicationDescriptionEntry | dataDescriptionEntry)*
//    ;
//
//communicationDescriptionEntry
//    : communicationDescriptionEntryFormat1
//    | communicationDescriptionEntryFormat2
//    | communicationDescriptionEntryFormat3
//    ;
//
//communicationDescriptionEntryFormat1
//    : CD cdName FOR? INITIAL? INPUT (
//        (
//            symbolicQueueClause
//            | symbolicSubQueueClause
//            | messageDateClause
//            | messageTimeClause
//            | symbolicSourceClause
//            | textLengthClause
//            | endKeyClause
//            | statusKeyClause
//            | messageCountClause
//        )
//        | dataDescName
//    )* '.'
//    ;
//
//communicationDescriptionEntryFormat2
//    : CD cdName FOR? OUTPUT (
//        destinationCountClause
//        | textLengthClause
//        | statusKeyClause
//        | destinationTableClause
//        | errorKeyClause
//        | symbolicDestinationClause
//    )* '.'
//    ;
//
//communicationDescriptionEntryFormat3
//    : CD cdName FOR? INITIAL I_O (
//        (
//            messageDateClause
//            | messageTimeClause
//            | symbolicTerminalClause
//            | textLengthClause
//            | endKeyClause
//            | statusKeyClause
//        )
//        | dataDescName
//    )* '.'
//    ;
//
//destinationCountClause
//    : DESTINATION COUNT IS? dataDescName
//    ;
//
//destinationTableClause
//    : DESTINATION TABLE OCCURS integerLiteral TIMES (INDEXED BY indexName+)?
//    ;
//
//endKeyClause
//    : END KEY IS? dataDescName
//    ;
//
//errorKeyClause
//    : ERROR KEY IS? dataDescName
//    ;
//
//messageCountClause
//    : MESSAGE? COUNT IS? dataDescName
//    ;
//
//messageDateClause
//    : MESSAGE DATE IS? dataDescName
//    ;
//
//messageTimeClause
//    : MESSAGE TIME IS? dataDescName
//    ;
//
//statusKeyClause
//    : STATUS KEY IS? dataDescName
//    ;
//
//symbolicDestinationClause
//    : SYMBOLIC? DESTINATION IS? dataDescName
//    ;
//
//symbolicQueueClause
//    : SYMBOLIC? QUEUE IS? dataDescName
//    ;
//
//symbolicSourceClause
//    : SYMBOLIC? SOURCE IS? dataDescName
//    ;
//
//symbolicTerminalClause
//    : SYMBOLIC? TERMINAL IS? dataDescName
//    ;
//
//symbolicSubQueueClause
//    : SYMBOLIC? (SUB_QUEUE_1 | SUB_QUEUE_2 | SUB_QUEUE_3) IS? dataDescName
//    ;
//
//textLengthClause
//    : TEXT LENGTH IS? dataDescName
//    ;
//
//// -- screen section ----------------------------------
//
//screenSection
//    : SCREEN SECTION '.' screenDescriptionEntry*
//    ;
//
//screenDescriptionEntry
//    : INTEGERLITERAL (FILLER | screenName)? (
//        screenDescriptionBlankClause
//        | screenDescriptionBellClause
//        | screenDescriptionBlinkClause
//        | screenDescriptionEraseClause
//        | screenDescriptionLightClause
//        | screenDescriptionGridClause
//        | screenDescriptionReverseVideoClause
//        | screenDescriptionUnderlineClause
//        | screenDescriptionSizeClause
//        | screenDescriptionLineClause
//        | screenDescriptionColumnClause
//        | screenDescriptionForegroundColorClause
//        | screenDescriptionBackgroundColorClause
//        | screenDescriptionControlClause
//        | screenDescriptionValueClause
//        | screenDescriptionPictureClause
//        | (screenDescriptionFromClause | screenDescriptionUsingClause)
//        | screenDescriptionUsageClause
//        | screenDescriptionBlankWhenZeroClause
//        | screenDescriptionJustifiedClause
//        | screenDescriptionSignClause
//        | screenDescriptionAutoClause
//        | screenDescriptionSecureClause
//        | screenDescriptionRequiredClause
//        | screenDescriptionPromptClause
//        | screenDescriptionFullClause
//        | screenDescriptionZeroFillClause
//    )* '.'
//    ;
//
//screenDescriptionBlankClause
//    : BLANK (SCREEN | LINE)
//    ;
//
//screenDescriptionBellClause
//    : BELL
//    | BEEP
//    ;
//
//screenDescriptionBlinkClause
//    : BLINK
//    ;
//
//screenDescriptionEraseClause
//    : ERASE (EOL | EOS)
//    ;
//
//screenDescriptionLightClause
//    : HIGHLIGHT
//    | LOWLIGHT
//    ;
//
//screenDescriptionGridClause
//    : GRID
//    | LEFTLINE
//    | OVERLINE
//    ;
//
//screenDescriptionReverseVideoClause
//    : REVERSE_VIDEO
//    ;
//
//screenDescriptionUnderlineClause
//    : UNDERLINE
//    ;
//
//screenDescriptionSizeClause
//    : SIZE IS? (identifier | integerLiteral)
//    ;
//
//screenDescriptionLineClause
//    : LINE (NUMBER? IS? (PLUS | PLUSCHAR | MINUSCHAR))? (identifier | integerLiteral)
//    ;
//
//screenDescriptionColumnClause
//    : (COLUMN | COL) (NUMBER? IS? (PLUS | PLUSCHAR | MINUSCHAR))? (identifier | integerLiteral)
//    ;
//
//screenDescriptionForegroundColorClause
//    : (FOREGROUND_COLOR | FOREGROUND_COLOUR) IS? (identifier | integerLiteral)
//    ;
//
//screenDescriptionBackgroundColorClause
//    : (BACKGROUND_COLOR | BACKGROUND_COLOUR) IS? (identifier | integerLiteral)
//    ;
//
//screenDescriptionControlClause
//    : CONTROL IS? identifier
//    ;
//
//screenDescriptionValueClause
//    : (VALUE IS?) literal
//    ;
//
//screenDescriptionPictureClause
//    : (PICTURE | PIC) IS? pictureString
//    ;
//
//screenDescriptionFromClause
//    : FROM (identifier | literal) screenDescriptionToClause?
//    ;
//
//screenDescriptionToClause
//    : TO identifier
//    ;
//
//screenDescriptionUsingClause
//    : USING identifier
//    ;
//
//screenDescriptionUsageClause
//    : (USAGE IS?) (DISPLAY | DISPLAY_1)
//    ;
//
//screenDescriptionBlankWhenZeroClause
//    : BLANK WHEN? ZERO
//    ;
//
//screenDescriptionJustifiedClause
//    : (JUSTIFIED | JUST) RIGHT?
//    ;
//
//screenDescriptionSignClause
//    : (SIGN IS?)? (LEADING | TRAILING) (SEPARATE CHARACTER?)?
//    ;
//
//screenDescriptionAutoClause
//    : AUTO
//    | AUTO_SKIP
//    ;
//
//screenDescriptionSecureClause
//    : SECURE
//    | NO_ECHO
//    ;
//
//screenDescriptionRequiredClause
//    : REQUIRED
//    | EMPTY_CHECK
//    ;
//
//screenDescriptionPromptClause
//    : PROMPT CHARACTER? IS? (identifier | literal) screenDescriptionPromptOccursClause?
//    ;
//
//screenDescriptionPromptOccursClause
//    : OCCURS integerLiteral TIMES?
//    ;
//
//screenDescriptionFullClause
//    : FULL
//    | LENGTH_CHECK
//    ;
//
//screenDescriptionZeroFillClause
//    : ZERO_FILL
//    ;
//
//// -- report section ----------------------------------
//
//reportSection
//    : REPORT SECTION '.' reportDescription*
//    ;
//
//reportDescription
//    : reportDescriptionEntry reportGroupDescriptionEntry+
//    ;
//
//reportDescriptionEntry
//    : RD reportName reportDescriptionGlobalClause? (
//        reportDescriptionPageLimitClause reportDescriptionHeadingClause? reportDescriptionFirstDetailClause? reportDescriptionLastDetailClause?
//            reportDescriptionFootingClause?
//    )? '.'
//    ;
//
//reportDescriptionGlobalClause
//    : IS? GLOBAL
//    ;
//
//reportDescriptionPageLimitClause
//    : PAGE (LIMIT IS? | LIMITS ARE?)? integerLiteral (LINE | LINES)?
//    ;
//
//reportDescriptionHeadingClause
//    : HEADING integerLiteral
//    ;
//
//reportDescriptionFirstDetailClause
//    : FIRST DETAIL integerLiteral
//    ;
//
//reportDescriptionLastDetailClause
//    : LAST DETAIL integerLiteral
//    ;
//
//reportDescriptionFootingClause
//    : FOOTING integerLiteral
//    ;
//
//reportGroupDescriptionEntry
//    : reportGroupDescriptionEntryFormat1
//    | reportGroupDescriptionEntryFormat2
//    | reportGroupDescriptionEntryFormat3
//    ;
//
//reportGroupDescriptionEntryFormat1
//    : integerLiteral dataName reportGroupLineNumberClause? reportGroupNextGroupClause? reportGroupTypeClause reportGroupUsageClause? '.'
//    ;
//
//reportGroupDescriptionEntryFormat2
//    : integerLiteral dataName? reportGroupLineNumberClause? reportGroupUsageClause '.'
//    ;
//
//reportGroupDescriptionEntryFormat3
//    : integerLiteral dataName? (
//        reportGroupPictureClause
//        | reportGroupUsageClause
//        | reportGroupSignClause
//        | reportGroupJustifiedClause
//        | reportGroupBlankWhenZeroClause
//        | reportGroupLineNumberClause
//        | reportGroupColumnNumberClause
//        | (
//            reportGroupSourceClause
//            | reportGroupValueClause
//            | reportGroupSumClause
//            | reportGroupResetClause
//        )
//        | reportGroupIndicateClause
//    )* '.'
//    ;
//
//reportGroupBlankWhenZeroClause
//    : BLANK WHEN? ZERO
//    ;
//
//reportGroupColumnNumberClause
//    : COLUMN NUMBER? IS? integerLiteral
//    ;
//
//reportGroupIndicateClause
//    : GROUP INDICATE?
//    ;
//
//reportGroupJustifiedClause
//    : (JUSTIFIED | JUST) RIGHT?
//    ;
//
//reportGroupLineNumberClause
//    : LINE? NUMBER? IS? (reportGroupLineNumberNextPage | reportGroupLineNumberPlus)
//    ;
//
//reportGroupLineNumberNextPage
//    : integerLiteral (ON? NEXT PAGE)?
//    ;
//
//reportGroupLineNumberPlus
//    : PLUS integerLiteral
//    ;
//
//reportGroupNextGroupClause
//    : NEXT GROUP IS? (integerLiteral | reportGroupNextGroupNextPage | reportGroupNextGroupPlus)
//    ;
//
//reportGroupNextGroupPlus
//    : PLUS integerLiteral
//    ;
//
//reportGroupNextGroupNextPage
//    : NEXT PAGE
//    ;
//
//reportGroupPictureClause
//    : (PICTURE | PIC) IS? pictureString
//    ;
//
//reportGroupResetClause
//    : RESET ON? (FINAL | dataName)
//    ;
//
//reportGroupSignClause
//    : SIGN IS? (LEADING | TRAILING) SEPARATE CHARACTER?
//    ;
//
//reportGroupSourceClause
//    : SOURCE IS? identifier
//    ;
//
//reportGroupSumClause
//    : SUM identifier (COMMACHAR? identifier)* (UPON dataName (COMMACHAR? dataName)*)?
//    ;
//
//reportGroupTypeClause
//    : TYPE IS? (
//        reportGroupTypeReportHeading
//        | reportGroupTypePageHeading
//        | reportGroupTypeControlHeading
//        | reportGroupTypeDetail
//        | reportGroupTypeControlFooting
//        | reportGroupTypePageFooting
//        | reportGroupTypeReportFooting
//    )
//    ;
//
//reportGroupTypeReportHeading
//    : REPORT HEADING
//    | RH
//    ;
//
//reportGroupTypePageHeading
//    : PAGE HEADING
//    | PH
//    ;
//
//reportGroupTypeControlHeading
//    : (CONTROL HEADING | CH) (FINAL | dataName)
//    ;
//
//reportGroupTypeDetail
//    : DETAIL
//    | DE
//    ;
//
//reportGroupTypeControlFooting
//    : (CONTROL FOOTING | CF) (FINAL | dataName)
//    ;
//
//reportGroupUsageClause
//    : (USAGE IS?)? (DISPLAY | DISPLAY_1)
//    ;
//
//reportGroupTypePageFooting
//    : PAGE FOOTING
//    | PF
//    ;
//
//reportGroupTypeReportFooting
//    : REPORT FOOTING
//    | RF
//    ;
//
//reportGroupValueClause
//    : VALUE IS? literal
//    ;
//
//// -- program library section ----------------------------------
//
//programLibrarySection
//    : PROGRAM_LIBRARY SECTION '.' libraryDescriptionEntry*
//    ;
//
//libraryDescriptionEntry
//    : libraryDescriptionEntryFormat1
//    | libraryDescriptionEntryFormat2
//    ;
//
//libraryDescriptionEntryFormat1
//    : LD libraryName EXPORT libraryAttributeClauseFormat1? libraryEntryProcedureClauseFormat1?
//    ;
//
//libraryDescriptionEntryFormat2
//    : LB libraryName IMPORT libraryIsGlobalClause? libraryIsCommonClause? (
//        libraryAttributeClauseFormat2
//        | libraryEntryProcedureClauseFormat2
//    )*
//    ;
//
//libraryAttributeClauseFormat1
//    : ATTRIBUTE (SHARING IS? (DONTCARE | PRIVATE | SHAREDBYRUNUNIT | SHAREDBYALL))?
//    ;
//
//libraryAttributeClauseFormat2
//    : ATTRIBUTE libraryAttributeFunction? (LIBACCESS IS? (BYFUNCTION | BYTITLE))? libraryAttributeParameter? libraryAttributeTitle?
//    ;
//
//libraryAttributeFunction
//    : FUNCTIONNAME IS literal
//    ;
//
//libraryAttributeParameter
//    : LIBPARAMETER IS? literal
//    ;
//
//libraryAttributeTitle
//    : TITLE IS? literal
//    ;
//
//libraryEntryProcedureClauseFormat1
//    : ENTRY_PROCEDURE programName libraryEntryProcedureForClause?
//    ;
//
//libraryEntryProcedureClauseFormat2
//    : ENTRY_PROCEDURE programName libraryEntryProcedureForClause? libraryEntryProcedureWithClause? libraryEntryProcedureUsingClause?
//        libraryEntryProcedureGivingClause?
//    ;
//
//libraryEntryProcedureForClause
//    : FOR literal
//    ;
//
//libraryEntryProcedureGivingClause
//    : GIVING dataName
//    ;
//
//libraryEntryProcedureUsingClause
//    : USING libraryEntryProcedureUsingName+
//    ;
//
//libraryEntryProcedureUsingName
//    : dataName
//    | fileName
//    ;
//
//libraryEntryProcedureWithClause
//    : WITH libraryEntryProcedureWithName+
//    ;
//
//libraryEntryProcedureWithName
//    : localName
//    | fileName
//    ;
//
//libraryIsCommonClause
//    : IS? COMMON
//    ;
//
//libraryIsGlobalClause
//    : IS? GLOBAL
//    ;
//
// data description entry ----------------------------------

dataDescriptionEntry: dataName dummy1? dummy2? '.';
//dataDescriptionEntry
//    : dataDescriptionEntryFormat1
//    | dataDescriptionEntryFormat2
//    | dataDescriptionEntryFormat3
//    | dataDescriptionEntryExecSql
//    ;
//
//dataDescriptionEntryFormat1
//    : (INTEGERLITERAL | LEVEL_NUMBER_77) (FILLER | dataName)? (
//        dataRedefinesClause
//        | dataIntegerStringClause
//        | dataExternalClause
//        | dataGlobalClause
//        | dataTypeDefClause
//        | dataThreadLocalClause
//        | dataPictureClause
//        | dataCommonOwnLocalClause
//        | dataTypeClause
//        | dataUsingClause
//        | dataUsageClause
//        | dataValueClause
//        | dataReceivedByClause
//        | dataOccursClause
//        | dataSignClause
//        | dataSynchronizedClause
//        | dataJustifiedClause
//        | dataBlankWhenZeroClause
//        | dataWithLowerBoundsClause
//        | dataAlignedClause
//        | dataRecordAreaClause
//    )* '.'
//    ;
//
//dataDescriptionEntryFormat2
//    : LEVEL_NUMBER_66 dataName dataRenamesClause '.'
//    ;
//
//dataDescriptionEntryFormat3
//    : LEVEL_NUMBER_88 conditionName dataValueClause '.'
//    ;
//
//dataDescriptionEntryExecSql
//    : EXECSQLLINE+ '.'?
//    ;
//
//dataAlignedClause
//    : ALIGNED
//    ;
//
//dataBlankWhenZeroClause
//    : BLANK WHEN? (ZERO | ZEROS | ZEROES)
//    ;
//
//dataCommonOwnLocalClause
//    : COMMON
//    | OWN
//    | LOCAL
//    ;
//
//dataExternalClause
//    : IS? EXTERNAL (BY literal)?
//    ;
//
//dataGlobalClause
//    : IS? GLOBAL
//    ;
//
//dataIntegerStringClause
//    : INTEGER
//    | STRING
//    ;
//
//dataJustifiedClause
//    : (JUSTIFIED | JUST) RIGHT?
//    ;
//
//dataOccursClause
//    : OCCURS integerLiteral dataOccursTo? TIMES? (DEPENDING ON? qualifiedDataName)? dataOccursSort* (
//        INDEXED BY? LOCAL? indexName+
//    )?
//    ;
//
//dataOccursTo
//    : TO integerLiteral
//    ;
//
//dataOccursSort
//    : (ASCENDING | DESCENDING) KEY? IS? qualifiedDataName+
//    ;
//
dataPictureClause : (PICTURE | PIC) IS? pictureString ;

//pictureString
//    : (pictureChars+ pictureCardinality?)+
//    ;
//
//pictureChars
//    : DOLLARCHAR
//    | IDENTIFIER
//    | NUMERICLITERAL
//    | SLASHCHAR
//    | COMMACHAR
//    | DOT
//    | COLONCHAR
//    | ASTERISKCHAR
//    | DOUBLEASTERISKCHAR
//    | LPARENCHAR
//    | RPARENCHAR
//    | PLUSCHAR
//    | MINUSCHAR
//    | LESSTHANCHAR
//    | MORETHANCHAR
//    | integerLiteral
//    ;
//
//pictureCardinality
//    : LPARENCHAR integerLiteral RPARENCHAR
//    ;
//
//dataReceivedByClause
//    : RECEIVED? BY? (CONTENT | REFERENCE | REF)
//    ;
//
//dataRecordAreaClause
//    : RECORD AREA
//    ;
//
//dataRedefinesClause
//    : REDEFINES dataName
//    ;
//
//dataRenamesClause
//    : RENAMES qualifiedDataName ((THROUGH | THRU) qualifiedDataName)?
//    ;
//
//dataSignClause
//    : (SIGN IS?)? (LEADING | TRAILING) (SEPARATE CHARACTER?)?
//    ;
//
//dataSynchronizedClause
//    : (SYNCHRONIZED | SYNC) (LEFT | RIGHT)?
//    ;
//
//dataThreadLocalClause
//    : IS? THREAD_LOCAL
//    ;
//
//dataTypeClause
//    : TYPE IS? (SHORT_DATE | LONG_DATE | NUMERIC_DATE | NUMERIC_TIME | LONG_TIME)
//    ;
//
//dataTypeDefClause
//    : IS? TYPEDEF
//    ;
//
//dataUsageClause
//    : (USAGE IS?)? (
//        BINARY (TRUNCATED | EXTENDED)?
//        | BIT
//        | COMP
//        | COMP_1
//        | COMP_2
//        | COMP_3
//        | COMP_4
//        | COMP_5
//        | COMPUTATIONAL
//        | COMPUTATIONAL_1
//        | COMPUTATIONAL_2
//        | COMPUTATIONAL_3
//        | COMPUTATIONAL_4
//        | COMPUTATIONAL_5
//        | CONTROL_POINT
//        | DATE
//        | DISPLAY
//        | DISPLAY_1
//        | DOUBLE
//        | EVENT
//        | FUNCTION_POINTER
//        | INDEX
//        | KANJI
//        | LOCK
//        | NATIONAL
//        | PACKED_DECIMAL
//        | POINTER
//        | PROCEDURE_POINTER
//        | REAL
//        | TASK
//    )
//    ;
//
//dataUsingClause
//    : USING (LANGUAGE | CONVENTION) OF? (cobolWord | dataName)
//    ;
//
dataValueClause: (VALUE IS? | VALUES ARE?)? literal ;
//dataValueClause
//    : (VALUE IS? | VALUES ARE?)? dataValueInterval (COMMACHAR? dataValueInterval)*
//    ;
//
//dataValueInterval
//    : dataValueIntervalFrom dataValueIntervalTo?
//    ;
//
//dataValueIntervalFrom
//    : literal
//    | cobolWord
//    ;
//
//dataValueIntervalTo
//    : (THROUGH | THRU) literal
//    ;
//
//dataWithLowerBoundsClause
//    : WITH? LOWER BOUNDS
//    ;
//
// --- procedure division ----------------------------------------------------------------{{{1

procedureDivision : PROCEDURE DIVISION procedureUsingClause? procedureGivingClause? '.' procedureDeclaratives? statement* ;

procedureGivingClause : (GIVING | RETURNING) dataName ;

procedureUsingClause: USING procedureParameter+;
//procedureUsingClause : (USING | CHAINING) 
//    (     ((BY? REFERENCE)? ((OPTIONAL? (identifier | fileName)) | ANY )+)
//	| (BY? VALUE (identifier | literal | ANY)+)
//    )+
//    ;
//procedureUsingClause : (USING | CHAINING) procedureParameter+ ;
//procedureParameter : procedureByReferencePhrase | procedureByValuePhrase ;
//procedureByReferencePhrase : (BY? REFERENCE)? procedureByReference+ ;
//procedureByReference : (OPTIONAL? (identifier | fileName)) | ANY ;
//procedureByValuePhrase : BY? VALUE procedureByValue+ ;
//procedureByValue : identifier | literal | ANY ;

//procedureDeclaratives : DECLARATIVES '.' procedureDeclarative+ END DECLARATIVES '.' ;
//procedureDeclarative : procedureSectionHeader '.' useStatement '.' paragraphs ;
//procedureSectionHeader : sectionName SECTION integerLiteral?  ;


//procedureDivisionBody : paragraphs procedureSection* ;
//procedureSection : procedureSectionHeader '.' paragraphs ;
//paragraphs : sentence* paragraph* ;
//paragraph : paragraphName '.' (alteredGoTo | sentence*) ;
//sentence : statement* '.' ;
statement:
      ifStatement
//    | evaluateStatement
    | exitStatement
    | stopStatement
    | continueStatement
//    | goToStatement
//    | gobackStatement
//    | callStatement
//    | performStatement
//    | moveStatement
//    | setStatement
//    | computeStatement
//    | addStatement
//    | subtractStatement
//    | multiplyStatement
//    | divideStatement

//    | acceptStatement
//    | alterStatement
//    | cancelStatement
//    | closeStatement
//    | deleteStatement
//    | disableStatement
//    | displayStatement
//    | enableStatement
//    | entryStatement
//    | exhibitStatement
//    | execCicsStatement
//    | execSqlStatement
//    | execSqlImsStatement
//    | generateStatement
//    | initializeStatement
//    | initiateStatement
//    | inspectStatement
//    | mergeStatement
//    | openStatement
//    | purgeStatement
//    | readStatement
//    | receiveStatement
//    | releaseStatement
//    | returnStatement
//    | rewriteStatement
//    | searchStatement
//    | sendStatement
//    | sortStatement
//    | startStatement
//    | stringStatement
//    | terminateStatement
//    | unstringStatement
//    | writeStatement
    ;

ifStatement : IF condition THEN? thenStatement (ELSE elseStatement)? END_IF?  ;
thenStatement : NEXT SENTENCE | statement* ;
elseStatement : NEXT SENTENCE | statement* ;

exitStatement : EXIT PROGRAM?  ;
stopStatement : STOP (RUN | literal) ;
continueStatement : CONTINUE ;

//// accept statement
//
//acceptStatement
//    : ACCEPT identifier (
//        acceptFromDateStatement
//        | acceptFromEscapeKeyStatement
//        | acceptFromMnemonicStatement
//        | acceptMessageCountStatement
//    )? onExceptionClause? notOnExceptionClause? END_ACCEPT?
//    ;
//
//acceptFromDateStatement
//    : FROM (
//        DATE YYYYMMDD?
//        | DAY YYYYDDD?
//        | DAY_OF_WEEK
//        | TIME
//        | TIMER
//        | TODAYS_DATE MMDDYYYY?
//        | TODAYS_NAME
//        | YEAR
//        | YYYYMMDD
//        | YYYYDDD
//    )
//    ;
//
//acceptFromMnemonicStatement
//    : FROM mnemonicName
//    ;
//
//acceptFromEscapeKeyStatement
//    : FROM ESCAPE KEY
//    ;
//
//acceptMessageCountStatement
//    : MESSAGE? COUNT
//    ;
//
//// add statement
//
//addStatement
//    : ADD (addToStatement | addToGivingStatement | addCorrespondingStatement) onSizeErrorPhrase? notOnSizeErrorPhrase? END_ADD?
//    ;
//
//addToStatement
//    : addFrom+ TO addTo+
//    ;
//
//addToGivingStatement
//    : addFrom+ (TO addToGiving+)? GIVING addGiving+
//    ;
//
//addCorrespondingStatement
//    : (CORRESPONDING | CORR) identifier TO addTo
//    ;
//
//addFrom
//    : identifier
//    | literal
//    ;
//
//addTo
//    : identifier ROUNDED?
//    ;
//
//addToGiving
//    : identifier
//    | literal
//    ;
//
//addGiving
//    : identifier ROUNDED?
//    ;
//
//// altered go to statement
//
//alteredGoTo
//    : GO TO? '.'
//    ;
//
//// alter statement
//
//alterStatement
//    : ALTER alterProceedTo+
//    ;
//
//alterProceedTo
//    : procedureName TO (PROCEED TO)? procedureName
//    ;
//
//// call statement
//
//callStatement
//    : CALL (identifier | literal) callUsingPhrase? callGivingPhrase? onOverflowPhrase? onExceptionClause? notOnExceptionClause? END_CALL?
//    ;
//
//callUsingPhrase
//    : USING callUsingParameter+
//    ;
//
//callUsingParameter
//    : callByReferencePhrase
//    | callByValuePhrase
//    | callByContentPhrase
//    ;
//
//callByReferencePhrase
//    : (BY? REFERENCE)? callByReference+
//    ;
//
//callByReference
//    : ((ADDRESS OF | INTEGER | STRING)? identifier | literal | fileName)
//    | OMITTED
//    ;
//
//callByValuePhrase
//    : BY? VALUE callByValue+
//    ;
//
//callByValue
//    : (ADDRESS OF | LENGTH OF?)? (identifier | literal)
//    ;
//
//callByContentPhrase
//    : BY? CONTENT callByContent+
//    ;
//
//callByContent
//    : (ADDRESS OF | LENGTH OF?)? identifier
//    | literal
//    | OMITTED
//    ;
//
//callGivingPhrase
//    : (GIVING | RETURNING) identifier
//    ;
//
//// cancel statement
//
//cancelStatement
//    : CANCEL cancelCall+
//    ;
//
//cancelCall
//    : libraryName (BYTITLE | BYFUNCTION)
//    | identifier
//    | literal
//    ;
//
//// close statement
//
//closeStatement
//    : CLOSE closeFile+
//    ;
//
//closeFile
//    : fileName (closeReelUnitStatement | closeRelativeStatement | closePortFileIOStatement)?
//    ;
//
//closeReelUnitStatement
//    : (REEL | UNIT) (FOR? REMOVAL)? (WITH? (NO REWIND | LOCK))?
//    ;
//
//closeRelativeStatement
//    : WITH? (NO REWIND | LOCK)
//    ;
//
//closePortFileIOStatement
//    : (WITH? NO WAIT | WITH WAIT) (USING closePortFileIOUsing+)?
//    ;
//
//closePortFileIOUsing
//    : closePortFileIOUsingCloseDisposition
//    | closePortFileIOUsingAssociatedData
//    | closePortFileIOUsingAssociatedDataLength
//    ;
//
//closePortFileIOUsingCloseDisposition
//    : CLOSE_DISPOSITION OF? (ABORT | ORDERLY)
//    ;
//
//closePortFileIOUsingAssociatedData
//    : ASSOCIATED_DATA (identifier | integerLiteral)
//    ;
//
//closePortFileIOUsingAssociatedDataLength
//    : ASSOCIATED_DATA_LENGTH OF? (identifier | integerLiteral)
//    ;
//
//// compute statement
//
//computeStatement
//    : COMPUTE computeStore+ (EQUALCHAR | EQUAL) arithmeticExpression onSizeErrorPhrase? notOnSizeErrorPhrase? END_COMPUTE?
//    ;
//
//computeStore
//    : identifier ROUNDED?
//    ;
//
//
//// delete statement
//
//deleteStatement
//    : DELETE fileName RECORD? invalidKeyPhrase? notInvalidKeyPhrase? END_DELETE?
//    ;
//
//// disable statement
//
//disableStatement
//    : DISABLE (INPUT TERMINAL? | I_O TERMINAL | OUTPUT) cdName WITH? KEY (identifier | literal)
//    ;
//
//// display statement
//
//displayStatement
//    : DISPLAY displayOperand+ displayAt? displayUpon? displayWith?
//    ;
//
//displayOperand
//    : identifier
//    | literal
//    ;
//
//displayAt
//    : AT (identifier | literal)
//    ;
//
//displayUpon
//    : UPON (mnemonicName | environmentName)
//    ;
//
//displayWith
//    : WITH? NO ADVANCING
//    ;
//
//// divide statement
//
//divideStatement
//    : DIVIDE (identifier | literal) (
//        divideIntoStatement
//        | divideIntoGivingStatement
//        | divideByGivingStatement
//    ) divideRemainder? onSizeErrorPhrase? notOnSizeErrorPhrase? END_DIVIDE?
//    ;
//
//divideIntoStatement
//    : INTO divideInto+
//    ;
//
//divideIntoGivingStatement
//    : INTO (identifier | literal) divideGivingPhrase?
//    ;
//
//divideByGivingStatement
//    : BY (identifier | literal) divideGivingPhrase?
//    ;
//
//divideGivingPhrase
//    : GIVING divideGiving+
//    ;
//
//divideInto
//    : identifier ROUNDED?
//    ;
//
//divideGiving
//    : identifier ROUNDED?
//    ;
//
//divideRemainder
//    : REMAINDER identifier
//    ;
//
//// enable statement
//
//enableStatement
//    : ENABLE (INPUT TERMINAL? | I_O TERMINAL | OUTPUT) cdName WITH? KEY (literal | identifier)
//    ;
//
//// entry statement
//
//entryStatement
//    : ENTRY literal (USING identifier+)?
//    ;
//
//// evaluate statement
//
//evaluateStatement
//    : EVALUATE evaluateSelect evaluateAlsoSelect* evaluateWhenPhrase+ evaluateWhenOther? END_EVALUATE?
//    ;
//
//evaluateSelect
//    : identifier
//    | literal
//    | arithmeticExpression
//    | condition
//    ;
//
//evaluateAlsoSelect
//    : ALSO evaluateSelect
//    ;
//
//evaluateWhenPhrase
//    : evaluateWhen+ statement*
//    ;
//
//evaluateWhen
//    : WHEN evaluateCondition evaluateAlsoCondition*
//    ;
//
//evaluateCondition
//    : ANY
//    | NOT? evaluateValue evaluateThrough?
//    | condition
//    | booleanLiteral
//    ;
//
//evaluateThrough
//    : (THROUGH | THRU) evaluateValue
//    ;
//
//evaluateAlsoCondition
//    : ALSO evaluateCondition
//    ;
//
//evaluateWhenOther
//    : WHEN OTHER statement*
//    ;
//
//evaluateValue
//    : identifier
//    | literal
//    | arithmeticExpression
//    ;
//
//// exec cics statement
//
//execCicsStatement
//    : EXECCICSLINE+
//    ;
//
//// exec sql statement
//
//execSqlStatement
//    : EXECSQLLINE+
//    ;
//
//// exec sql ims statement
//
//execSqlImsStatement
//    : EXECSQLIMSLINE+
//    ;
//
//// exhibit statement
//
//exhibitStatement
//    : EXHIBIT NAMED? CHANGED? exhibitOperand+
//    ;
//
//exhibitOperand
//    : identifier
//    | literal
//    ;
//
// exit statement


//// generate statement
//
//generateStatement
//    : GENERATE reportName
//    ;
//
//// goback statement
//
//gobackStatement
//    : GOBACK
//    ;
//
//// goto statement
//
//goToStatement
//    : GO TO? (goToStatementSimple | goToDependingOnStatement)
//    ;
//
//goToStatementSimple
//    : procedureName
//    ;
//
//goToDependingOnStatement
//    : MORE_LABELS
//    | procedureName+ (DEPENDING ON? identifier)?
//    ;
//

//// initialize statement
//
//initializeStatement
//    : INITIALIZE identifier+ initializeReplacingPhrase?
//    ;
//
//initializeReplacingPhrase
//    : REPLACING initializeReplacingBy+
//    ;
//
//initializeReplacingBy
//    : (
//        ALPHABETIC
//        | ALPHANUMERIC
//        | ALPHANUMERIC_EDITED
//        | NATIONAL
//        | NATIONAL_EDITED
//        | NUMERIC
//        | NUMERIC_EDITED
//        | DBCS
//        | EGCS
//    ) DATA? BY (identifier | literal)
//    ;
//
//// initiate statement
//
//initiateStatement
//    : INITIATE reportName+
//    ;
//
//// inspect statement
//
//inspectStatement
//    : INSPECT identifier (
//        inspectTallyingPhrase
//        | inspectReplacingPhrase
//        | inspectTallyingReplacingPhrase
//        | inspectConvertingPhrase
//    )
//    ;
//
//inspectTallyingPhrase
//    : TALLYING inspectFor+
//    ;
//
//inspectReplacingPhrase
//    : REPLACING (inspectReplacingCharacters | inspectReplacingAllLeadings)+
//    ;
//
//inspectTallyingReplacingPhrase
//    : TALLYING inspectFor+ inspectReplacingPhrase+
//    ;
//
//inspectConvertingPhrase
//    : CONVERTING (identifier | literal) inspectTo inspectBeforeAfter*
//    ;
//
//inspectFor
//    : identifier FOR (inspectCharacters | inspectAllLeadings)+
//    ;
//
//inspectCharacters
//    : CHARACTERS inspectBeforeAfter*
//    ;
//
//inspectReplacingCharacters
//    : CHARACTERS inspectBy inspectBeforeAfter*
//    ;
//
//inspectAllLeadings
//    : (ALL | LEADING) inspectAllLeading+
//    ;
//
//inspectReplacingAllLeadings
//    : (ALL | LEADING | FIRST) inspectReplacingAllLeading+
//    ;
//
//inspectAllLeading
//    : (identifier | literal) inspectBeforeAfter*
//    ;
//
//inspectReplacingAllLeading
//    : (identifier | literal) inspectBy inspectBeforeAfter*
//    ;
//
//inspectBy
//    : BY (identifier | literal)
//    ;
//
//inspectTo
//    : TO (identifier | literal)
//    ;
//
//inspectBeforeAfter
//    : (BEFORE | AFTER) INITIAL? (identifier | literal)
//    ;
//
//// merge statement
//
//mergeStatement
//    : MERGE fileName mergeOnKeyClause+ mergeCollatingSequencePhrase? mergeUsing* mergeOutputProcedurePhrase? mergeGivingPhrase*
//    ;
//
//mergeOnKeyClause
//    : ON? (ASCENDING | DESCENDING) KEY? qualifiedDataName+
//    ;
//
//mergeCollatingSequencePhrase
//    : COLLATING? SEQUENCE IS? alphabetName+ mergeCollatingAlphanumeric? mergeCollatingNational?
//    ;
//
//mergeCollatingAlphanumeric
//    : FOR? ALPHANUMERIC IS alphabetName
//    ;
//
//mergeCollatingNational
//    : FOR? NATIONAL IS? alphabetName
//    ;
//
//mergeUsing
//    : USING fileName+
//    ;
//
//mergeOutputProcedurePhrase
//    : OUTPUT PROCEDURE IS? procedureName mergeOutputThrough?
//    ;
//
//mergeOutputThrough
//    : (THROUGH | THRU) procedureName
//    ;
//
//mergeGivingPhrase
//    : GIVING mergeGiving+
//    ;
//
//mergeGiving
//    : fileName (LOCK | SAVE | NO REWIND | CRUNCH | RELEASE | WITH REMOVE CRUNCH)?
//    ;
//
//// move statement
//
//moveStatement
//    : MOVE ALL? (moveToStatement | moveCorrespondingToStatement)
//    ;
//
//moveToStatement
//    : moveToSendingArea TO identifier+
//    ;
//
//moveToSendingArea
//    : identifier
//    | literal
//    ;
//
//moveCorrespondingToStatement
//    : (CORRESPONDING | CORR) moveCorrespondingToSendingArea TO identifier+
//    ;
//
//moveCorrespondingToSendingArea
//    : identifier
//    ;
//
//// multiply statement
//
//multiplyStatement
//    : MULTIPLY (identifier | literal) BY (multiplyRegular | multiplyGiving) onSizeErrorPhrase? notOnSizeErrorPhrase? END_MULTIPLY?
//    ;
//
//multiplyRegular
//    : multiplyRegularOperand+
//    ;
//
//multiplyRegularOperand
//    : identifier ROUNDED?
//    ;
//
//multiplyGiving
//    : multiplyGivingOperand GIVING multiplyGivingResult+
//    ;
//
//multiplyGivingOperand
//    : identifier
//    | literal
//    ;
//
//multiplyGivingResult
//    : identifier ROUNDED?
//    ;
//
//// open statement
//
//openStatement
//    : OPEN (openInputStatement | openOutputStatement | openIOStatement | openExtendStatement)+
//    ;
//
//openInputStatement
//    : INPUT openInput+
//    ;
//
//openInput
//    : fileName (REVERSED | WITH? NO REWIND)?
//    ;
//
//openOutputStatement
//    : OUTPUT openOutput+
//    ;
//
//openOutput
//    : fileName (WITH? NO REWIND)?
//    ;
//
//openIOStatement
//    : I_O fileName+
//    ;
//
//openExtendStatement
//    : EXTEND fileName+
//    ;
//
//// perform statement
//
//performStatement
//    : PERFORM (performInlineStatement | performProcedureStatement)
//    ;
//
//performInlineStatement
//    : performType? statement* END_PERFORM
//    ;
//
//performProcedureStatement
//    : procedureName ((THROUGH | THRU) procedureName)? performType?
//    ;
//
//performType
//    : performTimes
//    | performUntil
//    | performVarying
//    ;
//
//performTimes
//    : (identifier | integerLiteral) TIMES
//    ;
//
//performUntil
//    : performTestClause? UNTIL condition
//    ;
//
//performVarying
//    : performTestClause performVaryingClause
//    | performVaryingClause performTestClause?
//    ;
//
//performVaryingClause
//    : VARYING performVaryingPhrase performAfter*
//    ;
//
//performVaryingPhrase
//    : (identifier | literal) performFrom performBy performUntil
//    ;
//
//performAfter
//    : AFTER performVaryingPhrase
//    ;
//
//performFrom
//    : FROM (identifier | literal | arithmeticExpression)
//    ;
//
//performBy
//    : BY (identifier | literal | arithmeticExpression)
//    ;
//
//performTestClause
//    : WITH? TEST (BEFORE | AFTER)
//    ;
//
//// purge statement
//
//purgeStatement
//    : PURGE cdName+
//    ;
//
//// read statement
//
//readStatement
//    : READ fileName NEXT? RECORD? readInto? readWith? readKey? invalidKeyPhrase? notInvalidKeyPhrase? atEndPhrase? notAtEndPhrase? END_READ?
//    ;
//
//readInto
//    : INTO identifier
//    ;
//
//readWith
//    : WITH? ((KEPT | NO) LOCK | WAIT)
//    ;
//
//readKey
//    : KEY IS? qualifiedDataName
//    ;
//
//// receive statement
//
//receiveStatement
//    : RECEIVE (receiveFromStatement | receiveIntoStatement) onExceptionClause? notOnExceptionClause? END_RECEIVE?
//    ;
//
//receiveFromStatement
//    : dataName FROM receiveFrom (
//        receiveBefore
//        | receiveWith
//        | receiveThread
//        | receiveSize
//        | receiveStatus
//    )*
//    ;
//
//receiveFrom
//    : THREAD dataName
//    | LAST THREAD
//    | ANY THREAD
//    ;
//
//receiveIntoStatement
//    : cdName (MESSAGE | SEGMENT) INTO? identifier receiveNoData? receiveWithData?
//    ;
//
//receiveNoData
//    : NO DATA statement*
//    ;
//
//receiveWithData
//    : WITH DATA statement*
//    ;
//
//receiveBefore
//    : BEFORE TIME? (numericLiteral | identifier)
//    ;
//
//receiveWith
//    : WITH? NO WAIT
//    ;
//
//receiveThread
//    : THREAD IN? dataName
//    ;
//
//receiveSize
//    : SIZE IN? (numericLiteral | identifier)
//    ;
//
//receiveStatus
//    : STATUS IN? (identifier)
//    ;
//
//// release statement
//
//releaseStatement
//    : RELEASE recordName (FROM qualifiedDataName)?
//    ;
//
//// return statement
//
//returnStatement
//    : RETURN fileName RECORD? returnInto? atEndPhrase notAtEndPhrase? END_RETURN?
//    ;
//
//returnInto
//    : INTO qualifiedDataName
//    ;
//
//// rewrite statement
//
//rewriteStatement
//    : REWRITE recordName rewriteFrom? invalidKeyPhrase? notInvalidKeyPhrase? END_REWRITE?
//    ;
//
//rewriteFrom
//    : FROM identifier
//    ;
//
//// search statement
//
//searchStatement
//    : SEARCH ALL? qualifiedDataName searchVarying? atEndPhrase? searchWhen+ END_SEARCH?
//    ;
//
//searchVarying
//    : VARYING qualifiedDataName
//    ;
//
//searchWhen
//    : WHEN condition (NEXT SENTENCE | statement*)
//    ;
//
//// send statement
//
//sendStatement
//    : SEND (sendStatementSync | sendStatementAsync) onExceptionClause? notOnExceptionClause?
//    ;
//
//sendStatementSync
//    : (identifier | literal) sendFromPhrase? sendWithPhrase? sendReplacingPhrase? sendAdvancingPhrase?
//    ;
//
//sendStatementAsync
//    : TO (TOP | BOTTOM) identifier
//    ;
//
//sendFromPhrase
//    : FROM identifier
//    ;
//
//sendWithPhrase
//    : WITH (EGI | EMI | ESI | identifier)
//    ;
//
//sendReplacingPhrase
//    : REPLACING LINE?
//    ;
//
//sendAdvancingPhrase
//    : (BEFORE | AFTER) ADVANCING? (sendAdvancingPage | sendAdvancingLines | sendAdvancingMnemonic)
//    ;
//
//sendAdvancingPage
//    : PAGE
//    ;
//
//sendAdvancingLines
//    : (identifier | literal) (LINE | LINES)?
//    ;
//
//sendAdvancingMnemonic
//    : mnemonicName
//    ;
//
//// set statement
//
//setStatement
//    : SET (setToStatement+ | setUpDownByStatement)
//    ;
//
//setToStatement
//    : setTo+ TO setToValue+
//    ;
//
//setUpDownByStatement
//    : setTo+ (UP BY | DOWN BY) setByValue
//    ;
//
//setTo
//    : identifier
//    ;
//
//setToValue
//    : ON
//    | OFF
//    | ENTRY (identifier | literal)
//    | identifier
//    | literal
//    ;
//
//setByValue
//    : identifier
//    | literal
//    ;
//
//// sort statement
//
//sortStatement
//    : SORT fileName sortOnKeyClause+ sortDuplicatesPhrase? sortCollatingSequencePhrase? sortInputProcedurePhrase? sortUsing* sortOutputProcedurePhrase
//        ? sortGivingPhrase*
//    ;
//
//sortOnKeyClause
//    : ON? (ASCENDING | DESCENDING) KEY? qualifiedDataName+
//    ;
//
//sortDuplicatesPhrase
//    : WITH? DUPLICATES IN? ORDER?
//    ;
//
//sortCollatingSequencePhrase
//    : COLLATING? SEQUENCE IS? alphabetName+ sortCollatingAlphanumeric? sortCollatingNational?
//    ;
//
//sortCollatingAlphanumeric
//    : FOR? ALPHANUMERIC IS alphabetName
//    ;
//
//sortCollatingNational
//    : FOR? NATIONAL IS? alphabetName
//    ;
//
//sortInputProcedurePhrase
//    : INPUT PROCEDURE IS? procedureName sortInputThrough?
//    ;
//
//sortInputThrough
//    : (THROUGH | THRU) procedureName
//    ;
//
//sortUsing
//    : USING fileName+
//    ;
//
//sortOutputProcedurePhrase
//    : OUTPUT PROCEDURE IS? procedureName sortOutputThrough?
//    ;
//
//sortOutputThrough
//    : (THROUGH | THRU) procedureName
//    ;
//
//sortGivingPhrase
//    : GIVING sortGiving+
//    ;
//
//sortGiving
//    : fileName (LOCK | SAVE | NO REWIND | CRUNCH | RELEASE | WITH REMOVE CRUNCH)?
//    ;
//
//// start statement
//
//startStatement
//    : START fileName startKey? invalidKeyPhrase? notInvalidKeyPhrase? END_START?
//    ;
//
//startKey
//    : KEY IS? (
//        EQUAL TO?
//        | EQUALCHAR
//        | GREATER THAN?
//        | MORETHANCHAR
//        | NOT LESS THAN?
//        | NOT LESSTHANCHAR
//        | GREATER THAN? OR EQUAL TO?
//        | MORETHANOREQUAL
//    ) qualifiedDataName
//    ;
//
//// string statement
//
//stringStatement
//    : STRING stringSendingPhrase+ stringIntoPhrase stringWithPointerPhrase? onOverflowPhrase? notOnOverflowPhrase? END_STRING?
//    ;
//
//stringSendingPhrase
//    : stringSending+ (stringDelimitedByPhrase | stringForPhrase)
//    ;
//
//stringSending
//    : identifier
//    | literal
//    ;
//
//stringDelimitedByPhrase
//    : DELIMITED BY? (SIZE | identifier | literal)
//    ;
//
//stringForPhrase
//    : FOR (identifier | literal)
//    ;
//
//stringIntoPhrase
//    : INTO identifier
//    ;
//
//stringWithPointerPhrase
//    : WITH? POINTER qualifiedDataName
//    ;
//
//// subtract statement
//
//subtractStatement
//    : SUBTRACT (
//        subtractFromStatement
//        | subtractFromGivingStatement
//        | subtractCorrespondingStatement
//    ) onSizeErrorPhrase? notOnSizeErrorPhrase? END_SUBTRACT?
//    ;
//
//subtractFromStatement
//    : subtractSubtrahend+ FROM subtractMinuend+
//    ;
//
//subtractFromGivingStatement
//    : subtractSubtrahend+ FROM subtractMinuendGiving GIVING subtractGiving+
//    ;
//
//subtractCorrespondingStatement
//    : (CORRESPONDING | CORR) qualifiedDataName FROM subtractMinuendCorresponding
//    ;
//
//subtractSubtrahend
//    : identifier
//    | literal
//    ;
//
//subtractMinuend
//    : identifier ROUNDED?
//    ;
//
//subtractMinuendGiving
//    : identifier
//    | literal
//    ;
//
//subtractGiving
//    : identifier ROUNDED?
//    ;
//
//subtractMinuendCorresponding
//    : qualifiedDataName ROUNDED?
//    ;
//
//// terminate statement
//
//terminateStatement
//    : TERMINATE reportName
//    ;
//
//// unstring statement
//
//unstringStatement
//    : UNSTRING unstringSendingPhrase unstringIntoPhrase unstringWithPointerPhrase? unstringTallyingPhrase? onOverflowPhrase? notOnOverflowPhrase?
//        END_UNSTRING?
//    ;
//
//unstringSendingPhrase
//    : identifier (unstringDelimitedByPhrase unstringOrAllPhrase*)?
//    ;
//
//unstringDelimitedByPhrase
//    : DELIMITED BY? ALL? (identifier | literal)
//    ;
//
//unstringOrAllPhrase
//    : OR ALL? (identifier | literal)
//    ;
//
//unstringIntoPhrase
//    : INTO unstringInto+
//    ;
//
//unstringInto
//    : identifier unstringDelimiterIn? unstringCountIn?
//    ;
//
//unstringDelimiterIn
//    : DELIMITER IN? identifier
//    ;
//
//unstringCountIn
//    : COUNT IN? identifier
//    ;
//
//unstringWithPointerPhrase
//    : WITH? POINTER qualifiedDataName
//    ;
//
//unstringTallyingPhrase
//    : TALLYING IN? qualifiedDataName
//    ;
//
//// use statement
//
//useStatement
//    : USE (useAfterClause | useDebugClause)
//    ;
//
//useAfterClause
//    : GLOBAL? AFTER STANDARD? (EXCEPTION | ERROR) PROCEDURE ON? useAfterOn
//    ;
//
//useAfterOn
//    : INPUT
//    | OUTPUT
//    | I_O
//    | EXTEND
//    | fileName+
//    ;
//
//useDebugClause
//    : FOR? DEBUGGING ON? useDebugOn+
//    ;
//
//useDebugOn
//    : ALL PROCEDURES
//    | ALL REFERENCES? OF? identifier
//    | procedureName
//    | fileName
//    ;
//
//// write statement
//
//writeStatement
//    : WRITE recordName writeFromPhrase? writeAdvancingPhrase? writeAtEndOfPagePhrase? writeNotAtEndOfPagePhrase? invalidKeyPhrase? notInvalidKeyPhrase
//        ? END_WRITE?
//    ;
//
//writeFromPhrase
//    : FROM (identifier | literal)
//    ;
//
//writeAdvancingPhrase
//    : (BEFORE | AFTER) ADVANCING? (
//        writeAdvancingPage
//        | writeAdvancingLines
//        | writeAdvancingMnemonic
//    )
//    ;
//
//writeAdvancingPage
//    : PAGE
//    ;
//
//writeAdvancingLines
//    : (identifier | literal) (LINE | LINES)?
//    ;
//
//writeAdvancingMnemonic
//    : mnemonicName
//    ;
//
//writeAtEndOfPagePhrase
//    : AT? (END_OF_PAGE | EOP) statement*
//    ;
//
//writeNotAtEndOfPagePhrase
//    : NOT AT? (END_OF_PAGE | EOP) statement*
//    ;
//
//// statement phrases ----------------------------------
//
//atEndPhrase
//    : AT? END statement*
//    ;
//
//notAtEndPhrase
//    : NOT AT? END statement*
//    ;
//
//invalidKeyPhrase
//    : INVALID KEY? statement*
//    ;
//
//notInvalidKeyPhrase
//    : NOT INVALID KEY? statement*
//    ;
//
//onOverflowPhrase
//    : ON? OVERFLOW statement*
//    ;
//
//notOnOverflowPhrase
//    : NOT ON? OVERFLOW statement*
//    ;
//
//onSizeErrorPhrase
//    : ON? SIZE ERROR statement*
//    ;
//
//notOnSizeErrorPhrase
//    : NOT ON? SIZE ERROR statement*
//    ;
//
//// statement clauses ----------------------------------
//
//onExceptionClause
//    : ON? EXCEPTION statement*
//    ;
//
//notOnExceptionClause
//    : NOT ON? EXCEPTION statement*
//    ;
//
//// arithmetic expression ----------------------------------
//
//arithmeticExpression
//    : multDivs plusMinus*
//    ;
//
//plusMinus
//    : (PLUSCHAR | MINUSCHAR) multDivs
//    ;
//
//multDivs
//    : powers multDiv*
//    ;
//
//multDiv
//    : (ASTERISKCHAR | SLASHCHAR) powers
//    ;
//
//powers
//    : (PLUSCHAR | MINUSCHAR)? basis power*
//    ;
//
//power
//    : DOUBLEASTERISKCHAR basis
//    ;
//
//basis
//    : LPARENCHAR arithmeticExpression RPARENCHAR
//    | identifier
//    | literal
//    ;
//
//// condition ----------------------------------
//
//condition
//    : combinableCondition andOrCondition*
//    ;
//
//andOrCondition
//    : (AND | OR) (combinableCondition | abbreviation+)
//    ;
//
//combinableCondition
//    : NOT? simpleCondition
//    ;
//
//simpleCondition
//    : LPARENCHAR condition RPARENCHAR
//    | relationCondition
//    | classCondition
//    | conditionNameReference
//    ;
//
//classCondition
//    : identifier IS? NOT? (
//        NUMERIC
//        | ALPHABETIC
//        | ALPHABETIC_LOWER
//        | ALPHABETIC_UPPER
//        | DBCS
//        | KANJI
//        | className
//    )
//    ;
//
//conditionNameReference
//    : conditionName (inData* inFile? conditionNameSubscriptReference* | inMnemonic*)
//    ;
//
//conditionNameSubscriptReference
//    : LPARENCHAR subscript_ (COMMACHAR? subscript_)* RPARENCHAR
//    ;
//
//// relation ----------------------------------
//
//relationCondition
//    : relationSignCondition
//    | relationArithmeticComparison
//    | relationCombinedComparison
//    ;
//
//relationSignCondition
//    : arithmeticExpression IS? NOT? (POSITIVE | NEGATIVE | ZERO)
//    ;
//
//relationArithmeticComparison
//    : arithmeticExpression relationalOperator arithmeticExpression
//    ;
//
//relationCombinedComparison
//    : arithmeticExpression relationalOperator LPARENCHAR relationCombinedCondition RPARENCHAR
//    ;
//
//relationCombinedCondition
//    : arithmeticExpression ((AND | OR) arithmeticExpression)+
//    ;
//
//relationalOperator
//    : (IS | ARE)? (
//        NOT? (GREATER THAN? | MORETHANCHAR | LESS THAN? | LESSTHANCHAR | EQUAL TO? | EQUALCHAR)
//        | NOTEQUALCHAR
//        | GREATER THAN? OR EQUAL TO?
//        | MORETHANOREQUAL
//        | LESS THAN? OR EQUAL TO?
//        | LESSTHANOREQUAL
//    )
//    ;
//
//abbreviation
//    : NOT? relationalOperator? (
//        arithmeticExpression
//        | LPARENCHAR arithmeticExpression abbreviation RPARENCHAR
//    )
//    ;
//
//// identifier ----------------------------------
//
//identifier
//    : qualifiedDataName
//    | tableCall
//    | functionCall
//    | specialRegister
//    ;
//
//tableCall
//    : qualifiedDataName (LPARENCHAR subscript_ (COMMACHAR? subscript_)* RPARENCHAR)* referenceModifier?
//    ;
//
//functionCall
//    : FUNCTION functionName (LPARENCHAR argument (COMMACHAR? argument)* RPARENCHAR)* referenceModifier?
//    ;
//
//referenceModifier
//    : LPARENCHAR characterPosition COLONCHAR length? RPARENCHAR
//    ;
//
//characterPosition
//    : arithmeticExpression
//    ;
//
//length
//    : arithmeticExpression
//    ;
//
//subscript_
//    : ALL
//    | integerLiteral
//    | qualifiedDataName integerLiteral?
//    | indexName integerLiteral?
//    | arithmeticExpression
//    ;
//
//argument
//    : literal
//    | identifier
//    | qualifiedDataName integerLiteral?
//    | indexName integerLiteral?
//    | arithmeticExpression
//    ;
//
//// qualified data name ----------------------------------
//
//qualifiedDataName
//    : qualifiedDataNameFormat1
//    | qualifiedDataNameFormat2
//    | qualifiedDataNameFormat3
//    | qualifiedDataNameFormat4
//    ;
//
//qualifiedDataNameFormat1
//    : (dataName | conditionName) (qualifiedInData+ inFile? | inFile)?
//    ;
//
//qualifiedDataNameFormat2
//    : paragraphName inSection
//    ;
//
//qualifiedDataNameFormat3
//    : textName inLibrary
//    ;
//
//qualifiedDataNameFormat4
//    : LINAGE_COUNTER inFile
//    ;
//
//qualifiedInData
//    : inData
//    | inTable
//    ;
//
//// in ----------------------------------
//
//inData
//    : (IN | OF) dataName
//    ;
//
//inFile
//    : (IN | OF) fileName
//    ;
//
//inMnemonic
//    : (IN | OF) mnemonicName
//    ;
//
//inSection
//    : (IN | OF) sectionName
//    ;
//
//inLibrary
//    : (IN | OF) libraryName
//    ;
//
//inTable
//    : (IN | OF) tableCall
//    ;
//
//// names ----------------------------------
//
//alphabetName
//    : cobolWord
//    ;
//
//assignmentName
//    : systemName
//    ;
//
//basisName
//    : programName
//    ;
//
//cdName
//    : cobolWord
//    ;
//
//className
//    : cobolWord
//    ;
//
//computerName
//    : systemName
//    ;
//
//conditionName
//    : cobolWord
//    ;
//
//dataName
//    : cobolWord
//    ;
//
//dataDescName
//    : FILLER
//    | CURSOR
//    | dataName
//    ;
//
//environmentName
//    : systemName
//    ;
//
//fileName
//    : cobolWord
//    ;
//
//functionName
//    : INTEGER
//    | LENGTH
//    | RANDOM
//    | SUM
//    | WHEN_COMPILED
//    | cobolWord
//    ;
//
//indexName
//    : cobolWord
//    ;
//
//languageName
//    : systemName
//    ;
//
//libraryName
//    : cobolWord
//    ;
//
//localName
//    : cobolWord
//    ;
//
//mnemonicName
//    : cobolWord
//    ;
//
//paragraphName
//    : cobolWord
//    | integerLiteral
//    ;
//
//procedureName
//    : paragraphName inSection?
//    | sectionName
//    ;
//
//programName
//    : NONNUMERICLITERAL
//    | cobolWord
//    ;
//
//recordName
//    : qualifiedDataName
//    ;
//
//reportName
//    : qualifiedDataName
//    ;
//
//routineName
//    : cobolWord
//    ;
//
//screenName
//    : cobolWord
//    ;
//
//sectionName
//    : cobolWord
//    | integerLiteral
//    ;
//
//systemName
//    : cobolWord
//    ;
//
//symbolicCharacter
//    : cobolWord
//    ;
//
//textName
//    : cobolWord
//    ;
//
// literal and identifier ----------------------------------{{{1
//
//cobolWord
//    : IDENTIFIER
//    | COBOL
//    | PROGRAM
//    | ABORT
//    | AS
//    | ASCII
//    | ASSOCIATED_DATA
//    | ASSOCIATED_DATA_LENGTH
//    | ATTRIBUTE
//    | AUTO
//    | AUTO_SKIP
//    | BACKGROUND_COLOR
//    | BACKGROUND_COLOUR
//    | BEEP
//    | BELL
//    | BINARY
//    | BIT
//    | BLINK
//    | BOUNDS
//    | CAPABLE
//    | CCSVERSION
//    | CHANGED
//    | CHANNEL
//    | CLOSE_DISPOSITION
//    | COMMITMENT
//    | CONTROL_POINT
//    | CONVENTION
//    | CRUNCH
//    | CURSOR
//    | DEFAULT
//    | DEFAULT_DISPLAY
//    | DEFINITION
//    | DFHRESP
//    | DFHVALUE
//    | DISK
//    | DONTCARE
//    | DOUBLE
//    | EBCDIC
//    | EMPTY_CHECK
//    | ENTER
//    | ENTRY_PROCEDURE
//    | EOL
//    | EOS
//    | ERASE
//    | ESCAPE
//    | EVENT
//    | EXCLUSIVE
//    | EXPORT
//    | EXTENDED
//    | FOREGROUND_COLOR
//    | FOREGROUND_COLOUR
//    | FULL
//    | FUNCTIONNAME
//    | FUNCTION_POINTER
//    | GRID
//    | HIGHLIGHT
//    | IMPLICIT
//    | IMPORT
//    | INTEGER
//    | KEPT
//    | KEYBOARD
//    | LANGUAGE
//    | LB
//    | LD
//    | LEFTLINE
//    | LENGTH_CHECK
//    | LIBACCESS
//    | LIBPARAMETER
//    | LIBRARY
//    | LIST
//    | LOCAL
//    | LONG_DATE
//    | LONG_TIME
//    | LOWER
//    | LOWLIGHT
//    | MMDDYYYY
//    | NAMED
//    | NATIONAL
//    | NATIONAL_EDITED
//    | NETWORK
//    | NO_ECHO
//    | NUMERIC_DATE
//    | NUMERIC_TIME
//    | ODT
//    | ORDERLY
//    | OVERLINE
//    | OWN
//    | PASSWORD
//    | PORT
//    | PRINTER
//    | PRIVATE
//    | PROCESS
//    | PROMPT
//    | READER
//    | REAL
//    | RECEIVED
//    | RECURSIVE
//    | REF
//    | REMOTE
//    | REMOVE
//    | REQUIRED
//    | REVERSE_VIDEO
//    | SAVE
//    | SECURE
//    | SHARED
//    | SHAREDBYALL
//    | SHAREDBYRUNUNIT
//    | SHARING
//    | SHORT_DATE
//    | SYMBOL
//    | TASK
//    | THREAD
//    | THREAD_LOCAL
//    | TIMER
//    | TODAYS_DATE
//    | TODAYS_NAME
//    | TRUNCATED
//    | TYPEDEF
//    | UNDERLINE
//    | VIRTUAL
//    | WAIT
//    | YEAR
//    | YYYYMMDD
//    | YYYYDDD
//    | ZERO_FILL
//    ;
//
//literal
//    : NONNUMERICLITERAL
//    | figurativeConstant
//    | numericLiteral
//    | booleanLiteral
//    | cicsDfhRespLiteral
//    | cicsDfhValueLiteral
//    ;
//
//booleanLiteral
//    : TRUE
//    | FALSE
//    ;
//
//numericLiteral
//    : NUMERICLITERAL
//    | ZERO
//    | integerLiteral
//    ;
//
//integerLiteral
//    : INTEGERLITERAL
//    | LEVEL_NUMBER_66
//    | LEVEL_NUMBER_77
//    | LEVEL_NUMBER_88
//    ;
//
//cicsDfhRespLiteral
//    : DFHRESP LPARENCHAR (cobolWord | literal) RPARENCHAR
//    ;
//
//cicsDfhValueLiteral
//    : DFHVALUE LPARENCHAR (cobolWord | literal) RPARENCHAR
//    ;
//
// keywords that cannot be used a identifier ---------------------------------- {{{2
//
//figurativeConstant
//    : ALL literal
//    | HIGH_VALUE
//    | HIGH_VALUES
//    | LOW_VALUE
//    | LOW_VALUES
//    | NULL_
//    | NULLS
//    | QUOTE
//    | QUOTES
//    | SPACE
//    | SPACES
//    | ZERO
//    | ZEROS
//    | ZEROES
//    ;
//
//specialRegister
//    : ADDRESS OF identifier
//    | DATE
//    | DAY
//    | DAY_OF_WEEK
//    | DEBUG_CONTENTS
//    | DEBUG_ITEM
//    | DEBUG_LINE
//    | DEBUG_NAME
//    | DEBUG_SUB_1
//    | DEBUG_SUB_2
//    | DEBUG_SUB_3
//    | LENGTH OF? identifier
//    | LINAGE_COUNTER
//    | LINE_COUNTER
//    | PAGE_COUNTER
//    | RETURN_CODE
//    | SHIFT_IN
//    | SHIFT_OUT
//    | SORT_CONTROL
//    | SORT_CORE_SIZE
//    | SORT_FILE_SIZE
//    | SORT_MESSAGE
//    | SORT_MODE_SIZE
//    | SORT_RETURN
//    | TALLY
//    | TIME
//    | WHEN_COMPILED
//    ;
//
// lexer rules ==================================={{{1

// Identifier && keywords				{{{1
// IDENTIFIER : [a-zA-Z0-9]+ ([-_]+ [a-zA-Z0-9]+)* ;
// keywords
// symbols				{{{1
/*
AMPCHAR : '&' ;
ASTERISKCHAR : '*' ;
DOUBLEASTERISKCHAR : '**' ;
COLONCHAR : ':' ;
COMMACHAR : ',' ;
COMMENTTAG : '*>' ;
DOLLARCHAR : '$' ;
DOUBLEQUOTE : '"' ;
// period full stop
DOT_FS
    : '.' ('\r' | '\n' | '\f' | '\t' | ' ')+
    | '.' EOF
    ;

DOT : '.' ;
EQUALCHAR : '=' ;
EXECCICSTAG : '*>EXECCICS' ;
EXECSQLTAG : '*>EXECSQL' ;
EXECSQLIMSTAG : '*>EXECSQLIMS' ;
LESSTHANCHAR : '<' ;
LESSTHANOREQUAL : '<=' ;
LPARENCHAR : '(' ;
MINUSCHAR : '-' ;
MORETHANCHAR : '>' ;
MORETHANOREQUAL : '>=' ;
NOTEQUALCHAR : '<>' ;
PLUSCHAR : '+' ;
SINGLEQUOTE : '\'' ;
RPARENCHAR : ')' ;
SLASHCHAR : '/' ;
*/
// literals				{{{1
/*
NONNUMERICLITERAL
    : STRINGLITERAL
    | DBCSLITERAL
    | HEXNUMBER
    | NULLTERMINATED
    ;

fragment HEXNUMBER
    : X '"' [0-9A-F]+ '"'
    | X '\'' [0-9A-F]+ '\''
    ;

fragment NULLTERMINATED
    : Z '"' (~["\n\r] | '""' | '\'')* '"'
    | Z '\'' (~['\n\r] | '\'\'' | '"')* '\''
    ;

fragment STRINGLITERAL
    : '"' (~["\n\r] | '""' | '\'')* '"'
    | '\'' (~['\n\r] | '\'\'' | '"')* '\''
    ;

fragment DBCSLITERAL
    : [GN] '"' (~["\n\r] | '""' | '\'')* '"'
    | [GN] '\'' (~['\n\r] | '\'\'' | '"')* '\''
    ;

LEVEL_NUMBER_66 : '66' ;
LEVEL_NUMBER_77 : '77' ;
LEVEL_NUMBER_88 : '88' ;
INTEGERLITERAL : (PLUSCHAR | MINUSCHAR)? [0-9]+ ;
NUMERICLITERAL
    : (PLUSCHAR | MINUSCHAR)? [0-9]* (DOT | COMMACHAR) [0-9]+ (
        ('e' | 'E') (PLUSCHAR | MINUSCHAR)? [0-9]+
    )?
    ;
*/
// whitespace, line breaks, comments, ...				{{{1
/*
NEWLINE : '\r'? '\n' -> channel(HIDDEN) ;
EXECCICSLINE : EXECCICSTAG WS ~('\n' | '\r' | '}')* ('\n' | '\r' | '}') ;
EXECSQLIMSLINE : EXECSQLIMSTAG WS ~('\n' | '\r' | '}')* ('\n' | '\r' | '}') ;
EXECSQLLINE : EXECSQLTAG WS ~('\n' | '\r' | '}')* ('\n' | '\r' | '}') ;
COMMENTLINE : COMMENTTAG WS ~('\n' | '\r')* -> channel(HIDDEN) ;
WS : [ \t\f;]+ -> channel(HIDDEN) ;
SEPARATOR : ', ' -> channel(HIDDEN) ;
*/

// case insensitive chars				{{{1
/*
fragment A : ('a' | 'A') ;
fragment B : ('b' | 'B') ;
fragment C : ('c' | 'C') ;
fragment D : ('d' | 'D') ;
fragment E : ('e' | 'E') ;
fragment F : ('f' | 'F') ;
fragment G : ('g' | 'G') ;
fragment H : ('h' | 'H') ;
fragment I : ('i' | 'I') ;
fragment J : ('j' | 'J') ;
fragment K : ('k' | 'K') ;
fragment L : ('l' | 'L') ;
fragment M : ('m' | 'M') ;
fragment N : ('n' | 'N') ;
fragment O : ('o' | 'O') ;
fragment P : ('p' | 'P') ;
fragment Q : ('q' | 'Q') ;
fragment R : ('r' | 'R') ;
fragment S : ('s' | 'S') ;
fragment T : ('t' | 'T') ;
fragment U : ('u' | 'U') ;
fragment V : ('v' | 'V') ;
fragment W : ('w' | 'W') ;
fragment X : ('x' | 'X') ;
fragment Y : ('y' | 'Y') ;
fragment Z : ('z' | 'Z') ;
*/
