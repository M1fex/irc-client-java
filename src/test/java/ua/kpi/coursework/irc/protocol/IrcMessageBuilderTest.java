package ua.kpi.coursework.irc.protocol;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IrcMessageBuilderTest {
    @Test
    void buildsUserWithANameContainingSpaces() {
        byte[] actual = new IrcMessageBuilder("user")
                .parameter("alice")
                .parameter("0")
                .parameter("*")
                .trailing("Alice Example")
                .build();

        assertArrayEquals("USER alice 0 * :Alice Example\r\n".getBytes(StandardCharsets.UTF_8), actual);
    }

    @Test
    void buildsJoinWithAnOrdinaryParameter() {
        byte[] actual = new IrcMessageBuilder("JOIN")
                .parameter("#course")
                .build();

        assertArrayEquals("JOIN #course\r\n".getBytes(StandardCharsets.UTF_8), actual);
    }

    @Test
    void buildsPrivmsgInUtf8AndPreservesSpaces() {
        byte[] actual = new IrcMessageBuilder("PRIVMSG")
                .parameter("#course")
                .trailing("Привіт  усім ")
                .build();

        assertArrayEquals("PRIVMSG #course :Привіт  усім \r\n".getBytes(StandardCharsets.UTF_8), actual);
    }

    @Test
    void preservesAnEmptyTrailingParameter() {
        byte[] actual = new IrcMessageBuilder("TOPIC")
                .parameter("#course")
                .trailing("")
                .build();

        assertArrayEquals("TOPIC #course :\r\n".getBytes(StandardCharsets.UTF_8), actual);
    }

    @Test
    void rejectsInvalidCommandNames() {
        for (String command : List.of("", "001", "JOIN #course")) {
            assertThrows(IllegalArgumentException.class, () -> new IrcMessageBuilder(command));
        }
    }

    @Test
    void rejectsInvalidOrdinaryParameters() {
        for (String parameter : List.of("", ":nick", "two words", "two\rwords")) {
            assertThrows(IllegalArgumentException.class, () -> new IrcMessageBuilder("NICK").parameter(parameter));
        }
    }

    @Test
    void rejectsLineSeparatorsAndNulInTrailingText() {
        for (String text : List.of("one\rtwo", "one\ntwo", "one\0two")) {
            assertThrows(IllegalArgumentException.class, () -> new IrcMessageBuilder("QUIT").trailing(text));
        }
    }

    @Test
    void allowsOnlyOneTrailingParameterAndRequiresItToBeLast() {
        IrcMessageBuilder builder = new IrcMessageBuilder("QUIT").trailing("");

        assertThrows(IllegalStateException.class, () -> builder.trailing("again"));
        assertThrows(IllegalStateException.class, () -> builder.parameter("later"));
    }

    @Test
    void countsTrailingTextTowardsTheParameterLimit() {
        IrcMessageBuilder builder = new IrcMessageBuilder("CMD");
        for (int i = 0; i < 15; i++) {
            builder.parameter("x");
        }

        byte[] expected = ("CMD" + " x".repeat(15) + "\r\n").getBytes(StandardCharsets.UTF_8);
        assertArrayEquals(expected, builder.build());
        builder.trailing("");
        assertThrows(IllegalStateException.class, builder::build);
    }

    @Test
    void countsUtf8BytesIncludingCrlf() {
        IrcMessageBuilder fits = new IrcMessageBuilder("PRIVMSG")
                .parameter("#course")
                .trailing("я".repeat(246) + "x");
        IrcMessageBuilder tooLong = new IrcMessageBuilder("PRIVMSG")
                .parameter("#course")
                .trailing("я".repeat(247));

        assertEquals(512, fits.build().length);
        assertThrows(IllegalStateException.class, tooLong::build);
    }
}

