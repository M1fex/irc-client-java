package ua.kpi.coursework.irc.settings;

public record Settings(String host, int port, String nickname) {
    public Settings {
        if (host == null || host.isBlank()){
            throw new IllegalArgumentException("Хост не може бути порожнім");
        }
        if (port > 65535 || port < 1) {
            throw new IllegalArgumentException("Порт має бути від 1 до 65535");
        }
        if (nickname == null || nickname.isBlank() || nickname.chars().anyMatch(Character::isWhitespace)) {
            throw new IllegalArgumentException("Нік має бути непорожнім і без пробілів");
        }
    }

    public static Settings defaults() {
        return new Settings("127.0.0.1", 6667, "student");
    }
}