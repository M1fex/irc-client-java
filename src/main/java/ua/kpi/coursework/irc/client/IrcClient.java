package ua.kpi.coursework.irc.client;

import ua.kpi.coursework.irc.settings.Settings;

public class IrcClient {
    private Settings settings = Settings.defaults();

    public void setServer(String host, int port) {
        settings = new Settings(host, port, settings.nickname());
    }

    public void setNickname(String nickname) {
        settings = new Settings(settings.host(), settings.port(), nickname);
    }
}