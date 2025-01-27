package ru.itis.balckjack;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client implements Runnable{

    private Socket client;
    private BufferedReader in;
    private PrintWriter out;

    public static void main(String[] args) {
        Client client = new Client();
        client.run();
    }

    @Override
    public void run() {
        try {
            Socket client = new Socket("localhost", 12345);
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));

            InputHandler inputHandler = new InputHandler();
            new Thread(inputHandler).start();

            String input;
            while ((input = in.readLine()) != null) {
                System.out.println(input);
            }
        } catch (IOException e) {
            shutdown();
        }
    }

    public void shutdown() {
        try {
            in.close();
            out.close();
            client.close();
        } catch (IOException e) {
            //TODO: handle
        }
    }

    class InputHandler implements Runnable {
        @Override
        public void run() {
            try {
                BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
                while (true) {
                    String message = console.readLine();
                    out.println(message);
                }
            } catch (IOException e) {
                //TODO: handle
            }
        }
    }
}
