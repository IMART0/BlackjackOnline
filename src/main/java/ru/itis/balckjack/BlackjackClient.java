package ru.itis.balckjack;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class BlackjackClient extends Thread {

    private final Logger logger = LogManager.getLogger(BlackjackClient.class);
    private PrintWriter out;
    private BufferedReader in;

    @Override
    public void run() {
        try {
            Socket client = new Socket("localhost", 12345);
            System.out.println("From client: connected");

            out = new PrintWriter(client.getOutputStream());

            client.close();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }
}
