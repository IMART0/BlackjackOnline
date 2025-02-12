// UIManager.java
package ru.itis.balckjack.ui;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ru.itis.balckjack.gamelogic.model.Player;
import ru.itis.balckjack.messages.clientQuery.NewGameRequestMessage;
import ru.itis.balckjack.messages.clientQuery.RestartMessage;
import javafx.scene.control.*;

import java.io.IOException;

public class UIManager {
    private HBox player1Cards;
    private HBox player2Cards;
    private HBox dealerCards;
    private VBox player1BalanceBox;
    private VBox player2BalanceBox;
    private VBox actionButtonsBox;
    private StackPane betBox;
    private TextField betField;
    private Button betButton;
    private Label messageLabel;
    private Stage primaryStage;

    public UIManager(HBox player1Cards, HBox player2Cards, HBox dealerCards,
                     VBox player1BalanceBox, VBox player2BalanceBox, VBox actionButtonsBox,
                     StackPane betBox, TextField betField, Button betButton,
                     Label messageLabel, Stage primaryStage) {
        this.player1Cards = player1Cards;
        this.player2Cards = player2Cards;
        this.dealerCards = dealerCards;
        this.player1BalanceBox = player1BalanceBox;
        this.player2BalanceBox = player2BalanceBox;
        this.actionButtonsBox = actionButtonsBox;
        this.betBox = betBox;
        this.betField = betField;
        this.betButton = betButton;
        this.messageLabel = messageLabel;
        this.primaryStage = primaryStage;
    }

    public void loadScene(String fxmlPath, ClientNetworkHandler networkHandler, GameManager gameManager) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            MainFXController newController = loader.getController();

            // Обновляем ссылки на UI-элементы
            this.player1Cards = newController.getPlayer1Cards();
            this.player2Cards = newController.getPlayer2Cards();
            this.dealerCards = newController.getDealerCards();
            this.player1BalanceBox = newController.getPlayer1BalanceBox();
            this.player2BalanceBox = newController.getPlayer2BalanceBox();
            this.betBox = newController.getBetBox();
            this.betField = newController.getBetField();
            this.betButton = newController.getBetButton();
            this.actionButtonsBox = newController.getActionButtonsBox();
            this.messageLabel = newController.getMessageLabel();

            if (gameManager.getCurrentPlayer() != null) {
                togglePlayerCardsOpacity(gameManager.getCurrentPlayer().getId());
            }

            newController.setPrimaryStage(primaryStage);
            newController.setNetworkHandler(networkHandler);
            newController.setPlayers(gameManager.getCurrentPlayer(), gameManager.getOtherPlayer());

            primaryStage.setScene(new Scene(root, 800, 600));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateUI(Player currentPlayer, Player otherPlayer) {
        updatePlayerBox(player1BalanceBox, currentPlayer.getId() == 0 ? currentPlayer : otherPlayer);
        updatePlayerBox(player2BalanceBox, currentPlayer.getId() == 1 ? currentPlayer : otherPlayer);
    }

    private void updatePlayerBox(VBox balanceBox, Player player) {
        if (balanceBox == null || player == null) return;

        Platform.runLater(() -> {
            try {
                ((Label) balanceBox.getChildren().get(1)).setText(String.valueOf(player.getBalance()));
                ((Label) balanceBox.getChildren().get(3)).setText(
                        player.getBet() != null ? String.valueOf(player.getBet()) : "0"
                );
                ((Label) balanceBox.getChildren().get(5)).setText(String.valueOf(player.score()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void togglePlayerCardsOpacity(int playerId) {
        Platform.runLater(() -> {
            if (player1Cards == null || player2Cards == null) return;

            if (playerId == 0) {
                player1Cards.setOpacity(1.0);
            } else {
                player2Cards.setOpacity(1.0);
            }
        });
    }

    public void showActionButtons(boolean show) {
        Platform.runLater(() -> actionButtonsBox.setVisible(show));
    }

    public void showAlert(String title, String content, Alert.AlertType type) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }

    public void toggleActionButtons(boolean visible) {
        Platform.runLater(() -> actionButtonsBox.setVisible(visible));
    }

    public void resetGameUI() {
        Platform.runLater(() -> {
            player1Cards.getChildren().clear();
            player2Cards.getChildren().clear();
            dealerCards.getChildren().clear();
            betField.setDisable(false);
            betButton.setDisable(false);
            betBox.setVisible(true);
        });
    }

    public void addCardToPlayer(int playerId, ImageView cardImage) {
        Platform.runLater(() -> {
            HBox targetBox = playerId == 0 ? player1Cards : player2Cards;
            if (!targetBox.getChildren().isEmpty()) {
                cardImage.getStyleClass().add("card-overlap");
            }
            targetBox.getChildren().add(cardImage);
        });
    }

    public void addDealerCard(ImageView cardImage, boolean isHidden) {
        Platform.runLater(() -> {
            if (isHidden) {
                cardImage.setUserData("HIDDEN_CARD");
            }
            dealerCards.getChildren().add(cardImage);
        });
    }

    public void removeHiddenCards() {
        Platform.runLater(() ->
                dealerCards.getChildren().removeIf(node ->
                        node instanceof ImageView &&
                                "HIDDEN_CARD".equals(((ImageView) node).getUserData())
                )
        );
    }

    public void setStageCloseHandler(Stage stage, ClientNetworkHandler networkHandler) {
        stage.setOnCloseRequest(event -> {
            if (networkHandler != null) {
                try {
                    networkHandler.sendCommand(new RestartMessage());
                    networkHandler.getSocket().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Platform.exit();
            System.exit(0);
        });
    }

    public void toggleBetControls(boolean disable) {
        Platform.runLater(() -> {
            betBox.setVisible(!disable);
            betField.setDisable(disable);
            betButton.setDisable(disable);
        });
    }

    public void showGameResultDialog(boolean isWin, int playerScore, int dealerScore, ClientNetworkHandler networkHandler, Player currentPlayer) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle(isWin ? "Победа!" : "Поражение");
            alert.setHeaderText(null);
            alert.setContentText(String.format("%s%nВаши очки: %d%nОчки дилера: %d",
                    isWin ? "ТЫ ПОБЕДИЛ!!!" : "Ты проиграл, повезет в следующий раз!",
                    playerScore,
                    dealerScore
            ));

            ButtonType confirmButton = new ButtonType("Новая игра", ButtonBar.ButtonData.OK_DONE);
            alert.getButtonTypes().setAll(confirmButton);

            alert.showAndWait().ifPresent(buttonType ->
                    networkHandler.sendCommand(new NewGameRequestMessage(currentPlayer.getId()))
            );
        });
    }
}