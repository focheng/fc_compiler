/** PLI Grammar for FC Compiler.					{{{
 *
 * @author FC
 *//*}}}*/
grammar Pli;

// Parser rules ===================================			{{{1

// ---- compilation unit ----                                          	{{{1
compilationUnit: procedureDeclaration
  | packageDeclaration
  | functionDeclaration
  | beginBlock;

// ---- statements ----							{{{2
statement: ifStatement
  | selectStatement
  | doStatement
  | endStatement
  | exitStatement
  | stopStatement
  | returnStatement
  | openStatement
  | closeStatement
  | flushStatement
  | readStatement
  | writeStatement
  | rewriteStatement
  | locateStatement
  | deleteStatement
  | getStatement
  | putStatement
  | displayStatement
  | emptyStatement
  | declareStatement
  | Statement
  ;

emptyStatement: ';' ;

// -- program control statements --				{{{2
// The EXIT statement stops the current thread
exitStatement: EXIT ;
// The STOP statement stops the current application
stopStatement: EXIT ;

ifStatement: IF expr THEN statement ELSE statement ;
endStatement: END [IDENTIFIER] ;

selectStatement: SELECT ['(' expr ')'] ';' ;
whenStatement: WHEN expr {',' expr} statement;
otherwiseStatement: OTHERWISE statement;

returnStatement: RETURN expr ;

doStatement: doType1Statement | doType2Statement | doType3Statement ;
doType1Statement: DO ;
doType2Statement: DO ( (WHILE expr [UNTIL expr]) | (UNTIL expr [WHILE expr]) );
doType3Statement: DO IDENTIFIER '=' expr 
	[ expr (TO expr [BY expr] | (BY expr [TO expr] ]
	[ (WHILE  expr  [UNTIL  expr ] | (UNTIL  expr  [WHILE  expr ] ]
  ;
doType4Statement: DO LOOP;

// ---- Program Orgnization ----					{{{1
packageDeclaration: IDENTIFIER ':' PACKAGE 
	[EXPORTS '(' IDENTIFIER (',' IDENTIFIER)* ')']
	statement*
	END IDENTIFIER
  ;

// ---- block ----							{{{2
beginBlock: IDENTIFIER ':' BEGIN 
	[OPTIONS '(' option (',' option)* ')']
	statement*
	END IDENTIFIER
  ;

// ---- procedure declaration ----					{{{2
// ---- package declaration ----					{{{2

// ---- Type definition ----						{{{1

// ---- Data Declaration ----						{{{1
/**
 * A data item is either the value of a variable or a constant. Data items can be single item, called scalar, or they can be a collection of items called data aggregates. The kinds of data aggregates are array, structures, and unions.
 * 
 * variable vs constant
 * - A variable has a value or values that might change during execution of a program.
 * - A constant has a value that cannot change. 
 *
 * Data types: 
 * - Computational data: Represents values that are used in computations to produce a desired result.
 *   - Arithmetic data: is either coded arithmetic data or numeric picture data
 *     - Coded arithmetic data items are rational numbers. 
 *     - Numeric picture data is numeric data that is held in character form
 *   - String data is a sequence of contiguous characters, bits, widechars or graphics that are treated as a single data item.
 * - Program-control data: Represents values that are used to control execution of your program. 
 *   - area, entry, label, file, format, pointer, and offset
 * - Array is an n-dimensional collection of elements that have identical attributes.
 * - Union is a collection of member elements that overlay each other, occupying the same storage. 
 * - Structure is a collection of member elements that can be elementary variables, structures, unions, and arrays.
 *
 * Data Attributes
 * - base:	DECIMAL,   BINARY
 * - scale:	FIXED,     FLOAT
 * - mode:	REAL,      COMPLEX
 * - signed:	SIGNED,    UNSIGNED
 * - alignment:	ALIGNED,   UNALIGNED
 * - scope:	INTERNAL,  EXTERNAL
 * - storage:	AUTOMATIC, STATIC, BASED, CONTROLLED
 * - precision (significant digits and decimal-point placement)
 * - DIMENSION
 * - SUPPRESS
 */
declareStatement: DECLARE variableDeclaration (',' variableDeclaration)* ';' ;
variableDeclaration: [LEVEL] NAME [dimensionExpr]
	( dataAttributes
	| alignmentAttributes
	| scopeAttributes
	| storageAttributes
	| complementaryAttributes
	)
  ;

/**
 * Character strings can also be declared using the PICTURE attribute.
 * length of a NONVARYING string or the maximum length of a VARYING or VARYINGZ string.
 * Graphic constant is a contiguous sequence of DBCS characters (2 bytes).
 * GX (hex) graphic constant for DBCS characters (4 bytes). 
 */
// 
stringDeclaration: (BIT | CHARACTER | GRAPHIC | WIDECHAR) 
	'(' [(NUMBER_LITERAL) | '*'] ')' 
	[NONVARYING | VARYING | VARYING]
  ;

// ---- Preprocessor statement ----					{{{1
// %include "absolute_file_name.inc"
includeStatement: STRING_LITERAL | IDENTIFIER '(' IDENTIFIER ')' ';' ;

// ---- Memory ----					{{{1
allocateStatement: ALLOCATE IDENTIFIER ';' ;
freeStatement:         FREE IDENTIFIER ';' ;

// ---- Multiple Threads ----					{{{1
attachStatement: ATTACH entryReference [THREAD taskReference] ';' ;
waitStatement:   WAIT   THREAD '(' taskReference ')' ';' ;
detachStatement: DETACH THREAD '(' taskReference ')' ';' ;

// -- open/close/flush file statements --				{{{1
openStatement: OPEN openFileStatement (',' openFileStatement)*;
openFileStatement: FILE '(' IDENTIFIER ')' openFileOption*;
openFileOption  : STREAM
  | RECORD
  | INPUT
  | OUTPUT
  | UPDATE
  | SEQUENTIAL
  | DIRECT
  | BUFFERED
  | UNBUFFERED
  | KEYED
  | PRINT
  | TITLE '(' expr ')'
  | LINESIZE '(' expr ')'
  | PAGESIZE '(' expr ')'
  ;

closeStatement: CLOSE closeFileStatement (',' closeFileStatement)*;
closeFileStatement: FILE '(' IDENTIFIER ')' [ENVIRONMENT '(' (LEAVE | REREAD) ')'];

// The FLUSH statement can be used to flush one or all files.
flushStatement: FLUSH FILE '(' (IDENTIFIER | '*') ')';

// -- Record-oriented data transmission statements --			{{{1

readStatement: READ FILE '(' IDENTIFIER ')' (
    INTO	'(' IDENTIFIER ')'
  | IGNORE	'(' expr ')'
  | SET		'(' IDENTIFIER ')'
  )?;

writeStatement: WRITE FILE '(' IDENTIFIER ')' FROM '(' IDENTIFIER ')';
rewriteStatement: REWRITE FILE '(' IDENTIFIER ')' FROM '(' IDENTIFIER ')';
locateStatement: ;
deleteStatement: DELETE FILE '(' IDENTIFIER ')';

// -- Stream-oriented data transmission statements --			{{{1
getStatement: GET FILE '(' IDENTIFIER ')';
putStatement: PUT FILE '(' IDENTIFIER ')';

// -- Condition / On statement --					{{{1
onStatement: ON condition [SNAP] [(SYSTEM ';') | onUnit] ; 

// ====== Expression ======					{{{1
/**
 * An expression is a representation of a value.
 * - An element expression represents a single value.
 * - An array expression represents an array of values.
 * - A structure expression represents a structured set of values.
 */
expr: assignmentExpr ;
assignmentExpr: [expr assignmentOperator] binaryExpr ;
binaryExpr:	unaryExpr binaryOperator unaryExpr ;
unaryExpr:	prefixOperator primaryExpr ;
primaryExpr: 	reference | STRING_LITERAL | NUMBER_LITERAL | parenExpr ;
parenExpr: 	'(' expr ')' ;
/**
 * A variable reference is one of the following:
 * - A declared variable name
 * - A reference derived from a declared name through one or more of the following:
 *   – Pointer qualification
 *   – Structure qualification
 *   – Subscripting
 */
reference:	[locatorQualifier] basicReference ['(' subscriptList ')'] ['(' argumentList ')'] ;
basicReference: arrayAccess {'.' arrayAccess} ;
arrayAccess: IDENTIFIER [subscriptList];
subscriptList:	(expr | '*') (',' (expr | '*'))* ;
argumentList:	(expr | '*') (',' (expr | '*'))* ;
locatorQualifier: reference ['->' '=>' '.'] ;

/*
 *+----------------------------------------------------------------------+
 * Priority    Operator	Operation Type	Remarks
 *+----------------------------------------------------------------------+
 * 1		** 	Arithmetic 	Result is in coded arithmetic form
 * 	prefix +, − 	Arithmetic 
 * 	prefix ¬ 	Bit string 	All non-BIT data converted to BIT
 * 2 	*, / 		Arithmetic 	Result is in coded arithmetic form
 * 3 	infix +, −	Arithmetic 	Result is in coded arithmetic form
 * 4 	|| 		Concatenation 
 * 5 	<, ¬<, <=, =, 	Comparison 	Result is always either '1'B or '0'B
 * 	¬=, >=, >, ¬> 
 * 6 	& 		Bit string 	All non-BIT data converted to BIT
 * 7 	│ 		Bit string 	All non-BIT data converted to BIT
 *   	infix ¬ 	Bit string 	All non-BIT data converted to BIT
 */
assignmentOperator: 
    '='
  | '+=' 
  | '-=' 
  | '*=' 
  | '/=' 
  | '**='
  | '&=' 
  | '|=' 
  | '||='
  ;

binaryOperator: 
    '+' 
  | '-' 
  | '*' 
  | '/' 
  | '**'
  | '=' 
  | '¬='
  | '^='
  | '<' 
  | '¬<'
  | '^<'
  | '>' 
  | '¬>'
  | '^>'
  | '<='
  | '>='
  | '¬'
  | '^'
  | '&'
  | '|'
  | '||'
  ;

prefixOperator:	'+' | '-';

// Lexer rules ===================================			{{{1

// ---- IDENTIFIER ----							{{{1
IDENTIFIER : [a-zA-Z_$#@!] [a-zA-Z0-9_$#@!]* ;

// ---- Constants/Literals ----						{{{1
// Constants for computational data are referred to by stating the value of the constant or naming the constant in a DECLARE statement.
// Constants for program-control data are referred to by name.

// String Literals     							{{{1
// - enclosed in either single or double quotation marks.
// two quotation marks indicate single quotation mark. 

/*+----------------------------------------------------------------------+
	Constant 			Comments/Length
  +----------------------------------------------------------------------+
    Character constant
	'Shakespeare''s "Hamlet"' 	22
	"Shakespeare's ""Hamlet""" 	22
	"Page 5" 			6
	'' 				0
	(2)'Walla ' 			12
    X (hex) character constant
	"0d0A"x				2
	''X				0
    Bit constant
	'1'B				1
	"1100_1010_11"B			10
	(64)'0'B			64
	''B				0
	'0'B				1
    B4/BX (hex) bit constant
	'CA'B4				same as "1100_1010"B
	'80'B4				same as '1000_0000'B
	'1'B4				same as '0001'B
	(2)'F'B4			same as '1111_1111'B
	(2)'F'B4			same as 'FF'BX
	''B4				same as ""B
    B3 (octal) bit constant
	'22'B3				same as "010_010"B
	'40'B3				same as '100_000'B
	'1'B3				same as '001'B
	(2)'7'B3			same as '111_111'B
	''B3				same as ""B
    GX (hex) graphic constant
	'81a1'gx 		represents one DBCS character
	""gX 				same as ''g
    Mixed character data: SBCS and DBCS characters
	'IBM<kk>'M 	7 bytes on z/OS, 5 bytes on other platforms
	'<.I.B.M>'M 	8 bytes on z/OS, 6 bytes on other platforms
	''M 		0
    WX (hex) widechar constant
	'0031'wx 	represents one UTF-16 character
	""wX 		is the same as ''w
  +----------------------------------------------------------------------+
 */

// Number Literals     							{{{1
/*+----------------------------------------------------------------------+
	Constant 	Precision/Comments
  +----------------------------------------------------------------------+
    Binary fixed-point constant
	1011_0B		(5,0)
	1111_1B		(5,0)
	101B		(3,0)
	1011.111B	(7,3)
    XN (hex) binary fixed-point constant -   SIGNED REAL FIXED BINARY constant
	’100’XN  	same as ’00000100’XN with value 256
	’8000’XN 	same as ’00008000’XN with value 32,768
	’FFFF’XN 	same as ’0000FFFF’XN with value 65,535
	"ffff_ffff"XN 	is the value -1
    XU (hex) binary fixed-point constant - UNSIGNED REAL FIXED BINARY constant
	’100’XU 	same as ’00000100’XU with value 256
	’8000’XU	same as ’00008000’XU with value 32,768
	’FFFF’XU	same as ’0000FFFF’XU with value 65,535
	"ffff_ffff"XU 	is the value 2**32-1
    Decimal fixed-point constant
	3.1416		(5,4)
	455.3		(4,1)
	732		(3,0)
	1_200_300	(7,0)
	003		(3,0)
	5280		(4,0)
	.0012		(4,4)
    Binary floating-point constant
	101101E5B	(6)
	101.101E2B	(6)
	11101E-28B	(5)
	11.01E+42B	(4)
    Decimal floating-point constant
	15E-23		(2)
	15E23		(2)
	4E-3		(1)
	1.96E+07	(3)
	438E0		(3)
	3_141_593E-6	(7)
	.003_141_593E3	(9)
	1s0		(6)
	1d0		(16)
	1q0		(18)(Windows)
	1q0		(32)(AIX)
	1q0		(33)(z/OS)
  +----------------------------------------------------------------------+
 */

// ---- Delimiters ----							{{{1

// ---- Operators ----							{{{1

// ---- Comments ----							{{{1

// ---- Blank/White Spaces ----						{{{1

/*

*/
