package ru.itis.balckjack.ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import ru.itis.balckjack.gamelogic.model.Player;
import ru.itis.balckjack.messages.Message;
import ru.itis.balckjack.messages.MessageParser;
import ru.itis.balckjack.messages.clientQuery.BetMessage;
import ru.itis.balckjack.messages.clientQuery.ConnectedMessage;
import ru.itis.balckjack.messages.serverAnswer.BetAcceptedMessage;
import ru.itis.balckjack.messages.serverAnswer.ConnectionAcceptedMessage;


public class BlackjackFxInterface extends Application {
    private ClientNetworkHandler networkHandler;
    private Stage globalStage;
    private Player player; // Добавляем поле player

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Blackjack Online");
        globalStage = primaryStage;
        primaryStage.setResizable(false);
        primaryStage.setScene(drawStartScene());
        primaryStage.show();
    }

    private Scene drawStartScene() {
        Label titleLabel = new Label("Blackjack Online");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setStyle("-fx-text-fill: white;");

        Label messageLabel = new Label("Ожидание подключения...");
        messageLabel.setFont(Font.font("Arial", 16));
        messageLabel.setStyle("-fx-text-fill: white;");

        Button connectButton = new Button("Подключиться");
        connectButton.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        connectButton.setStyle(
                "-fx-background-color: #ffcc00; " +
                        "-fx-text-fill: black; " +
                        "-fx-padding: 10px 20px; " +
                        "-fx-border-radius: 10px; " +
                        "-fx-background-radius: 10px; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.3), 10, 0, 0, 3);"
        );
        connectButton.setOnAction(event -> {
            networkHandler = new ClientNetworkHandler("localhost", 12345);
            networkHandler.setMessageListener(this::handleServerMessage);
            networkHandler.sendCommand(new ConnectedMessage());
        });

        VBox vbox = new VBox(20, titleLabel, messageLabel, connectButton);
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-background-color: #2C3E50; -fx-padding: 30px;");

        return new Scene(vbox, 400, 300);
    }

    private void handleServerMessage(String message) {
        Platform.runLater(() -> {
            Message parsedMessage = MessageParser.parse(message);
            switch (parsedMessage.getType()) {
                case CONNECTIONACCEPTED:
                    ConnectionAcceptedMessage connectionAcceptedMessage = (ConnectionAcceptedMessage) parsedMessage;
                    int playerID = connectionAcceptedMessage.getCurrentPlayerID();
                    player = new Player(playerID, 1000, null); // Инициализируем player
                    if (connectionAcceptedMessage.getOtherPlayerID() != null) {
                        drawMainScene();
                    } else {
                        drawWaitConnectionScene();
                    }
                    break;
                case BETACCEPTED:
                    BetAcceptedMessage betAcceptedMessage = (BetAcceptedMessage) parsedMessage;
                    // Обновляем интерфейс в зависимости от принятой ставки
                    break;
                // Добавьте обработку других типов сообщений
            }
        });
    }

    private void drawWaitConnectionScene() {
        Label titleLabel = new Label("Blackjack Online");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setStyle("-fx-text-fill: white;");

        Label messageLabel = new Label("Вы успешно подключены,\nожидайте второго игрока");
        messageLabel.setFont(Font.font("Arial", 16));
        messageLabel.setStyle("-fx-text-fill: white;");

        VBox vbox = new VBox(20, titleLabel, messageLabel);
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-background-color: #2C3E50; -fx-padding: 30px;");

        globalStage.setScene(new Scene(vbox, 400, 300));
    }

    private void drawMainScene() {
        // Заголовок
        Label titleLabel = new Label("Blackjack Online");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setStyle("-fx-text-fill: white;");

        // Основной контейнер
        VBox mainContainer = new VBox(20);
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setStyle("-fx-background-color: #2C3E50; -fx-padding: 30px;");

        // Контейнер для дилера
        VBox dealerContainer = new VBox(10);
        dealerContainer.setAlignment(Pos.CENTER);

        // Дилер
        Label dealerLabel = new Label("Дилер");
        dealerLabel.setFont(Font.font("Arial", 16));
        dealerLabel.setStyle("-fx-text-fill: white;");

        // Аватар дилера
        javafx.scene.shape.Circle dealerAvatar = new javafx.scene.shape.Circle(40);
        dealerAvatar.setStyle("-fx-fill: #ffcc00; -fx-stroke: white; -fx-stroke-width: 2;");

        // Контейнер для карт дилера
        HBox dealerCards = new HBox(10);
        dealerCards.setAlignment(Pos.CENTER);
        dealerCards.setStyle("-fx-padding: 10px;");

        dealerContainer.getChildren().addAll(dealerLabel, dealerAvatar, dealerCards);

        // Контейнер для игроков
        HBox playersContainer = new HBox(20);
        playersContainer.setAlignment(Pos.CENTER);

        // Игрок 1
        VBox player1Container = new VBox(10);
        player1Container.setAlignment(Pos.CENTER);

        Label player1Label = new Label("Игрок 1");
        player1Label.setFont(Font.font("Arial", 16));
        player1Label.setStyle("-fx-text-fill: white;");

        javafx.scene.shape.Circle player1Avatar = new javafx.scene.shape.Circle(40);
        player1Avatar.setStyle("-fx-fill: #ffcc00; -fx-stroke: white; -fx-stroke-width: 2;");

        // Контейнер для карт игрока 1
        HBox player1Cards = new HBox(10);
        player1Cards.setAlignment(Pos.CENTER);
        player1Cards.setStyle("-fx-padding: 10px;");

        player1Container.getChildren().addAll(player1Label, player1Avatar, player1Cards);

        // Игрок 2
        VBox player2Container = new VBox(10);
        player2Container.setAlignment(Pos.CENTER);

        Label player2Label = new Label("Игрок 2");
        player2Label.setFont(Font.font("Arial", 16));
        player2Label.setStyle("-fx-text-fill: white;");

        javafx.scene.shape.Circle player2Avatar = new javafx.scene.shape.Circle(40);
        player2Avatar.setStyle("-fx-fill: #ffcc00; -fx-stroke: white; -fx-stroke-width: 2;");

        // Контейнер для карт игрока 2
        HBox player2Cards = new HBox(10);
        player2Cards.setAlignment(Pos.CENTER);
        player2Cards.setStyle("-fx-padding: 10px;");

        player2Container.getChildren().addAll(player2Label, player2Avatar, player2Cards);

        // Добавляем игроков в общий контейнер
        playersContainer.getChildren().addAll(player1Container, player2Container);

        // Поле для ввода ставки
        Label betLabel = new Label("Введите ставку (1-" + player.getBalance() + "):");
        betLabel.setFont(Font.font("Arial", 16));
        betLabel.setStyle("-fx-text-fill: white;");

        javafx.scene.control.TextField betField = new javafx.scene.control.TextField();
        betField.setMaxWidth(150);
        betField.setPromptText("Ставка");
        betField.setStyle("-fx-background-radius: 10px; -fx-padding: 5px;");

        // Ограничение на ввод чисел от 1 до баланса игрока
        betField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                betField.setText(newValue.replaceAll("[^\\d]", ""));
            }
            try {
                int value = Integer.parseInt(newValue);
                if (value < 1 || value > player.getBalance()) {
                    betField.setText(oldValue);
                }
            } catch (NumberFormatException e) {
                // Игнорируем
            }
        });

        // Кнопка "Сделать ставку"
        Button betButton = new Button("Сделать ставку");
        betButton.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        betButton.setStyle(
                "-fx-background-color: #ffcc00; " +
                        "-fx-text-fill: black; " +
                        "-fx-padding: 10px 20px; " +
                        "-fx-border-radius: 10px; " +
                        "-fx-background-radius: 10px; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.3), 10, 0, 0, 3);"
        );
        betButton.setOnAction(event -> {
            String betValue = betField.getText();
            if (!betValue.isEmpty()) {
                int bet = Integer.parseInt(betValue);
                if (bet >= 1 && bet <= player.getBalance()) {
                    System.out.println("Ставка сделана: " + bet);
                    networkHandler.sendCommand(new BetMessage(player.getId(), bet)); // Отправляем ставку на сервер
                } else {
                    System.out.println("Некорректная ставка!");
                }
            }
        });

        // Добавляем все элементы в основной контейнер
        mainContainer.getChildren().addAll(titleLabel, dealerContainer, playersContainer, betLabel, betField, betButton);

        // Устанавливаем сцену
        globalStage.setScene(new Scene(mainContainer, 600, 600));
    }
}