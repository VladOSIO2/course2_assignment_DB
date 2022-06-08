package utility;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {
    public static String splitStringOnLines(String s, int charsOnLine) {
        String regex = ".{1," + charsOnLine + "}\\s";
        Matcher m = Pattern.compile(regex).matcher(s);
        ArrayList<String> lines = new ArrayList<>();
        while(m.find()) {
            lines.add(m.group());
        }
        return String.join("\n", lines);

    }


}
