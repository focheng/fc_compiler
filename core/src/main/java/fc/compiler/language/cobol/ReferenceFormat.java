package fc.compiler.language.cobol;

/**
 * The format used when writing a COBOL source program is called the "reference format".
 * The reference format is a rule stating which element is being placed in which position on a line.
 * There are three types of reference formats:
 * - Fixed format.
 * - Variable format. Area B is 12 ~ 80.
 * - Free format. Code can be written anywhere in a line with the exception of the
 *      particular rules for comment, debugging line, and continuation of lines.
 *
 * [ seq]i[aa][  area b                                                    ][id # ]
 * 12345678901234567890123456789012345678901234567890123456789012345678901234567890
 *           1         2         3         4         5         6         7
 *
 * [ seq]i[aa][  area b                                                   ][ id # ]
 * 12345678901234567890123456789012345678901234567890123456789012345678901234567890
 *           1         2         3         4         5         6         7
 * 1~6     Sequence number area is used to label a source statement line.
 *         The label need not be in a specific sequence or be unique.
 * 7       Indicator area is used to specify line type.
 * 8~11    Area A.
 * 12~72   Area B.
 * 73~80   [Only fixed format] Program Identification Number Area.
 *
 * Indicator are:
 *      '*' or '/'   Comment lines
 *      '-'          Continuation lines
 *      - 'D'        Debugging lines
 * 61.
 * The following items must start in area A:
 *         - Division headers
 *         - Section headers
 *         - Paragraph headers or paragraph names
 *         - Level indicators (FD, SD or RD)
 *         - Level-numbers (only 01 and 77)
 *         - DECLARATIVES and END DECLARATIVES
 *         - End program, end class, and end method mark
 * The following items must start in area B:
 *         - Sentences
 *         - Statements (except the COPY and REPLACE statements, which can start in area A)
 *         - Clauses
 *         - Entries (except entries starting with a level indicator or data description entries whose level-number is 01 or 77, which
 *                 must originate in area A. Data description entries whose level-number is not 01 or 77 can also originate in area A.)
 *         - Continuation lines
 * Certain items can begin in either Area A or Area B:
 *         - Level-numbers
 *         - Comment lines is any line with an asterisk (*) or slash (/)
 *         - Floating comment indicators (*>)
 *         - Debugging lines
 *         - Pseudo-text
 *         - Blank lines contains nothing but spaces
 * @author FC
 */
public enum ReferenceFormat {
	FIXED,
	VARIABLE,
	FREE,
}
