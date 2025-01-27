package ru.itis.balckjack;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class BlackjackClient extends Thread {

    private final Logger logger = LogManager.getLogger(BlackjackClient.class);

    @Override
    public void run() {
        try {
            Socket client = new Socket("localhost", 12345);
            System.out.println("From client: connected");

            OutputStream outputStream = client.getOutputStream();

            outputStream.write("Hello!\n".getBytes(StandardCharsets.UTF_8));

            client.close();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
