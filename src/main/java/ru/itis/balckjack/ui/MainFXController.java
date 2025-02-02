package ru.itis.balckjack.ui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import ru.itis.balckjack.gamelogic.model.Player;
import ru.itis.balckjack.messages.Message;
import ru.itis.balckjack.messages.MessageParser;
import ru.itis.balckjack.messages.clientQuery.BetMessage;
import ru.itis.balckjack.messages.clientQuery.ConnectedMessage;
import ru.itis.balckjack.messages.serverAnswer.*;

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

    @FXML private HBox player1Cards;
    @FXML private HBox player2Cards;
    @FXML private HBox dealerCards;
    private boolean dealerHasHiddenCard = false;

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
                    if (player == null) {
                        player = new Player(cam.getCurrentPlayerID(), 1000, null);
                    }
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
                case RECEIVEDCARD:
                    ReceivedCardMessage rcm = (ReceivedCardMessage) parsedMessage;
                    handleReceivedCard(rcm.getPlayerID(), rcm.getCardID());
                    break;
                case DEALERFIRSTCARD:
                    DealerFirstCardMessage dfcm = (DealerFirstCardMessage) parsedMessage;
                    handleDealerFirstCard(dfcm.getCardID());
                    break;

                case DEALERCARD:
                    DealerCardMessage dcm = (DealerCardMessage) parsedMessage;
                    handleDealerCard(dcm.getCardID());
                    break;
            }
        });
    }

    private void handleDealerFirstCard(int cardId) {
        // Добавляем открытую карту
        addDealerCard(cardId);

        // Добавляем рубашкой вверх
        ImageView hiddenCard = new ImageView(new Image(getClass().getResourceAsStream("/images/rect_cards/card_back.png")));
        hiddenCard.setFitWidth(80);
        hiddenCard.setPreserveRatio(true);
        dealerCards.getChildren().add(hiddenCard);
        dealerHasHiddenCard = true;
    }

    private void handleDealerCard(int cardId) {
        // Удаляем все скрытые карты
        if(dealerHasHiddenCard) {
            dealerCards.getChildren().removeIf(node ->
                    ((ImageView) node).getImage().getUrl().contains("card_back.png"));
            dealerHasHiddenCard = false;
        }

        // Добавляем новую открытую карту
        addDealerCard(cardId);
    }

    private void addDealerCard(int cardId) {
        String imagePath = convertCardIdToImagePath(cardId);
        ImageView cardImage = new ImageView(new Image(getClass().getResourceAsStream(imagePath)));
        cardImage.setFitWidth(80);
        cardImage.setPreserveRatio(true);
        dealerCards.getChildren().add(cardImage);
    }

    private void handleReceivedCard(int playerId, int cardId) {
        // Добавляем карту игроку в модель
        if (player.getId() == playerId) {
            player.getHand().add(cardId);
        }

        // Получаем путь к изображению карты
        String imagePath = convertCardIdToImagePath(cardId);

        // Создаем элемент для отображения карты
        ImageView cardImage = new ImageView(new Image(getClass().getResourceAsStream(imagePath)));
        cardImage.setFitWidth(80);
        cardImage.setPreserveRatio(true);

        // Добавляем в соответствующий HBox
        if (playerId == 0) {
            player1Cards.getChildren().add(cardImage);
        } else if (playerId == 1) {
            player2Cards.getChildren().add(cardImage);
        } else if (playerId == -1) { // ID дилера
            dealerCards.getChildren().add(cardImage);
        }
    }

    private String convertCardIdToImagePath(int cardId) {
        String[] suits = {"club", "diamond", "heart", "spade"};
        String suit = suits[cardId % 4];
        int valueIndex = cardId / 4;

        String value;
        if (valueIndex < 9) {
            value = String.valueOf(valueIndex + 2);
        } else {
            String[] faceCards = {"J", "Q", "K", "A"};
            value = faceCards[valueIndex - 9];
        }

        return "/images/rect_cards/" + suit + "/" + value + ".png";
    }

    private void loadScene(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            MainFXController controller = loader.getController();
            controller.setPrimaryStage(primaryStage);
            controller.setNetworkHandler(networkHandler);
            controller.setPlayer(player);
            networkHandler.setMessageListener(controller::handleServerMessage);

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