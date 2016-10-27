package at.storm.commons;

import java.lang.Character.UnicodeBlock;
import java.util.HashSet;

public class LanguageDetection {

	private static final double IS_ENGLISH_THRESHOLD = 0.3;
	
	private static final HashSet<UnicodeBlock> unwantedLanguageUnicodeBlocks = new HashSet<UnicodeBlock>() {
		private static final long serialVersionUID = -1831266386669962336L;
	{
	    add(UnicodeBlock.CJK_COMPATIBILITY);
	    add(UnicodeBlock.CJK_COMPATIBILITY_FORMS);
	    add(UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS);
	    add(UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS_SUPPLEMENT);
	    add(UnicodeBlock.CJK_RADICALS_SUPPLEMENT);
	    add(UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION);
	    add(UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS);
	    add(UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A);
	    add(UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B);
	    add(UnicodeBlock.KANGXI_RADICALS);
	    add(UnicodeBlock.IDEOGRAPHIC_DESCRIPTION_CHARACTERS);
	    add(UnicodeBlock.CYRILLIC);
	}};
	
	public static boolean isEnglish(String message) {
		double countOccurences = 0.0;
		for (int i = 0; i < message.length(); i++) {
			if(unwantedLanguageUnicodeBlocks.contains(UnicodeBlock.of(message.charAt(i)))) {
				countOccurences += 1.0;
			}
		}
		if(countOccurences / message.length() > IS_ENGLISH_THRESHOLD) {
			return false;	
		}
		return true;
	}
}
