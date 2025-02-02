package ru.itis.balckjack.ui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import ru.itis.balckjack.gamelogic.model.Player;
import ru.itis.balckjack.messages.Message;
import ru.itis.balckjack.messages.MessageParser;
import ru.itis.balckjack.messages.clientQuery.BetMessage;
import ru.itis.balckjack.messages.clientQuery.ConnectedMessage;
import ru.itis.balckjack.messages.serverAnswer.BetAcceptedMessage;
import ru.itis.balckjack.messages.serverAnswer.ConnectionAcceptedMessage;

import java.io.IOException;

public class MainFXController {
    private ClientNetworkHandler networkHandler;
    private Player player;
    private Stage primaryStage;

    @FXML private Label messageLabel;
    @FXML private Button connectButton;
    @FXML private StackPane betBox;
    @FXML private TextField betField;
    @FXML private Button betButton;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    private void handleConnect() {
        networkHandler = new ClientNetworkHandler("localhost", 12345);
        networkHandler.setMessageListener(this::handleServerMessage);
        networkHandler.sendCommand(new ConnectedMessage());
    }

    @FXML
    private void handleBet() {
        String betValue = betField.getText();
        if (!betValue.isEmpty()) {
            int bet = Integer.parseInt(betValue);
            if (bet >= 1 && bet <= player.getBalance()) {
                networkHandler.sendCommand(new BetMessage(player.getId(), bet));
                showAlert("Ставка принята", "Ваша ставка в размере " + bet + " принята.", Alert.AlertType.INFORMATION);
                betButton.setDisable(true);
                betField.setDisable(true);
            } else {
                showAlert("Ошибка", "Некорректная ставка! Введите сумму от 1 до " + player.getBalance() + ".", Alert.AlertType.ERROR);
            }
        } else {
            showAlert("Предупреждение", "Введите сумму ставки!", Alert.AlertType.WARNING);
        }
    }

    private void handleServerMessage(String message) {
        Platform.runLater(() -> {
            Message parsedMessage = MessageParser.parse(message);
            switch (parsedMessage.getType()) {
                case CONNECTIONACCEPTED:
                    ConnectionAcceptedMessage cam = (ConnectionAcceptedMessage) parsedMessage;
                    player = new Player(cam.getCurrentPlayerID(), 1000, null);
                    if (cam.getOtherPlayerID() != null) {
                        loadScene("/main-scene.fxml");
                    } else {
                        loadScene("/wait-scene.fxml");
                    }
                    break;
                case BETACCEPTED:
                    BetAcceptedMessage bam = (BetAcceptedMessage) parsedMessage;
                    if (bam.getBetPlayerID() == player.getId()) {
                        betBox.setVisible(false);
                    }
                    break;
            }
        });
    }

    private void loadScene(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            MainFXController controller = loader.getController();
            controller.setPrimaryStage(primaryStage);
            controller.setNetworkHandler(networkHandler);
            controller.setPlayer(player);

            primaryStage.setScene(new Scene(root, 680, 510));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setNetworkHandler(ClientNetworkHandler networkHandler) {
        this.networkHandler = networkHandler;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}