package ru.itis.balckjack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.Socket;

public class ConnectionHandler implements Runnable {
    private static final Logger logger = LogManager.getLogger(ConnectionHandler.class);

    private final Socket socket;
    private final BlackjackServer server;
    private PrintWriter out;
    private BufferedReader in;

    public ConnectionHandler(Socket socket, BlackjackServer server) {
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            String message;
            while ((message = in.readLine()) != null) {
                System.out.println(message);
            }
        } catch (IOException e) {
            logger.error("Connection error: {}", e.getMessage());
        } finally {
            stop();
        }
    }

    public void sendMessage(String message) {
        if (out != null) {
            out.println(message);
        }
    }

    public void stop() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            logger.error("Error while closing connection: {}", e.getMessage());
        }
        server.removeConnection(this);
    }
}
