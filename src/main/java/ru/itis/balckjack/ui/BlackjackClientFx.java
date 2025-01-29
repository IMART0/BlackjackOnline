package ru.itis.balckjack.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.itis.balckjack.messages.Message;
import ru.itis.balckjack.messages.clientQuery.BetMessage;
import ru.itis.balckjack.messages.clientQuery.ConnectedMessage;
import ru.itis.balckjack.messages.clientQuery.EndMoveMessage;
import ru.itis.balckjack.messages.clientQuery.RequestCardMessage;
import ru.itis.balckjack.messages.serverAnswer.*;

import java.io.PrintWriter;
import java.net.Socket;

public class BlackjackClientFx extends Application {
    private final Logger logger = LogManager.getLogger(BlackjackClientFx.class);

    private PrintWriter out;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Командный клиент");

        VBox vbox = new VBox(10);
        vbox.setStyle("-fx-padding: 10;");

        // ТЕСТ ВСЕХ ТИПОВ СООБЩЕНИЙ

        addMessageButton(vbox, new BetAcceptedMessage(12,12));
        addMessageButton(vbox, new ConnectionAcceptedMessage(12,1));
        addMessageButton(vbox, new DealerCardMessage(12));
        addMessageButton(vbox, new DealerFirstCardMessage(12));
        addMessageButton(vbox, new GotCardMessage(12));
        addMessageButton(vbox, new NewGameMessage());
        addMessageButton(vbox, new ReceivedCardMessage(12));
        addMessageButton(vbox, new WinnerMessage(12,1));
        addMessageButton(vbox, new RequestCardMessage(12));
        addMessageButton(vbox, new EndMoveMessage(12));
        addMessageButton(vbox, new ConnectedMessage());
        addMessageButton(vbox, new BetMessage(0, 100)); // Пример с параметрами

        Scene scene = new Scene(vbox, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Подключаемся к серверу
        connectToServer();
    }

    private void connectToServer() {
        try {
            Socket socket = new Socket("localhost", 12345);
            out = new PrintWriter(socket.getOutputStream(), true);
            System.out.println("Подключено к серверу.");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void sendCommand(String command) {
        if (out != null) {
            out.println(command);
        }
    }

    private void addMessageButton(VBox vbox, Message message) {
        Button button = new Button(message.getClass().getSimpleName());
        button.setOnAction(event -> {
            sendCommand(message.toMessageString());
            System.out.println(message.toMessageString());
        });
        vbox.getChildren().add(button);
    }
}

