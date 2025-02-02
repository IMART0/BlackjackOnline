package ru.itis.balckjack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.itis.balckjack.exceptions.PlayersLimitException;
import ru.itis.balckjack.gamelogic.GameProcess;
import ru.itis.balckjack.gamelogic.model.Player;
import ru.itis.balckjack.messages.Message;
import ru.itis.balckjack.messages.MessageParser;
import ru.itis.balckjack.messages.clientQuery.BetMessage;
import ru.itis.balckjack.messages.clientQuery.EndMoveMessage;
import ru.itis.balckjack.messages.clientQuery.RequestCardMessage;
import ru.itis.balckjack.messages.serverAnswer.*;

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
                    gameProcess.getPlayer(playerID).setBet(amount);
                    logger.info("Ставка игрока {} принята: {}", playerID, amount);

                    // Проверяем, сделали ли оба игрока ставки
                    if (gameProcess.areAllBetsPlaced()) {
                        // Переход к следующему состоянию игры
                        int dealerCardID = gameProcess.getCard();
                        DealerFirstCardMessage dealerCardMessage = new DealerFirstCardMessage(dealerCardID);
                        gameProcess.addDealerCard(dealerCardID);
                        broadcast(dealerCardMessage.toMessageString());
                        for (Player player : gameProcess.players()) {
                            broadcast(
                                    new ReceivedCardMessage(player.getId(), gameProcess.getCard()).toMessageString()
                            );
                            broadcast(
                                    new ReceivedCardMessage(player.getId(), gameProcess.getCard()).toMessageString()
                            );
                        }
                    }
                } else {
                    handler.sendMessage("Ошибка: недостаточно средств или неверный ID игрока");
                }
                break;
            case REQUESTCARD:
                RequestCardMessage requestCardMessage = (RequestCardMessage) parsedMessage;
                broadcast(new ReceivedCardMessage(
                        requestCardMessage.getPlayerID(),
                        gameProcess.getCard()
                ).toMessageString());
                break;
            case ENDMOVE:
                EndMoveMessage endMoveMessage = (EndMoveMessage) parsedMessage;
                gameProcess.playerFinished(endMoveMessage.getPlayerID());
                if (gameProcess.areAllPlayersMoved()) {
                    while (gameProcess.dealerScore() <= 17) {
                        int cardID = gameProcess.getCard();
                        DealerCardMessage dealerCardMessage = new DealerCardMessage(cardID);
                        gameProcess.addDealerCard(cardID);
                        broadcast(dealerCardMessage.toMessageString());
                    }

                    for (Player player : gameProcess.players()) {
                        if (player.score() < gameProcess.dealerScore()) {
                            player.reduceBalance();
                            LooserMessage looserMessage = new LooserMessage(player.getId(), player.getBalance());
                            broadcast(looserMessage.toMessageString());
                        } else if (player.score() > gameProcess.dealerScore()) {
                            player.increaseBalance();
                            WinnerMessage winnerMessage = new WinnerMessage(player.getId(), player.getBalance());
                            broadcast(winnerMessage.toMessageString());
                        } else {
                            WinnerMessage winnerMessage = new WinnerMessage(player.getId(), player.getBalance());
                            broadcast(winnerMessage.toMessageString());
                        }
                    }
                }
        }
    }
}