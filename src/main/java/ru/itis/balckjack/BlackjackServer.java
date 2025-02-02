package ru.itis.balckjack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.itis.balckjack.exceptions.PlayersLimitException;
import ru.itis.balckjack.gamelogic.GameProcess;
import ru.itis.balckjack.gamelogic.model.Player;
import ru.itis.balckjack.messages.Message;
import ru.itis.balckjack.messages.MessageParser;
import ru.itis.balckjack.messages.clientQuery.BetMessage;
import ru.itis.balckjack.messages.serverAnswer.BetAcceptedMessage;
import ru.itis.balckjack.messages.serverAnswer.ConnectionAcceptedMessage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BlackjackServer implements Runnable {

    private final Logger logger = LogManager.getLogger(BlackjackServer.class);
    private final ArrayList<ConnectionHandler> connections;
    private GameProcess gameProcess;
    
    private ServerSocket server;

    public static void main(String[] args) {

        BlackjackServer server = new BlackjackServer();
        server.run();
    }

    public BlackjackServer() {
        connections = new ArrayList<>();
    }

    @Override
    public void run() {
        gameProcess = GameProcess.getInstance();
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

    public void broadcastForOther(String message, ConnectionHandler handlerExcept) {
        for (ConnectionHandler connection : connections) {
            if (connection != null && connection != handlerExcept)
                connection.sendMessage(message);
        }
    }

    public void handleMessage(String message, ConnectionHandler handler) throws PlayersLimitException {
        Message parsedMessage = MessageParser.parse(message);
        switch (parsedMessage.getType()) {
            case CONNECTED:
                ConnectionAcceptedMessage connectionAcceptedMessage;
                if (gameProcess.playersCount() <= 2) {
                    Player player = new Player(
                            gameProcess.playersCount(),
                            1000,
                            handler.getSocket()
                    );
                    gameProcess.initNewPlayer(player);
                    handler.setPlayerId(player.getId());
                    if (gameProcess.playersCount() == 1)
                        connectionAcceptedMessage = new ConnectionAcceptedMessage(0);
                    else
                        connectionAcceptedMessage = new ConnectionAcceptedMessage(1,0);
                } else throw new PlayersLimitException();
                broadcast(connectionAcceptedMessage.toMessageString());
                logger.info("Подключен игрок");
                break;

            case BET:
                BetMessage betMessage = (BetMessage) parsedMessage;
                int playerID = betMessage.getPlayerID();
                int amount = betMessage.getAmount();

                // Обрабатываем ставку
                if (gameProcess.placeBet(playerID, amount)) {
                    // Отправляем подтверждение всем игрокам
                    broadcast(new BetAcceptedMessage(playerID, amount).toMessageString());
                    logger.info("Ставка игрока {} принята: {}", playerID, amount);

                    // Проверяем, сделали ли оба игрока ставки
                    if (gameProcess.areAllBetsPlaced()) {
                        // Переход к следующему состоянию игры
                        broadcast("Игра начинается!");
                    }
                } else {
                    // Отправляем сообщение об ошибке
                    handler.sendMessage("Ошибка: недостаточно средств или неверный ID игрока");
                }
                break;
            // Добавьте обработку других типов сообщений
        }
    }
}