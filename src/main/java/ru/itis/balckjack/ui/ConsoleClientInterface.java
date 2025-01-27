package ru.itis.balckjack.ui;

import ru.itis.balckjack.BlackjackClient;

public class ConsoleClientInterface {
    public static void main(String[] args) {
        BlackjackClient client = new BlackjackClient();
        client.start();
    }
}
