// MainFXController.java
package ru.itis.balckjack.ui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Getter;
import ru.itis.balckjack.gamelogic.model.Player;
import ru.itis.balckjack.messages.Message;
import ru.itis.balckjack.messages.MessageParser;
import ru.itis.balckjack.messages.clientQuery.*;

public class MainFXController {
    private ClientNetworkHandler networkHandler;
    private GameManager gameManager;
    private UIManager uiManager;
    private CardRenderer cardRenderer;

    @Getter
    @FXML private Label messageLabel;
    @Getter
    @FXML private StackPane betBox;
    @Getter
    @FXML private TextField betField;
    @Getter
    @FXML private Button betButton;
    @Getter
    @FXML private HBox player1Cards;
    @Getter
    @FXML private HBox player2Cards;
    @Getter
    @FXML private HBox dealerCards;
    @Getter
    @FXML private VBox player1BalanceBox;
    @Getter
    @FXML private VBox player2BalanceBox;
    @Getter
    @FXML private VBox actionButtonsBox;
    private Stage primaryStage;

    public void initialize() {
        this.gameManager = new GameManager(null, null);
        this.cardRenderer = new CardRenderer();
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage; // Initialize primaryStage
        this.uiManager = new UIManager(
                player1Cards, player2Cards, dealerCards,
                player1BalanceBox, player2BalanceBox, actionButtonsBox,
                betBox, betField, betButton, messageLabel,
                primaryStage
        );
        uiManager.setStageCloseHandler(primaryStage, networkHandler);
    }

    @FXML
    private void handleConnect() {
        networkHandler = new ClientNetworkHandler("localhost", 12345);
        networkHandler.setMessageListener(this::handleServerMessage);
        networkHandler.sendCommand(new ConnectedMessage());
    }

    @FXML
    private void handleBet() {
        gameManager.handleBet(betField.getText(), networkHandler, uiManager);
    }

    @FXML
    private void handleRequestCard() {
        networkHandler.sendCommand(new RequestCardMessage(gameManager.getCurrentPlayer().getId()));
    }

    @FXML
    private void handleEndMove() {
        networkHandler.sendCommand(new EndMoveMessage(gameManager.getCurrentPlayer().getId()));
        uiManager.toggleActionButtons(false);
    }

    private void handleServerMessage(String message) {
        Platform.runLater(() -> {
            if ("SERVER_SHUTDOWN".equals(message)) {
                new MessageHandler().handleServerShutdown(uiManager);
                return;
            }

            Message parsedMessage = MessageParser.parse(message);
            new MessageHandler().handleMessage(
                    parsedMessage,
                    gameManager,
                    uiManager,
                    cardRenderer,
                    networkHandler
            );
        });
    }

    public void setNetworkHandler(ClientNetworkHandler networkHandler) {
        this.networkHandler = networkHandler;
        if (this.uiManager != null) {
            this.uiManager.setStageCloseHandler(primaryStage, networkHandler);
        }
    }

    public void setPlayers(Player currentPlayer, Player otherPlayer) {
        this.gameManager.setPlayers(currentPlayer, otherPlayer);
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public UIManager getUiManager() {
        return uiManager;
    }
}