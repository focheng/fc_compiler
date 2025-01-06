package fc.compiler.common.util;

/**
 * @author FC
 */
public class StrUtils {
	/**
	 * '_' is the difference from apache commons lang.
	 */
	public static boolean isAllUpperCase(CharSequence cs) {
		if (cs == null || cs.length() == 0) {
			return false;
		} else {
			int sz = cs.length();
			for(int i = 0; i < sz; ++i) {
				if (!Character.isUpperCase(cs.charAt(i))
						&& cs.charAt(i) != '_') {
					return false;
				}
			}

			return true;
		}
	}

	public static boolean containsLetterOrDigit(CharSequence chars) {
		for (int i = 0, length = chars.length(); i < length; i++) {
			char c = chars.charAt(i);
			if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9'))
				return true;
		}
		return false;
	}
}
