package ru.itis.balckjack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.itis.balckjack.exceptions.PlayersLimitException;
import ru.itis.balckjack.gamelogic.States;
import ru.itis.balckjack.messages.Message;
import ru.itis.balckjack.messages.clientQuery.BetMessage;
import ru.itis.balckjack.messages.MessageParser;
import ru.itis.balckjack.messages.serverAnswer.ConnectionAcceptedMessage;

import java.io.*;
import java.net.Socket;

public class ConnectionHandler implements Runnable {
    private final Socket clientSocket;
    private final BlackjackServer server;
    private BufferedReader in;
    private PrintWriter out;

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
            int clientID = -1;
            while (server.gameProcess.state() == States.PlayersInit) {
                if (!server.gameProcess.contains(clientID)) {
                    if ((clientID = server.gameProcess.initNewPlayer(clientSocket)) != -1) {
                        if (server.gameProcess.getOtherPlayerID() != -1)
                            server.broadcast(new ConnectionAcceptedMessage(
                                    clientID,
                                    server.gameProcess.getOtherPlayerID()).toMessageString()
                            );
                        else
                            server.broadcast(new ConnectionAcceptedMessage(
                                    clientID).toMessageString()
                            );
                    } else throw new PlayersLimitException();
                    logger.info("Подключен игрок");
                }
            }

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
            logger.info("Отправлено клиенту: {}", message);
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
}