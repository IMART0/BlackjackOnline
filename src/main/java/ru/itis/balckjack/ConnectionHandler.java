package ru.itis.balckjack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ConnectionHandler implements Runnable {
    private final Logger logger = LogManager.getLogger(ConnectionHandler.class);
    private final Socket client;
    private BufferedReader in;
    private PrintWriter out;

    public ConnectionHandler(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        try {
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            System.out.println(client.getPort() + " connected");
            String message;
            while ((message = in.readLine()) != null) {
                System.out.println(client.getPort() + ": " + message);
            }
        } catch (IOException e) {
            // TODO: handle
        }
    }

    public void stop() {
        try {
            in.close();
            out.close();
            client.close();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
