package ru.itis.balckjack;

import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.itis.balckjack.gamelogic.model.Player;

import java.io.*;
import java.net.Socket;

public class ConnectionHandler implements Runnable {
    private final Socket clientSocket;
    private final BlackjackServer server;
    private BufferedReader in;
    private PrintWriter out;
    @Setter
    private int playerId;

    private final Logger logger = LogManager.getLogger(ConnectionHandler.class);

    public ConnectionHandler(Socket client, BlackjackServer blackjackServer) {
        this.clientSocket = client;
        server = blackjackServer;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true);
            // Этап подключения

            String message;
            while ((message = in.readLine()) != null) {
                logger.info("Получено сообщение от клиента: {}", message);
                server.handleMessage(message, this);
            }
        } catch (Exception e) {
            logger.error("Ошибка связи с клиентом: {}", e.getMessage(), e);
        } finally {
            try {
                clientSocket.close();
            } catch (Exception e) {
                logger.error("Ошибка закрытия соединения: {}", e.getMessage(), e);
            }
        }
    }

    public void sendMessage(String message) {
        if (out != null) {
            out.println(message);
            System.out.printf("Отправлено клиенту id=%s: %s%n", playerId, message);
        }
    }

    public void close() {
        try {
            clientSocket.close();
            in.close();
            out.close();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public Socket getSocket() {
        return clientSocket;
    }
}