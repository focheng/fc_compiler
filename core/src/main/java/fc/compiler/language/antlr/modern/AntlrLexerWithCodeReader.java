package fc.compiler.language.antlr.modern;

import fc.compiler.common.lexer.CodeReaderBase;
import fc.compiler.common.lexer.IdentifierLexerWithCodeReader;
import fc.compiler.common.lexer.LexerWithCodeReaderBase;
import fc.compiler.common.lexer.LexerMapper;
import fc.compiler.common.token.StringToken;
import fc.compiler.language.java.JavaLexerWithCodeReader;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;

import static fc.compiler.common.lexer.Constants.*;
import static fc.compiler.common.lexer.Constants.CR;
import static fc.compiler.common.lexer.Constants.EOF;
import static fc.compiler.common.token.StringTokenKind.*;

/**
 * Antlr Extended Lexer.
 * - The name of lexer rule must be all UPPER_CASE.
 * - Lexer rule can be explicitly defined in .g4 file.
 * - If lexer rule is not explicitly defined,
 *   1. firstly,  try to match the built-in lexer rule. e.g. NEWLINE : '\r'? '\n' -> channel(HIDDEN) ;
 *   2. secondly, try to convert following naming convention.
 *                  e.g. KEYWORD match "keyword".
 *                  e.g. END_IF matches "end-if";
 *   3. report undefined error.
 * @author FC
 */
@Slf4j
public class AntlrLexerWithCodeReader extends LexerWithCodeReaderBase {
	public AntlrLexerWithCodeReader() {
		this.mapper = initLexerMapper();
		initReservedKeywords();
	}

	public LexerMapper initLexerMapper() {
		LexerMapper mapper = new LexerMapper();
		mapper.mapLexer(EOF, LexerWithCodeReaderBase::scanEOF);
		mapper.mapLexer(SPACE,  LexerWithCodeReaderBase::scanWhiteSpaces);
		mapper.mapLexer(TAB,    LexerWithCodeReaderBase::scanWhiteSpaces);
		mapper.mapLexer(FF,     LexerWithCodeReaderBase::scanWhiteSpaces);
		mapper.mapLexer(LF,     LexerWithCodeReaderBase::scanLineTerminator);
		mapper.mapLexer(CR,     LexerWithCodeReaderBase::scanLineTerminator);

		IdentifierLexerWithCodeReader idLexer = new IdentifierLexerWithCodeReader().caseSensitive(true);
		for (char c = 'a'; c <= 'z'; c++) {
			mapper.mapLexer(c,     idLexer);
		}
		for (char c = 'A'; c <= 'Z'; c++) {
			mapper.mapLexer(c,     idLexer);
		}

		mapper.mapLexer('0', JavaLexerWithCodeReader::scanNumber);
		for (char c = '1'; c < '9'; c++) {
			mapper.mapLexer(c,     JavaLexerWithCodeReader::scanNumber);
		}

		mapper.mapLexer(';', LexerWithCodeReaderBase::scanSemicolon);
		mapper.mapLexer(':', LexerWithCodeReaderBase::scanColon);
		mapper.mapLexer('/', AntlrLexerWithCodeReader::onSlash);
		mapper.mapLexer('?', LexerWithCodeReaderBase::scanQuestion);
		mapper.mapLexer('*', LexerWithCodeReaderBase::scanStar);
		mapper.mapLexer('+', LexerWithCodeReaderBase::scanPlus);
		mapper.mapLexer('(', LexerWithCodeReaderBase::scanLeftParen);
		mapper.mapLexer('[', LexerWithCodeReaderBase::scanLeftBracket);
		mapper.mapLexer('{', LexerWithCodeReaderBase::scanLeftBrace);
		mapper.mapLexer(')', LexerWithCodeReaderBase::scanRightParen);
		mapper.mapLexer(']', LexerWithCodeReaderBase::scanRightBracket);
		mapper.mapLexer('}', LexerWithCodeReaderBase::scanRightBrace);
		mapper.mapLexer('|', LexerWithCodeReaderBase::scanBar);
		mapper.mapLexer('\'', AntlrLexerWithCodeReader::onSingleQuote);

		return mapper;
	}

	public void initReservedKeywords() {
		try {
			for (Field field : AntlrKeywords.class.getDeclaredFields()) {
				if (field.getType().equals(String.class)) {
					String keyword = (String) field.get(null);
					reservedKeywords.put(keyword, keyword);
				}
			}
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public static StringToken onSlash(CodeReaderBase reader) {
		reader.optionalChar('/');
		if (reader.optionalChar('/')) {           // "//" for line comment
			return scanLineComment(reader);
		} else if (reader.optionalChar('*')) {
			return JavaLexerWithCodeReader.scanBlockComment(reader);
		} else {
			return new StringToken(SLASH, reader.lexeme(), reader.position);
		}
	}

	public static StringToken onSingleQuote(CodeReaderBase reader) {
		StringToken token = scanStringLiteral(reader, '\'');
//
//		String newKind = mapLiteralToTokenKind.get(token.attribute("value"));
//		if (newKind != null) {
//			token.kind(newKind);
//		}
		return token;
	}
}
