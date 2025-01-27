package ru.itis.balckjack.ui;

import ru.itis.balckjack.BlackjackClient;

import java.util.Scanner;

public class ConsoleTestClientInterface {
    public static void main(String[] args) {
        BlackjackClient client = new BlackjackClient();
        new Thread(client).start();

        Scanner scanner = new Scanner(System.in);
        while (true) {
            String message = scanner.nextLine();
            client.sendMessage(message);
        }
    }
}
