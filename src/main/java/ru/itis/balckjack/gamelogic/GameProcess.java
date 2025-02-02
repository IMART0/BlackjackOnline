package ru.itis.balckjack.gamelogic;

import ru.itis.balckjack.gamelogic.model.Deck;
import ru.itis.balckjack.gamelogic.model.Player;

import java.util.ArrayList;
import java.util.List;

public class GameProcess {
    private final ArrayList<Player> players;
    private final List<Boolean> playersFinished;
    private final List<Boolean> newGameRequest;
    private List<Integer> dealerCardsID;
    private Deck deck;

    public boolean areAllPlayersMoved() {
        for (Boolean playerFinished : playersFinished) {
            if (!playerFinished) {
                return false;
            }
        }
        return true;
    }

    public boolean allPlayersReady() {
        for (Boolean newGameRequest : newGameRequest) {
            if (!newGameRequest) {
                return false;
            }
        }
        return true;
    }

    public int dealerScore() {
        int score = 0;
        int aces = 0;
        for (int cardID : dealerCardsID) {
            int rank = (cardID / 4) + 2; // Получаем номинал карты (2-14, где 14 - туз)

            if (rank >= 2 && rank <= 10) {
                score += rank;
            } else if (rank >= 11 && rank <= 13) { // Валет, дама, король
                score += 10;
            } else { // Туз (rank == 14)
                aces++;
                score += 11;
            }
        }
        // Корректируем тузы, если перебор
        while (score > 21 && aces > 0) {
            score -= 10;
            aces--;
        }

        return score;
    }

    public void addDealerCard(int dealerCardID) {
        dealerCardsID.add(dealerCardID);
    }

    public void clearRequests() {
        for (int i = 0; i < players.size(); i++) {
            newGameRequest.set(i, false);
        }
    }

    private static final class GameProcessHolder {
        private static final GameProcess gameProcess = new GameProcess();
    }

    public static GameProcess getInstance() {
        return GameProcessHolder.gameProcess;
    }

    private GameProcess() {
        players = new ArrayList<>();
        playersFinished = new ArrayList<>();
        deck = new Deck();
        dealerCardsID = new ArrayList<>();
        newGameRequest = new ArrayList<>();
    }

    public void reset() {
        for (int i = 0; i < players.size(); i++) {
            playersFinished.set(i, false);
        }
        deck = new Deck();
        dealerCardsID = new ArrayList<>();
        for (Player player : players) {
            player.setBet(null);
            player.setHand(new ArrayList<>());
        }
    }

    public boolean isGameFinished() {
        return false;
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
        playersFinished.add(false);
        newGameRequest.add(false);
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

    public void playerFinished(int playerID) {
        playersFinished.set(playerID, true);
    }

    public void playerRequestNewGame(int playerID) {
        newGameRequest.set(playerID, true);
    }
}