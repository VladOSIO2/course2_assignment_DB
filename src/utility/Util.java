package utility;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    public static LocalDateTime sqlDTToLocalDT(String sqlDT) {
        DateTimeFormatter dtf =  DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(sqlDT, dtf);
    }
}
