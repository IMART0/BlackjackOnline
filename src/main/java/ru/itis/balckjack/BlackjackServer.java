package ru.itis.balckjack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.itis.balckjack.gamelogic.GameProcess;
import ru.itis.balckjack.messages.Message;
import ru.itis.balckjack.messages.MessageParser;
import ru.itis.balckjack.messages.clientQuery.BetMessage;
import ru.itis.balckjack.messages.serverAnswer.BetAcceptedMessage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BlackjackServer implements Runnable {

    private final Logger logger = LogManager.getLogger(BlackjackServer.class);
    private final ArrayList<ConnectionHandler> connections;
    public final GameProcess gameProcess;

    private ServerSocket server;

    public static void main(String[] args) {
        BlackjackServer server = new BlackjackServer();
        server.run();
    }

    public BlackjackServer() {
        connections = new ArrayList<>();
        gameProcess = new GameProcess();
    }

    @Override
    public void run() {
        try (ExecutorService executor = Executors.newCachedThreadPool()) {
            server = new ServerSocket(12345);
            logger.info("Server started on port 12345...");

            while (!gameProcess.isGameFinished()) {
                Socket client = server.accept();
                logger.info("New client connected: {}", client.getInetAddress());
                ConnectionHandler connection = new ConnectionHandler(client, this);
                connections.add(connection);
                executor.execute(connection);
            }
            stop();
        } catch (IOException e) {
            logger.error("Server error: {}", e.getMessage());
            stop();
        }
    }

    public void stop() {
        try {
            server.close();
        } catch (IOException e) {
            logger.error("Error while stopping server: {}", e.getMessage());
        }
    }

    public void broadcast(String message) {
        for (ConnectionHandler connection : connections) {
            if (connection != null)
                connection.sendMessage(message);
        }
    }

    public void handleMessage(String message, ConnectionHandler handler) {
        Message parsedMessage = MessageParser.parse(message);
        switch (parsedMessage.getType()) {
            case BET:
                BetMessage betMessage = (BetMessage) parsedMessage;
                int playerID = betMessage.getPlayerID();
                int amount = betMessage.getAmount();
                // Здесь можно добавить логику проверки ставки
                broadcast(new BetAcceptedMessage(playerID, amount).toMessageString());
                break;
            // Добавьте обработку других типов сообщений
        }
    }
}