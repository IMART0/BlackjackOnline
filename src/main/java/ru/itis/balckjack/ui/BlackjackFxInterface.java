package ru.itis.balckjack.ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ru.itis.balckjack.messages.Message;
import ru.itis.balckjack.messages.clientQuery.*;
import ru.itis.balckjack.messages.serverAnswer.*;

public class BlackjackFxInterface extends Application {
    private ClientNetworkHandler networkHandler;
    private Label messageLabel;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Командный клиент");

        // Инициализация сетевого обработчика
        networkHandler = new ClientNetworkHandler("localhost", 12345);
        networkHandler.setMessageListener(this::handleServerMessage);

        VBox vbox = new VBox(10);
        vbox.setStyle("-fx-padding: 10;");

        messageLabel = new Label("Ожидание сообщений...");
        vbox.getChildren().add(messageLabel);

        addMessageButton(vbox, new BetAcceptedMessage(12, 12));
        addMessageButton(vbox, new ConnectionAcceptedMessage(12, 1));
        addMessageButton(vbox, new DealerCardMessage(12));
        addMessageButton(vbox, new DealerFirstCardMessage(12));
        addMessageButton(vbox, new GotCardMessage(12));
        addMessageButton(vbox, new NewGameMessage());
        addMessageButton(vbox, new ReceivedCardMessage(12));
        addMessageButton(vbox, new WinnerMessage(12, 1));
        addMessageButton(vbox, new RequestCardMessage(12));
        addMessageButton(vbox, new EndMoveMessage(12));
        addMessageButton(vbox, new ConnectedMessage());
        addMessageButton(vbox, new BetMessage(0, 100));

        Scene scene = new Scene(vbox, 300, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void addMessageButton(VBox vbox, Message message) {
        Button button = new Button(message.getClass().getSimpleName());
        button.setOnAction(event -> networkHandler.sendCommand(message));
        vbox.getChildren().add(button);
    }

    private void handleServerMessage(String message) {
        Platform.runLater(() -> messageLabel.setText(message));
    }
}
