package ru.itis.balckjack.gamelogic;

import ru.itis.balckjack.gamelogic.model.Deck;
import ru.itis.balckjack.gamelogic.model.Player;

import java.util.ArrayList;
import java.util.List;

public class GameProcess {
    private boolean isGameFinished = false;
    private ArrayList<Player> players;
    private Deck deck;
    private static GameProcess gameProcess;

    public static GameProcess getInstance() {
        if (gameProcess == null) {
            synchronized (GameProcess.class) {
                if (gameProcess == null) {
                    gameProcess = new GameProcess();
                }
            }
        }
        return gameProcess;
    }

    private GameProcess() {
        players = new ArrayList<>();
        deck = new Deck();
    }

    public boolean isGameFinished() {
        return isGameFinished;
    }

    public int playersCount() {
        return players.size();
    }

    // Метод для обработки ставки игрока
    public boolean placeBet(int playerID, int amount) {
        if (playerID < 0 || playerID >= players.size() || players.get(playerID) == null) {
            return false; // Игрок не найден
        }

        Player player = players.get(playerID);
        if (player.getBalance() >= amount) {
            player.setBalance(player.getBalance() - amount);
            player.setBet(amount);
            return true;
        }
        return false;
    }

    public boolean areAllBetsPlaced() {
        return players.get(0).getBet() != null && players.get(1).getBet() != null;
    }

    public void initNewPlayer(Player player) {
        players.add(player);
    }

    public Player getPlayer(int playerID) {
        return players.get(playerID);
    }

    public int getCard() {
        return deck.drawCard();
    }

    public List<Player> players() {
        return players;
    }
}