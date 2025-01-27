package ru.itis.balckjack;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.PrintWriter;
import java.net.Socket;

public class SimpleClientFx extends Application {
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
        for (CommandType command : CommandType.values()) {
            Button button = new Button(""+command.getId());
            button.setOnAction(event -> sendCommand(command.getId()));
            vbox.getChildren().add(button);
        }

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
            e.printStackTrace();
        }
    }

    private void sendCommand(int commandId) {
        if (out != null) {
            out.println(commandId);
        }
    }
}

