package ua.kpi.coursework.irc.settings;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SettingsTest {
    @Test
    void acceptsNicknameWithoutWhitespace() {
        assertDoesNotThrow(() -> new Settings("127.0.0.1", 6667, "student"));
    }

    @Test
    void rejectsEmptyOrWhitespaceNickname() {
        for (String nickname : new String[]{null, "", "   ", "two words", "tab\tname"}) {
            assertThrows(IllegalArgumentException.class,
                    () -> new Settings("127.0.0.1", 6667, nickname));
        }
    }
}