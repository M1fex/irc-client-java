package ua.kpi.coursework.irc.ui;

public class ConsoleOutput implements MessageOutput {
    @Override
    public void writeLine(String text) {
        System.out.println(text);
    }
}