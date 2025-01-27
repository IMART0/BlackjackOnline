package ru.itis.balckjack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.itis.balckjack.gamelogic.GameProcess;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BlackjackServer implements Runnable {

    private final Logger logger = LogManager.getLogger(BlackjackServer.class);
    private final ArrayList<ConnectionHandler> connections;
    private final GameProcess gameProcess;

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
            while (!gameProcess.isGameFinished()) {
                Socket client = server.accept();
                ConnectionHandler connection = new ConnectionHandler(client);
                connections.add(connection);
                executor.execute(connection);
            }
            stop();
        } catch (IOException e) {
            stop();
        }
    }

    public void stop() {
        try {
            for (ConnectionHandler connection : connections) {
                connection.stop();
            }
            server.close();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
