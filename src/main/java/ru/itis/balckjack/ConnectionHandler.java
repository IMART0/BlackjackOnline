package ru.itis.balckjack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.itis.balckjack.messages.Message;
import ru.itis.balckjack.messages.clientQuery.BetMessage;
import ru.itis.balckjack.messages.MessageParser;

import java.io.*;
import java.net.Socket;

public class ConnectionHandler implements Runnable {
    private final Socket clientSocket;

    private final Logger logger = LogManager.getLogger(ConnectionHandler.class);

    public ConnectionHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                Message message = MessageParser.parse(inputLine);
                System.out.println(message);
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }
}
