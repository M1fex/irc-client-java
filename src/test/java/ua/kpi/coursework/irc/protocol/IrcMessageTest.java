package ua.kpi.coursework.irc.protocol;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IrcMessageTest {
    @Test
    void parsesPingWithoutPrefixAndNormalizesCommand() {
        IrcMessage message = IrcMessage.parse("ping :irc.example");

        assertNull(message.prefix());
        assertEquals("PING", message.command());
        assertEquals(List.of("irc.example"), message.params());
    }

    @Test
    void parsesPrefixAndMessageText() {
        IrcMessage message = IrcMessage.parse(":bob!u@host PRIVMSG #course :Привіт усім");

        assertEquals("bob!u@host", message.prefix());
        assertEquals("PRIVMSG", message.command());
        assertEquals(List.of("#course", "Привіт усім"), message.params());
    }

    @Test
    void preservesEmptyTrailingParameter() {
        IrcMessage message = IrcMessage.parse(":server 332 alice #course :");

        assertEquals("server", message.prefix());
        assertEquals("332", message.command());
        assertEquals(List.of("alice", "#course", ""), message.params());
    }

    @Test
    void rejectsPrefixWithoutCommand() {
        assertThrows(IllegalArgumentException.class, () -> IrcMessage.parse(":server"));
        assertThrows(IllegalArgumentException.class, () -> IrcMessage.parse(":server   "));
    }

    @Test
    void keepsSpacesAndColonsInTheTrailingParameter() {
        IrcMessage message = IrcMessage.parse("PRIVMSG   #course  :  Привіт: усім  ");

        assertEquals(List.of("#course", "  Привіт: усім  "), message.params());
    }

    @Test
    void keepsLeadingZerosAndSupportsACommandWithoutParameters() {
        assertEquals("001", IrcMessage.parse(":server 001 alice :Welcome").command());
        assertEquals(List.of(), IrcMessage.parse("QUIT").params());
    }

    @Test
    void rejectsMalformedInput() {
        for (String line : List.of("", "   ", ": PING", "12 PING", "PING\r\n", "PING\0")) {
            assertThrows(IllegalArgumentException.class, () -> IrcMessage.parse(line));
        }
    }

    @Test
    void enforcesParameterLimit() {
        assertEquals(15, IrcMessage.parse("CMD" + " x".repeat(14) + " :").params().size());
        assertThrows(IllegalArgumentException.class, () -> IrcMessage.parse("CMD" + " x".repeat(15) + " :"));
    }

    @Test
    void takesAnImmutableSnapshotOfParameters() {
        List<String> params = new ArrayList<>(List.of("irc.example"));
        IrcMessage message = new IrcMessage(null, "PING", params);

        params.clear();

        assertEquals(List.of("irc.example"), message.params());
        assertThrows(UnsupportedOperationException.class, () -> message.params().clear());
    }
}

