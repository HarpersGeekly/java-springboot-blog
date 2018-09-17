package com.codeup.blog.springbootblog.Models;

import java.text.Normalizer;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.regex.Pattern;

public class FormatterUtil {

    public FormatterUtil(){}

    private static final NavigableMap<Long, String> suffixes = new TreeMap<>();
    static {
        suffixes.put(1_000L, "k");
        suffixes.put(1_000_000L, "M");
        suffixes.put(1_000_000_000L, "G");
        suffixes.put(1_000_000_000_000L, "T");
        suffixes.put(1_000_000_000_000_000L, "P");
        suffixes.put(1_000_000_000_000_000_000L, "E");
    }

    public String formatCount(long value) {
        //Long.MIN_VALUE == -Long.MIN_VALUE so we need an adjustment here
        if (value == Long.MIN_VALUE) return formatCount(Long.MIN_VALUE + 1);
        if (value < 0) return "-" + formatCount(-value);
        if (value < 1000) return Long.toString(value); //deal with easy case

        Map.Entry<Long, String> e = suffixes.floorEntry(value);
        Long divideBy = e.getKey();
        String suffix = e.getValue();

        long truncated = value / (divideBy / 10); //the number part of the output times 10
        boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);
        return hasDecimal ? (truncated / 10d) + suffix : (truncated / 10) + suffix;
    }

    // TITLE STRING MANIPULATION ==============================================================

    public String titleToUppercase(String title) {

        StringBuffer sb = new StringBuffer();

        String[] sentence = title.split(" ");

//        String dontcap =
//                "a, an, the, and, as, as if, as long as, at, is, but, by, even if, for, from, if, if only, in, into, like, near, now, nor, of, off, on, on top of, once, onto, or, out of, over, past, so, than, that, till, to, up, upon, with, when, yet";
//
//        String[] wordsNotCapitalized = dontcap.split(",");
//
//        for(String wrd : wordsNotCapitalized) {
//            System.out.println(wrd);
//        }

        for (String word : sentence) {

            char[] letters = word.toCharArray(); // no need to trim()

            for (int i = 0; i < letters.length; i++) {
                if (letters[i] != '*' || letters[i] != '\"') {
                    // Capitalize the first non-asterisk (even if that doesn't change it)
                    letters[i] = Character.toUpperCase(letters[i]);
                    // No need to look any further
                    break;
                }

            }
            // That's it for capitalizing!
            word = new String(letters);
            sb.append(word).append(" ");
        }
        return sb.toString().trim();
    }

    // CREATING A SLUG FOR URL, this-is-a-slug-in-url ==================================================================

    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");

    public String makeSlug(String input) {
        String nowhitespace = WHITESPACE.matcher(input).replaceAll("-");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        String slug = NONLATIN.matcher(normalized).replaceAll("");
        return slug.toLowerCase(Locale.ENGLISH);
    }
}
