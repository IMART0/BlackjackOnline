package ru.itis.balckjack.gamelogic;

import ru.itis.balckjack.gamelogic.model.Player;

import java.net.Socket;

public class GameProcess {
    private boolean isGameFinished = false;
    private final Player[] players;
    private States state;

    public GameProcess() {
        players = new Player[2];
        state = States.PlayersInit;
    }

    public boolean isGameFinished() {
        return isGameFinished;
    }

    public int initNewPlayer(Socket clientSocket) {
        for (int i = 0; i < 2; i++) {
            if (players[i] == null) {
                players[i] = new Player(i, 1000, clientSocket);
                return i;
            }
        }
        return -1;
    }

    public int getOtherPlayerID() {
        if (players[1] != null)
            return players[0].getId();
        else
            return -1;
    }

    public States state() {
        return state;
    }

    public boolean contains(int clientID) {
        return clientID != -1 && players[clientID] != null;
    }
}