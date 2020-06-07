package notepad;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextStats {
    private final String text;

    public TextStats(String text) {
        this.text = Objects.requireNonNullElse(text, "");
    }

    public int getWordNumber() {
        return numberOfPatternMatches(text, "\\w+");
    }

    public int getCharacterNumber() {
        return numberOfPatternMatches(text, "[\\s\\S.]");
    }

    public int getParagraphsNumber() {
        return numberOfPatternMatches(text, ".+");
    }

    private int numberOfPatternMatches(String text, String patternRegex) {
        int matchNumber = 0;
        Pattern pattern = Pattern.compile(patternRegex);
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            matchNumber++;
        }
        return matchNumber;
    }
}
