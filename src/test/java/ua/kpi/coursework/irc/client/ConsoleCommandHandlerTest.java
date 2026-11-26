package ua.kpi.coursework.irc.client;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConsoleCommandHandlerTest {
    @Test
    void reportsUnknownCommandAndContinues() {
        List<String> messages = new ArrayList<>();
        IrcClient client = new IrcClient();
        ConsoleCommandHandler handler = new ConsoleCommandHandler(client, messages::add);

        boolean shouldContinue = handler.handle("/foo");

        assertTrue(shouldContinue);
        assertEquals(List.of("Невідома команда."), messages);
    }
}