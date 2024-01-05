
## Grammar 
### Extended BNF - Syntactic metalanguage - ISO/IEC 14977:1996.
https://www.iso.org/standard/26153.html     
https://www.cs.man.ac.uk/~pjj/bnf/ebnf.html BNF/EBNF variants and comparison

```
    Usage               Notation
    ----------------------------------
    definition          =
    concatenation       ,
    termination         ;
    alternation         |
    optional            [ ... ]
    repetition          { ... }
    grouping            ( ... )
    terminal string     " ... "
    terminal string     ' ... '
    comment             (* ... *)
    special sequence    ? ... ?
    exception           -
```
```
Even EBNF can be described using EBNF. 
Consider below grammar (using conventions such as "-" to indicate set disjunction, "+" to indicate one or more matches, and "?" for optionality):

letter = "A" | "B" | "C" | "D" | "E" | "F" | "G"
       | "H" | "I" | "J" | "K" | "L" | "M" | "N"
       | "O" | "P" | "Q" | "R" | "S" | "T" | "U"
       | "V" | "W" | "X" | "Y" | "Z" | "a" | "b"
       | "c" | "d" | "e" | "f" | "g" | "h" | "i"
       | "j" | "k" | "l" | "m" | "n" | "o" | "p"
       | "q" | "r" | "s" | "t" | "u" | "v" | "w"
       | "x" | "y" | "z" ;

digit = "0" | "1" | "2" | "3" | "4" | "5" | "6" | "7" | "8" | "9" ;

symbol = "[" | "]" | "{" | "}" | "(" | ")" | "<" | ">"
       | "'" | '"' | "=" | "|" | "." | "," | ";" | "-" 
       | "+" | "*" | "?" | "\n" | "\t" | "\r" | "\f" | "\b" ;

character = letter | digit | symbol | "_" | " " ;
identifier = letter , { letter | digit | "_" } ;

S = { " " | "\n" | "\t" | "\r" | "\f" | "\b" } ;

terminal = "'" , character - "'" , { character - "'" } , "'"
         | '"' , character - '"' , { character - '"' } , '"' ;

terminator = ";" | "." ;

term = "(" , S , rhs , S , ")"
     | "[" , S , rhs , S , "]"
     | "{" , S , rhs , S , "}"
     | terminal
     | identifier ;

factor = term , S , "?"
       | term , S , "*"
       | term , S , "+"
       | term , S , "-" , S , term
       | term , S ;

concatenation = ( S , factor , S , "," ? ) + ;
alternation = ( S , concatenation , S , "|" ? ) + ;

rhs = alternation ;
lhs = identifier ;

rule = lhs , S , "=" , S , rhs , S , terminator ;

grammar = ( S , rule , S ) * ;
```

### ANTLR
https://github.com/antlr/antlr4 ANTLR v4
http://lab.antlr.org/  ANTLR lab, where you can learn about ANTLR or experiment with and test grammars

```
/** Optional javadoc style comment */
grammar Name;          // both lexical and parser rules
parser grammar Name;   // only parser rules
lexer grammar Name;    // only lexical rules
options {...}
import ... ;
 	
tokens {...}
channels { WHITESPACE_CHANNEL, COMMENTS_CHANNEL, ...} // lexer only
@actionName {...}
 	 
rule1 // parser and lexer rules, possibly intermingled
...
ruleN

ruleName : alternative1 | ... | alternativeN ;
/** Parser rule names must start with a lowercase letter and lexer rules must start with a capital letter. */

WS : [ \r\t\n]+ -> channel(WHITESPACE_CHANNEL) ;

```

## COBOL
https://www.ibm.com/docs/en/SS6SG3_6.4.0/pdf/lrmvs.pdf  IBM Enterprise COBOL 6.4 Language Reference
https://software.fujitsu.com/jp/manual/manualindex/p14000307e.html
https://software.fujitsu.com/jp/manual/manualfiles/m140018/b1wd3304/02enz000/b1wd-3304-02enz0.pdf   NetCOBOL V11.0 Language Reference
https://software.fujitsu.com/jp/manual/manualfiles/m140018/b1wd3300/02enz000/b1wd-3300-02enz0.pdf
https://github.com/uwol/proleap-cobol-parser        ProLeap ANTLR4-based parser for COBOL
https://github.com/proleap/proleap-cobol    ProLeap ANTLR4-based analyzer, interpreter & transformer for COBOL
https://github.com/krisds/koopa       Koopa (COBOL) Parser Generator
https://www.itl.nist.gov/div897/ctg/cobol_form.htm      NIST COBOL85 Test Suites
https://www.mainframestechhelp.com/tutorials/cobol/character-sets.htm 
