package ru.itis.balckjack;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
public class SimpleClient {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 12345);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            System.out.println("Подключено к серверу.");

            while (true) {
                System.out.print("Введите ID команды (или -1 для выхода): ");
                out.println(1);
                out.println(1);
                out.println(1);
                out.println(1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
