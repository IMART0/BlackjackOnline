package ru.itis.balckjack.ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
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

        // Создаем GridPane для основного макета
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(100); // Горизонтальное расстояние между колонками
        gridPane.setVgap(20);  // Вертикальное расстояние между строками
        gridPane.setPadding(new Insets(20)); // Отступы вокруг GridPane

        // Контейнер для дилера
        VBox dealerContainer = new VBox(10);
        dealerContainer.setAlignment(Pos.CENTER);

        Label dealerLabel = new Label("Дилер");
        dealerLabel.setFont(Font.font("Arial", 16));
        dealerLabel.setStyle("-fx-text-fill: white;");

        Circle dealerAvatar = new Circle(40);
        dealerAvatar.setStyle("-fx-fill: #ffcc00; -fx-stroke: white; -fx-stroke-width: 2;");

        // Контейнер для карт дилера
        HBox dealerCards = new HBox(10);
        dealerCards.setAlignment(Pos.CENTER);
        dealerCards.setStyle("-fx-padding: 10px;");

        dealerContainer.getChildren().addAll(dealerLabel, dealerAvatar, dealerCards);

        // Добавляем дилера в GridPane (первая строка, занимает 2 колонки)
        gridPane.add(dealerContainer, 0, 0, 2, 1);

        // Поле для ввода ставки
        Label betLabel = new Label("Введите ставку (1-" + player.getBalance() + "):");
        betLabel.setFont(Font.font("Arial", 16));
        betLabel.setStyle("-fx-text-fill: white;");

        TextField betField = new TextField();
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

                    // Показываем всплывающее окно с сообщением "Ставка принята"
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Ставка принята");
                    alert.setHeaderText(null);
                    alert.setContentText("Ваша ставка в размере " + bet + " принята.");
                    alert.showAndWait();

                    // Блокируем кнопку и поле ввода
                    betButton.setDisable(true);
                    betField.setDisable(true);
                } else {
                    // Показываем всплывающее окно с сообщением о некорректной ставке
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Ошибка");
                    alert.setHeaderText(null);
                    alert.setContentText("Некорректная ставка! Введите сумму от 1 до " + player.getBalance() + ".");
                    alert.showAndWait();
                }
            } else {
                // Показываем всплывающее окно, если поле ввода пустое
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Предупреждение");
                alert.setHeaderText(null);
                alert.setContentText("Введите сумму ставки!");
                alert.showAndWait();
            }
        });

        // Контейнер для элементов управления (ставка и кнопка)
        VBox controlsContainer = new VBox(10, betLabel, betField, betButton);
        controlsContainer.setAlignment(Pos.CENTER);

        // Добавляем элементы управления в GridPane (вторая строка, занимает 2 колонки)
        gridPane.add(controlsContainer, 0, 1, 2, 1);

        // Контейнер для игроков
        HBox playersContainer = new HBox(); // spacing не задаем
        playersContainer.setAlignment(Pos.CENTER);

        // Игрок 1
        VBox player1Container = new VBox(10);
        player1Container.setAlignment(Pos.CENTER);

        Label player1Label = new Label("Игрок 1");
        player1Label.setFont(Font.font("Arial", 16));
        player1Label.setStyle("-fx-text-fill: white;");

        javafx.scene.shape.Circle player1Avatar = new javafx.scene.shape.Circle(40);
        player1Avatar.setStyle("-fx-fill: #ffcc00; -fx-stroke: white; -fx-stroke-width: 2;");

        // Контейнер для карт первого игрока
        HBox player1Cards = new HBox(10);
        player1Cards.setAlignment(Pos.CENTER);
        player1Cards.setStyle("-fx-padding: 10px;");

        player1Container.getChildren().addAll(player1Cards, player1Avatar, player1Label);

        // Игрок 2
        VBox player2Container = new VBox(10);
        player2Container.setAlignment(Pos.CENTER);

        Label player2Label = new Label("Игрок 2");
        player2Label.setFont(Font.font("Arial", 16));
        player2Label.setStyle("-fx-text-fill: white;");

        javafx.scene.shape.Circle player2Avatar = new javafx.scene.shape.Circle(40);
        player2Avatar.setStyle("-fx-fill: #ffcc00; -fx-stroke: white; -fx-stroke-width: 2;");

        // Контейнер для карт второго игрока
        HBox player2Cards = new HBox(10);
        player2Cards.setAlignment(Pos.CENTER);
        player2Cards.setStyle("-fx-padding: 10px;");

        player2Container.getChildren().addAll(player2Cards, player2Avatar, player2Label);

        // Убираем отступы слева у первого игрока и справа у второго
        HBox.setMargin(player1Container, new Insets(0, 50, 0, 0)); // Отступ справа 50 пикселей
        HBox.setMargin(player2Container, new Insets(0, 0, 0, 50)); // Отступ слева 50 пикселей

        // Добавляем игроков в контейнер
        playersContainer.getChildren().addAll(player1Container, player2Container);

        // Убираем отступы по краям HBox
        playersContainer.setPadding(new Insets(0)); // Убираем отступы вокруг HBox

        // Добавляем игроков в GridPane (третья строка, занимает 2 колонки)
        gridPane.add(playersContainer, 0, 2, 2, 1);

        // Основной контейнер
        VBox mainContainer = new VBox(20, titleLabel, gridPane);
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setStyle("-fx-background-color: #2C3E50; -fx-padding: 30px;");

        // Устанавливаем сцену
        globalStage.setScene(new Scene(mainContainer, 800, 600)); // Увеличиваем размер окна
    }
}