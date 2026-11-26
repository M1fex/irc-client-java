package ua.kpi.coursework.irc.client;

import ua.kpi.coursework.irc.ui.MessageOutput;

public class ConsoleCommandHandler {
    private final IrcClient client;
    private final MessageOutput output;

    public ConsoleCommandHandler(IrcClient client, MessageOutput output) {
        this.client = client;
        this.output = output;
    }

    public boolean handle(String command){
        String[] commandParts = command.strip().split("\\s+", 2);
        String commandName = commandParts[0];
        String arguments = commandParts.length > 1 ? commandParts[1] : "";

        switch (commandName) {
            case "/help" -> {
                output.writeLine("/help - список команд");
                output.writeLine("/server <хост> <порт> - зміна параметрів сервера");
                output.writeLine("/nick <нік> - зміна псевдоніма");
                output.writeLine("/quit - вихід із системи");
                return true;
            }
            case "/server" -> {
                try {
                    String[] serverArguments = arguments.strip().split("\\s+");
                    if (serverArguments.length != 2) {
                        throw new IllegalArgumentException();
                    }

                    String host = serverArguments[0];
                    int port = Integer.parseInt(serverArguments[1]);
                    client.setServer(host, port);

                    output.writeLine("Налаштування сервера оновлено.");
                } catch (IllegalArgumentException e) {
                    output.writeLine("Некоректні параметри сервера. Використання: /server <хост> <порт від 1 до 65535>.");
                }
                return true;
            }
            case "/nick" -> {
                try {
                    client.setNickname(arguments);
                    output.writeLine("Нік оновлено.");
                } catch (IllegalArgumentException e) {
                    output.writeLine("Некоректний нік. Використання: /nick <нік без пробілів>.");
                }
                return true;
            }
            case "/quit" -> {
                output.writeLine("Роботу завершено.");
                return false;
            }
            default -> {
                output.writeLine("Невідома команда.");
                return true;
            }
        }
    }
}
