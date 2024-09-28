package fc.compiler.language.antlr.modern;

import fc.compiler.common.lexer.CodeReaderBase;
import fc.compiler.common.lexer.IdentifierLexer;
import fc.compiler.common.lexer.LexerBase;
import fc.compiler.common.lexer.LexerMapper;
import fc.compiler.common.token.Token;
import fc.compiler.language.java.JavaLexer;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;

import static fc.compiler.common.lexer.Constants.*;
import static fc.compiler.common.lexer.Constants.CR;
import static fc.compiler.common.lexer.Constants.EOF;
import static fc.compiler.common.token.TokenKind.*;

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
public class AntlrLexer extends LexerBase {
	public AntlrLexer() {
		this.mapper = initLexerMapper();
		initReservedKeywords();
	}

	public LexerMapper initLexerMapper() {
		LexerMapper mapper = new LexerMapper();
		mapper.mapLexer(EOF, LexerBase::scanEOF);
		mapper.mapLexer(SPACE,  LexerBase::scanWhiteSpaces);
		mapper.mapLexer(TAB,    LexerBase::scanWhiteSpaces);
		mapper.mapLexer(FF,     LexerBase::scanWhiteSpaces);
		mapper.mapLexer(LF,     LexerBase::scanLineTerminator);
		mapper.mapLexer(CR,     LexerBase::scanLineTerminator);

		IdentifierLexer idLexer = new IdentifierLexer().caseSensitive(true);
		for (char c = 'a'; c <= 'z'; c++) {
			mapper.mapLexer(c,     idLexer);
		}
		for (char c = 'A'; c <= 'Z'; c++) {
			mapper.mapLexer(c,     idLexer);
		}

		mapper.mapLexer('0', JavaLexer::scanNumber);
		for (char c = '1'; c < '9'; c++) {
			mapper.mapLexer(c,     JavaLexer::scanNumber);
		}

		mapper.mapLexer(';', LexerBase::scanSemicolon);
		mapper.mapLexer(':', LexerBase::scanColon);
		mapper.mapLexer('/', AntlrLexer::onSlash);
		mapper.mapLexer('?', LexerBase::scanQuestion);
		mapper.mapLexer('*', LexerBase::scanStar);
		mapper.mapLexer('+', LexerBase::scanPlus);
		mapper.mapLexer('(', LexerBase::scanLeftParen);
		mapper.mapLexer('[', LexerBase::scanLeftBracket);
		mapper.mapLexer('{', LexerBase::scanLeftBrace);
		mapper.mapLexer(')', LexerBase::scanRightParen);
		mapper.mapLexer(']', LexerBase::scanRightBracket);
		mapper.mapLexer('}', LexerBase::scanRightBrace);
		mapper.mapLexer('|', LexerBase::scanBar);
		mapper.mapLexer('\'', AntlrLexer::onSingleQuote);

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

	public static Token onSlash(CodeReaderBase reader) {
		reader.accept('/');
		if (reader.accept('/')) {           // "//" for line comment
			return scanLineComment(reader);
		} else if (reader.accept('*')) {
			return JavaLexer.scanBlockComment(reader);
		} else {
			return new Token(SLASH, reader.position).lexeme(reader.lexeme());
		}
	}

	public static Token onSingleQuote(CodeReaderBase reader) {
		Token token = scanStringLiteral(reader, '\'');
//
//		String newKind = mapLiteralToTokenKind.get(token.attribute("value"));
//		if (newKind != null) {
//			token.kind(newKind);
//		}
		return token;
	}
}
