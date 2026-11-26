package ua.kpi.coursework.irc;

import ua.kpi.coursework.irc.client.ConsoleCommandHandler;
import ua.kpi.coursework.irc.client.IrcClient;
import ua.kpi.coursework.irc.ui.ConsoleOutput;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            IrcClient client = new IrcClient();
            ConsoleCommandHandler handler = new ConsoleCommandHandler(client, new ConsoleOutput());
            while (true) {
                System.out.print("irc> ");

                if (!scanner.hasNextLine()) {
                    return;
                }

                String command = scanner.nextLine().strip();

                if (!handler.handle(command)) {
                    return;
                }
            }
        }
    }
}