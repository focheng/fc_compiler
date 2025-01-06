package fc.compiler.language.pli;

import lombok.AllArgsConstructor;

/**
 * Priority of operators.
 * @author FC
 */
@AllArgsConstructor
public enum PliOperatorPriority {
	POWER					(900),
	PREFIX_PLUS_MINUS		(800),
	PREFIX_NOT				(700),
	MULTIPLICATION_DIVISION	(600),
	INFIX_PLUS_MINUS		(500),
	CONCATENATION			(400),
	COMPIRSON				(300),
	BIT_AND					(200),
	BIT_XOR					(100),
	BIT_OR					( 90),
	;

	int value;
}
