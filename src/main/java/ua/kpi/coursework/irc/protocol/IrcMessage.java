package ua.kpi.coursework.irc.protocol;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public record IrcMessage(String prefix, String command, List<String> params) {
    private static final Pattern COMMAND_PATTERN = Pattern.compile("[A-Za-z]+|[0-9]{3}");
    private static final int MAX_PARAMS = 15;

    public IrcMessage {
        params = List.copyOf(params);
    }

    public static IrcMessage parse(String line) {
        if (line == null || line.indexOf('\r') >= 0 || line.indexOf('\n') >= 0 || line.indexOf('\0') >= 0) {
            throw new IllegalArgumentException("Очікується один IRC-рядок без CR, LF або NUL.");
        }

        int cursor = skipSpaces(line, 0);
        String prefix = null;
        if (cursor < line.length() && line.charAt(cursor) == ':') {
            int prefixStart = cursor + 1;
            int prefixEnd = line.indexOf(' ', prefixStart);
            if (prefixEnd == -1 || prefixEnd == prefixStart) {
                throw new IllegalArgumentException("Некоректний IRC-префікс або відсутня команда.");
            }

            prefix = line.substring(prefixStart, prefixEnd);
            cursor = skipSpaces(line, prefixEnd);
        }

        int commandEnd = tokenEnd(line, cursor);
        String command = line.substring(cursor, commandEnd);
        if (!COMMAND_PATTERN.matcher(command).matches()) {
            throw new IllegalArgumentException("Некоректна або відсутня IRC-команда.");
        }

        List<String> params = new ArrayList<>();
        cursor = skipSpaces(line, commandEnd);
        while (cursor < line.length()) {
            if (params.size() == MAX_PARAMS) {
                throw new IllegalArgumentException("IRC-повідомлення містить понад 15 параметрів.");
            }

            if (line.charAt(cursor) == ':') {
                params.add(line.substring(cursor + 1));
                break;
            }

            int paramEnd = tokenEnd(line, cursor);
            params.add(line.substring(cursor, paramEnd));
            cursor = skipSpaces(line, paramEnd);
        }

        return new IrcMessage(prefix, command.toUpperCase(Locale.ROOT), params);
    }

    private static int skipSpaces(String line, int cursor) {
        while (cursor < line.length() && line.charAt(cursor) == ' ') {
            cursor++;
        }
        return cursor;
    }

    private static int tokenEnd(String line, int cursor) {
        int end = line.indexOf(' ', cursor);
        return end == -1 ? line.length() : end;
    }
}
