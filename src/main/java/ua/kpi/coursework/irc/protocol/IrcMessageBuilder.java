package ua.kpi.coursework.irc.protocol;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public final class IrcMessageBuilder {
    private static final Pattern COMMAND_NAME = Pattern.compile("[A-Za-z]+");
    private static final int MAX_PARAMETERS = 15;
    private static final int MAX_LINE_BYTES = 512;

    private final String command;
    private final List<String> parameters = new ArrayList<>();
    private String trailingText;

    public IrcMessageBuilder(String command) {
        if (command == null || !COMMAND_NAME.matcher(command).matches()) {
            throw new IllegalArgumentException("IRC-команда має складатися з латинських літер.");
        }
        this.command = command.toUpperCase(Locale.ROOT);
    }

    public IrcMessageBuilder parameter(String value) {
        if (trailingText != null) {
            throw new IllegalStateException("Завершальний параметр уже додано.");
        }
        requireSingleLine(value);
        if (value.isEmpty() || value.startsWith(":") || value.contains(" ")) {
            throw new IllegalArgumentException(
                    "Звичайний IRC-параметр має бути непорожнім, без пробілів і початкової двокрапки.");
        }

        parameters.add(value);
        return this;
    }

    public IrcMessageBuilder trailing(String text) {
        if (trailingText != null) {
            throw new IllegalStateException("Завершальний параметр уже додано.");
        }
        requireSingleLine(text);
        trailingText = text;
        return this;
    }

    public byte[] build() {
        int parameterCount = parameters.size();
        if (trailingText != null) {
            parameterCount++;
        }
        if (parameterCount > MAX_PARAMETERS) {
            throw new IllegalStateException("IRC-повідомлення містить понад 15 параметрів.");
        }

        StringBuilder line = new StringBuilder(command);
        for (String parameter : parameters) {
            line.append(' ').append(parameter);
        }
        if (trailingText != null) {
            line.append(" :").append(trailingText);
        }
        line.append("\r\n");

        byte[] messageBytes = line.toString().getBytes(StandardCharsets.UTF_8);
        if (messageBytes.length > MAX_LINE_BYTES) {
            throw new IllegalStateException("IRC-повідомлення перевищує 512 байтів разом із CRLF.");
        }
        return messageBytes;
    }

    private static void requireSingleLine(String value) {
        if (value == null || value.indexOf('\r') >= 0 || value.indexOf('\n') >= 0 || value.indexOf('\0') >= 0) {
            throw new IllegalArgumentException("IRC-параметр має бути рядком без CR, LF або NUL.");
        }
    }
}

