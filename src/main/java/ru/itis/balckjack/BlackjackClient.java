package ru.itis.balckjack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.Socket;

public class BlackjackClient implements Runnable {

    private final Logger logger = LogManager.getLogger(BlackjackClient.class);
    private PrintWriter out;
    private BufferedReader in;
    private Socket client;

    @Override
    public void run() {
        try {
            client = new Socket("localhost", 12345);
            logger.info("Connected to server");

            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));

            new Thread(this::listenForMessages).start();

        } catch (IOException e) {
            logger.error("Connection error: " + e.getMessage());
        }
    }

    public void sendMessage(String message) {
        if (out != null) {
            out.println(message);
        }
    }

    private void listenForMessages() {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                System.out.println(message);
            }
        } catch (IOException e) {
            logger.error("Error reading from server: " + e.getMessage());
        }
    }
}
