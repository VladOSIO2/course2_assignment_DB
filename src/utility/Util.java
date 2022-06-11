package utility;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {
    public static String splitStringOnLines(String s, int charsOnLine) {
        int size = 0;
        StringBuilder sb = new StringBuilder();
        for (String s1 : s.split("\\s+")) {
            size += s1.length() + 1;
            if (size < charsOnLine) {
                sb.append(" ");
            } else {
                sb.append("\n");
                size = 0;
            }
            sb.append(s1);
        }
        return sb.toString();
    }


}
