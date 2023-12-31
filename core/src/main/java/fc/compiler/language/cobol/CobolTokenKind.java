package fc.compiler.language.cobol;

import fc.compiler.common.token.TokenKind;

/**
 * @author FC
 */
public interface CobolTokenKind extends TokenKind {
	// -- separators --
	public static final String SEPARATOR_COMMA = "SEPARATOR_COMMA";
	public static final String SEPARATOR_SEMICOLON = "SEPARATOR_SEMICOLON";
	public static final String SEPARATOR_PERIOD = "SEPARATOR_PERIOD";

	// -- reserved keywords --
	public static final String DIVISION = "DIVISION";
	public static final String IDENTIFICATION = "IDENTIFICATION";
	public static final String ID = "ID";
	public static final String DATA = "DATA";
	public static final String ENVIRONMENT = "ENVIRONMENT";
	public static final String PROCEDURE = "PROCEDURE";
	public static final String PROGRAM_ID = "PROGRAM-ID";

	public static final String SECTION = "SECTION";
	public static final String FILE = "FILE";
	public static final String WORKING_STORAGE = "WORKING-STORAGE";
	public static final String LINKAGE = "LINKAGE";
	public static final String CONFIGURATION = "CONFIGURATION";
	public static final String INPUT_OUTPUT = "INPUT-OUTPUT";

	public static final String PICTURE = "PICTURE";
	public static final String PIC = "PIC";
	public static final String VALUE = "VALUE";
	public static final String USING = "USING";
	public static final String DISPLAY = "DISPLAY";

//	public static final String FILLER = "FILLER";

	public static final String IS = "IS";
	public static final String ARE = "ARE";
	public static final String END = "END";
	public static final String PROGRAM = "PROGRAM";
	public static final String NOT_EQUAL = "NOT_EQUAL";

	// figure constants
	public static final String ZERO = "ZERO";
	public static final String ZEROS = "ZEROS";
	public static final String ZEROES = "ZEROES";
	public static final String SPACE = "SPACE";
	public static final String SPACES = "SPACES";

	public static final String ACCEPT = "ACCEPT";
	public static final String ALPHABETIC_LOWER = "ALPHABETIC-LOWER";
	public static final String APPLY = "APPLY";
	public static final String ACCESS = "ACCESS";
	public static final String ALPHABETIC_UPPER = "ALPHABETIC-UPPER";
	public static final String ADD = "ADD";
	public static final String ALPHANUMERIC = "ALPHANUMERIC";
	public static final String AREA = "AREA";
	public static final String ADDRESS = "ADDRESS";
	public static final String ALPHANUMERIC_EDITED = "ALPHANUMERIC-EDITED";
	public static final String AREAS = "AREAS";
	public static final String ADVANCING = "ADVANCING";
	public static final String ALSO = "ALSO";
	public static final String ASCENDING = "ASCENDING";
	public static final String AFTER = "AFTER";
	public static final String ALTER = "ALTER";
	public static final String ASSIGN = "ASSIGN";
	public static final String ALL = "ALL";
	public static final String ALTERNATE = "ALTERNATE";
	public static final String AT = "AT";
	public static final String ALPHABET = "ALPHABET";
	public static final String AND = "AND";
	public static final String AUTHOR = "AUTHOR";
	public static final String ALPHABETIC = "ALPHABETIC";
	public static final String ANY = "ANY";
	public static final String BASIS = "BASIS";
	public static final String BINARY = "BINARY";
	public static final String BOTTOM = "BOTTOM";
	public static final String BEFORE = "BEFORE";
	public static final String BLANK = "BLANK";
	public static final String BY = "BY";
	public static final String BEGINNING = "BEGINNING";
	public static final String BLOCK = "BLOCK";
	public static final String CALL = "CALL";
	public static final String COLUMN = "COLUMN";
	public static final String COMPUTATIONAL_5 = "COMPUTATIONAL-5";
	public static final String CANCEL = "CANCEL";
	public static final String COM_REG = "COM-REG";
	public static final String COMPUTE = "COMPUTE";
	public static final String CBL = "CBL";
	public static final String COMMA = "COMMA";
	public static final String CD = "CD";
	public static final String COMMON = "COMMON";
	public static final String CONTAINS = "CONTAINS";
	public static final String CF = "CF";
	public static final String COMMUNICATION = "COMMUNICATION";
	public static final String CONTENT = "CONTENT";
	public static final String CH = "CH";
	public static final String COMP = "COMP";
	public static final String CONTINUE = "CONTINUE";
	public static final String CHARACTER = "CHARACTER";
	public static final String COMP_1 = "COMP-1";
	public static final String CONTROL = "CONTROL";
	public static final String CHARACTERS = "CHARACTERS";
	public static final String COMP_2 = "COMP-2";
	public static final String CONTROLS = "CONTROLS";
	public static final String CLASS = "CLASS";
	public static final String COMP_3 = "COMP-3";
	public static final String CONVERTING = "CONVERTING";
	public static final String CLASS_ID = "CLASS-ID";
	public static final String COMP_4 = "COMP-4";
	public static final String COPY = "COPY";
	public static final String CLOCK_UNITS = "CLOCK-UNITS";
	public static final String COMP_5 = "COMP-5";
	public static final String CORR = "CORR";
	public static final String CLOSE = "CLOSE";
	public static final String COMPUTATIONAL = "COMPUTATIONAL";
	public static final String CORRESPONDING = "CORRESPONDING";
	public static final String COBOL = "COBOL";
	public static final String COMPUTATIONAL_1 = "COMPUTATIONAL-1";
	public static final String COUNT = "COUNT";
	public static final String CODE = "CODE";
	public static final String COMPUTATIONAL_2 = "COMPUTATIONAL-2";
	public static final String CURRENCY = "CURRENCY";
	public static final String CODE_SET = "CODE-SET";
	public static final String COMPUTATIONAL_3 = "COMPUTATIONAL-3";
	public static final String COLLATING = "COLLATING";
	public static final String COMPUTATIONAL_4 = "COMPUTATIONAL-4";
	public static final String DEBUG_SUB_1 = "DEBUG-SUB-1";
	public static final String DESTINATION = "DESTINATION";
	public static final String DATE_COMPILED = "DATE-COMPILED";
	public static final String DEBUG_SUB_2 = "DEBUG-SUB-2";
	public static final String DETAIL = "DETAIL";
	public static final String DATE_WRITTEN = "DATE-WRITTEN";
	public static final String DEBUG_SUB_3 = "DEBUG-SUB-3";
	public static final String DAY = "DAY";
	public static final String DEBUGGING = "DEBUGGING";
	public static final String DISPLAY_1 = "DISPLAY-1";
	public static final String DAY_OF_WEEK = "DAY-OF-WEEK";
	public static final String DECIMAL_POINT = "DECIMAL-POINT";
	public static final String DIVIDE = "DIVIDE";
	public static final String DBCS = "DBCS";
	public static final String DECLARATIVES = "DECLARATIVES";
	public static final String DE = "DE";
	public static final String DELETE = "DELETE";
	public static final String DOWN = "DOWN";
	public static final String DEBUG_CONTENTS = "DEBUG-CONTENTS";
	public static final String DELIMITED = "DELIMITED";
	public static final String DUPLICATES = "DUPLICATES";
	public static final String DEBUG_ITEM = "DEBUG-ITEM";
	public static final String DELIMITER = "DELIMITER";
	public static final String DYNAMIC = "DYNAMIC";
	public static final String DEBUG_LINE = "DEBUG-LINE";
	public static final String DEPENDING = "DEPENDING";
	public static final String DEBUG_NAME = "DEBUG-NAME";
	public static final String DESCENDING = "DESCENDING";
	public static final String EGCS = "EGCS";
	public static final String END_INVOKE = "END-INVOKE";
	public static final String ENDING = "ENDING";
	public static final String EGI = "EGI";
	public static final String END_MULTIPLY = "END-MULTIPLY";
	public static final String ENTER = "ENTER";
	public static final String EJECT = "EJECT";
	public static final String END_OF_PAGE = "END-OF-PAGE";
	public static final String ENTRY = "ENTRY";
	public static final String ELSE = "ELSE";
	public static final String END_PERFORM = "END-PERFORM";
	public static final String EMI = "EMI";
	public static final String END_READ = "END-READ";
	public static final String EOP = "EOP";
	public static final String ENABLE = "ENABLE";
	public static final String END_RECEIVE = "END-RECEIVE";
	public static final String END_RETURN = "END-RETURN";
//	public static final String ERROR = "ERROR";
	public static final String END_ADD = "END-ADD";
	public static final String END_REWRITE = "END-REWRITE";
	public static final String ESI = "ESI";
	public static final String END_CALL = "END-CALL";
	public static final String END_SEARCH = "END-SEARCH";
	public static final String EVALUATE = "EVALUATE";
	public static final String END_COMPUTE = "END-COMPUTE";
	public static final String END_START = "END-START";
	public static final String EVERY = "EVERY";
	public static final String END_DELETE = "END-DELETE";
	public static final String END_STRING = "END-STRING";
	public static final String EXCEPTION = "EXCEPTION";
	public static final String END_DIVIDE = "END-DIVIDE";
	public static final String END_SUBTRACT = "END-SUBTRACT";
	public static final String EXIT = "EXIT";
	public static final String END_EVALUATE = "END-EVALUATE";
	public static final String END_UNSTRING = "END-UNSTRING";
	public static final String EXTEND = "EXTEND";
	public static final String END_IF = "END-IF";
	public static final String END_WRITE = "END-WRITE";
	public static final String EXTERNAL = "EXTERNAL";
	public static final String FALSE = "FALSE";
	public static final String FILLER = "FILLER";
	public static final String FD = "FD";
	public static final String FINAL = "FINAL";
	public static final String FROM = "FROM";
	public static final String FIRST = "FIRST";
	public static final String FUNCTION = "FUNCTION";
	public static final String FILE_CONTROL = "FILE-CONTROL";
	public static final String FOOTING = "FOOTING";
	public static final String GENERATE = "GENERATE";
	public static final String GO = "GO";
	public static final String GROUP = "GROUP";
	public static final String GIVING = "GIVING";
	public static final String GOBACK = "GOBACK";
	public static final String GLOBAL = "GLOBAL";
	public static final String GREATER = "GREATER";
	public static final String HEADING = "HEADING";
	public static final String HIGH_VALUE = "HIGH-VALUE";
	public static final String HIGH_VALUES = "HIGH-VALUES";
	public static final String I_O = "I-O";
	public static final String INDICATE = "INDICATE";
	public static final String INSPECT = "INSPECT";
	public static final String I_O_CONTROL = "I-O-CONTROL";
	public static final String INHERITS = "INHERITS";
	public static final String INSTALLATION = "INSTALLATION";
	public static final String INITIAL = "INITIAL";
	public static final String INTO = "INTO";
	public static final String INITIALIZE = "INITIALIZE";
	public static final String INVALID = "INVALID";
	public static final String INITIATE = "INITIATE";
	public static final String INVOKE = "INVOKE";
	public static final String IN = "IN";
	public static final String INPUT = "INPUT";
	public static final String INDEX = "INDEX";
	public static final String INDEXED = "INDEXED";
	public static final String INSERT = "INSERT";
	public static final String JUST = "JUST";
	public static final String JUSTIFIED = "JUSTIFIED";
	public static final String KANJI = "KANJI";
	public static final String KEY = "KEY";
	public static final String LABEL = "LABEL";
	public static final String LIMIT = "LIMIT";
	public static final String LINES = "LINES";
	public static final String LAST = "LAST";
	public static final String LIMITS = "LIMITS";
	public static final String LEADING = "LEADING";
	public static final String LINAGE = "LINAGE";
	public static final String LOCAL_STORAGE = "LOCAL-STORAGE";
	public static final String LEFT = "LEFT";
	public static final String LINAGE_COUNTER = "LINAGE-COUNTER";
	public static final String LOCK = "LOCK";
	public static final String LENGTH = "LENGTH";
	public static final String LINE = "LINE";
	public static final String LOW_VALUE = "LOW-VALUE";
	public static final String LESS = "LESS";
	public static final String LINE_COUNTER = "LINE-COUNTER";
	public static final String LOW_VALUES = "LOW-VALUES";
	public static final String MEMORY = "MEMORY";
	public static final String METHOD = "METHOD";
	public static final String MORE_LABELS = "MORE-LABELS";
	public static final String MERGE = "MERGE";
	public static final String METHOD_ID = "METHOD-ID";
	public static final String MOVE = "MOVE";
	public static final String MESSAGE = "MESSAGE";
	public static final String MODE = "MODE";
	public static final String MULTIPLE = "MULTIPLE";
	public static final String METACLASS = "METACLASS";
	public static final String MODULES = "MODULES";
	public static final String MULTIPLY = "MULTIPLY";
	public static final String NATIVE = "NATIVE";
	public static final String NO = "NO";
	public static final String NUMBER = "NUMBER";
	public static final String NATIVE_BINARY = "NATIVE_BINARY";
	public static final String NOT = "NOT";
	public static final String NUMERIC = "NUMERIC";
	public static final String NEGATIVE = "NEGATIVE";
	public static final String NULL = "NULL";
	public static final String NUMERIC_EDITED = "NUMERIC-EDITED";
	public static final String NEXT = "NEXT";
	public static final String NULLS = "NULLS";
	public static final String OBJECT = "OBJECT";
	public static final String ON = "ON";
	public static final String OTHER = "OTHER";
	public static final String OBJECT_COMPUTER = "OBJECT-COMPUTER";
	public static final String OPEN = "OPEN";
	public static final String OUTPUT = "OUTPUT";
	public static final String OCCURS = "OCCURS";
	public static final String OPTIONAL = "OPTIONAL";
	public static final String OVERFLOW = "OVERFLOW";
	public static final String OF = "OF";
	public static final String OR = "OR";
	public static final String OVERRIDE = "OVERRIDE";
	public static final String OFF = "OFF";
	public static final String ORDER = "ORDER";
	public static final String OMITTED = "OMITTED";
	public static final String ORGANIZATION = "ORGANIZATION";
	public static final String PACKED_DECIMAL = "PACKED-DECIMAL";
	public static final String PROCEDURE_POINTER = "PROCEDURE-POINTER";
	public static final String PADDING = "PADDING";
	public static final String PROCEDURES = "PROCEDURES";
	public static final String PAGE = "PAGE";
	public static final String PROCEED = "PROCEED";
	public static final String PAGE_COUNTER = "PAGE-COUNTER";
	public static final String POINTER = "POINTER";
	public static final String PROCESSING = "PROCESSING";
	public static final String PASSWORD = "PASSWORD";
	public static final String POSITION = "POSITION";
	public static final String PERFORM = "PERFORM";
	public static final String POSITIVE = "POSITIVE";
	public static final String PF = "PF";
	public static final String PRINTING = "PRINTING";
	public static final String PURGE = "PURGE";
	public static final String PH = "PH";
	public static final String QUEUE = "QUEUE";
	public static final String QUOTE = "QUOTE";
	public static final String QUOTES = "QUOTES";
	public static final String RANDOM = "RANDOM";
	public static final String RELATIVE = "RELATIVE";
	public static final String RESERVE = "RESERVE";
	public static final String RD = "RD";
	public static final String RELEASE = "RELEASE";
	public static final String RESET = "RESET";
	public static final String READ = "READ";
	public static final String RELOAD = "RELOAD";
	public static final String RETURN = "RETURN";
	public static final String READY = "READY";
	public static final String REMAINDER = "REMAINDER";
	public static final String RETURN_CODE = "RETURN-CODE";
	public static final String RECEIVE = "RECEIVE";
	public static final String REMOVAL = "REMOVAL";
	public static final String RETURNING = "RETURNING";
	public static final String RECORD = "RECORD";
	public static final String RENAMES = "RENAMES";
	public static final String REVERSED = "REVERSED";
	public static final String RECORDING = "RECORDING";
	public static final String REPLACE = "REPLACE";
	public static final String REWIND = "REWIND";
	public static final String RECORDS = "RECORDS";
	public static final String REPLACING = "REPLACING";
	public static final String REWRITE = "REWRITE";
	public static final String RECURSIVE = "RECURSIVE";
	public static final String REPORT = "REPORT";
	public static final String RF = "RF";
	public static final String REDEFINES = "REDEFINES";
	public static final String REPORTING = "REPORTING";
	public static final String RH = "RH";
	public static final String REEL = "REEL";
	public static final String REPORTS = "REPORTS";
	public static final String RIGHT = "RIGHT";
	public static final String REFERENCE = "REFERENCE";
	public static final String REPOSITORY = "REPOSITORY";
	public static final String ROUNDED = "ROUNDED";
	public static final String REFERENCES = "REFERENCES";
	public static final String RERUN = "RERUN";
	public static final String RUN = "RUN";
	public static final String SAME = "SAME";
	public static final String SIGN = "SIGN";
	public static final String STANDARD = "STANDARD";
	public static final String SD = "SD";
	public static final String SIZE = "SIZE";
	public static final String STANDARD_1 = "STANDARD-1";
	public static final String SEARCH = "SEARCH";
	public static final String SKIP1 = "SKIP1";
	public static final String STANDARD_2 = "STANDARD-2";
	public static final String SKIP2 = "SKIP2";
	public static final String START = "START";
	public static final String SECURITY = "SECURITY";
	public static final String SKIP3 = "SKIP3";
	public static final String STATUS = "STATUS";
	public static final String SEGMENT = "SEGMENT";
	public static final String SORT = "SORT";
	public static final String STOP = "STOP";
	public static final String SEGMENT_LIMIT = "SEGMENT-LIMIT";
	public static final String SORT_CONTROL = "SORT-CONTROL";
	public static final String STRING = "STRING";
	public static final String SELECT = "SELECT";
	public static final String SORT_CORE_SIZE = "SORT-CORE-SIZE";
	public static final String SUB_QUEUE_1 = "SUB-QUEUE-1";
	public static final String SELF = "SELF";
	public static final String SORT_FILE_SIZE = "SORT-FILE-SIZE";
	public static final String SUB_QUEUE_2 = "SUB-QUEUE-2";
	public static final String SEND = "SEND";
	public static final String SORT_MERGE = "SORT-MERGE";
	public static final String SUB_QUEUE_3 = "SUB-QUEUE-3";
	public static final String SENTENCE = "SENTENCE";
	public static final String SORT_MESSAGE = "SORT-MESSAGE";
	public static final String SUBTRACT = "SUBTRACT";
	public static final String SEPARATE = "SEPARATE";
	public static final String SORT_MODE_SIZE = "SORT-MODE-SIZE";
	public static final String SUM = "SUM";
	public static final String SEQUENCE = "SEQUENCE";
	public static final String SORT_RETURN = "SORT-RETURN";
	public static final String SUPER = "SUPER";
	public static final String SEQUENTIAL = "SEQUENTIAL";
	public static final String SOURCE = "SOURCE";
	public static final String SUPPRESS = "SUPPRESS";
	public static final String SERVICE = "SERVICE";
	public static final String SOURCE_COMPUTER = "SOURCE-COMPUTER";
	public static final String SYMBOLIC = "SYMBOLIC";
	public static final String SET = "SET";
	public static final String SYNC = "SYNC";
	public static final String SHIFT_IN = "SHIFT-IN";
	public static final String SYNCHRONIZED = "SYNCHRONIZED";
	public static final String SHIFT_OUT = "SHIFT-OUT";
	public static final String SPECIAL_NAMES = "SPECIAL-NAMES";
	public static final String TABLE = "TABLE";
	public static final String TEXT = "TEXT";
	public static final String TITLE = "TITLE";
	public static final String TALLY = "TALLY";
	public static final String THAN = "THAN";
	public static final String TO = "TO";
	public static final String TALLYING = "TALLYING";
	public static final String THEN = "THEN";
	public static final String TOP = "TOP";
	public static final String TAPE = "TAPE";
	public static final String THROUGH = "THROUGH";
	public static final String TRACE = "TRACE";
	public static final String TERMINAL = "TERMINAL";
	public static final String THRU = "THRU";
	public static final String TRAILING = "TRAILING";
	public static final String TERMINATE = "TERMINATE";
	public static final String TIME = "TIME";
	public static final String TRUE = "TRUE";
	public static final String TEST = "TEST";
	public static final String TIMES = "TIMES";
	public static final String TYPE = "TYPE";
	public static final String UNIT = "UNIT";
	public static final String UP = "UP";
	public static final String USE = "USE";
	public static final String UNSTRING = "UNSTRING";
	public static final String UPON = "UPON";
	public static final String UNTIL = "UNTIL";
	public static final String USAGE = "USAGE";
	public static final String VALUES = "VALUES";
	public static final String VARYING = "VARYING";
	public static final String WHEN = "WHEN";
	public static final String WORDS = "WORDS";
	public static final String WRITE_ONLY = "WRITE-ONLY";
	public static final String WHEN_COMPILED = "WHEN-COMPILED";
	public static final String WITH = "WITH";
	public static final String WRITE = "WRITE";

}
