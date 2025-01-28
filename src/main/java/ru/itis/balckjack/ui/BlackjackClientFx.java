package ru.itis.balckjack.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.itis.balckjack.messages.clientQuery.BetMessage;

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

        // Создаем кнопки для каждой команды
        BetMessage betMessage = new BetMessage(0, 100);
        Button button = new Button("" + betMessage.getType().getId());
        button.setOnAction(event -> {
                    sendCommand(betMessage.toMessageString());
                    System.out.println(betMessage.toMessageString());
                }
        );
        vbox.getChildren().add(button);

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
}

