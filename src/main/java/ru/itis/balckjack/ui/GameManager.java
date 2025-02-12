// GameManager.java
package ru.itis.balckjack.ui;

import javafx.scene.control.Alert;
import ru.itis.balckjack.gamelogic.model.Player;
import ru.itis.balckjack.messages.clientQuery.BetMessage;

import java.util.ArrayList;

public class GameManager {
    private Player currentPlayer;
    private Player otherPlayer;
    private final ArrayList<Integer> dealerHand = new ArrayList<>();
    private int currentBet = 0;
    private boolean currentPlayerReady = false;
    private boolean otherPlayerReady = false;
    private boolean dealerHasHiddenCard = false;

    public GameManager(Player currentPlayer, Player otherPlayer) {
        this.currentPlayer = currentPlayer;
        this.otherPlayer = otherPlayer;
    }

    public void handleBet(String betValue, ClientNetworkHandler networkHandler, UIManager uiManager) {
        if (!betValue.isEmpty()) {
            int bet = Integer.parseInt(betValue);
            if (bet >= 1 && bet <= currentPlayer.getBalance()) {
                networkHandler.sendCommand(new BetMessage(currentPlayer.getId(), bet));

                uiManager.togglePlayerCardsOpacity(currentPlayer.getId());
                uiManager.toggleBetControls(true);
                uiManager.updateUI(currentPlayer, otherPlayer);
            }   else {
                uiManager.showAlert("Ошибка", "Некорректная ставка! Введите сумму от 1 до " + currentPlayer.getBalance() + ".", Alert.AlertType.ERROR);
            }
        } else {
            uiManager.showAlert("Предупреждение", "Введите сумму ставки!", Alert.AlertType.WARNING);
        }
    }

    public void setCurrentPlayerReady(boolean ready) {
        this.currentPlayerReady = ready;
    }

    public void setOtherPlayerReady(boolean ready) {
        this.otherPlayerReady = ready;
    }

    public boolean isBothPlayersReady() {
        return currentPlayerReady && otherPlayerReady;
    }

    public void resetGame() {
        currentPlayer.getHand().clear();
        otherPlayer.getHand().clear();
        dealerHand.clear();
        currentPlayerReady = false;
        otherPlayerReady = false;
        currentPlayer.setBet(null);
        otherPlayer.setBet(null);
        dealerHasHiddenCard = false;
    }

    public int calculateDealerScore() {
        int score = 0;
        int aces = 0;
        for (int cardID : dealerHand) {
            int rank = (cardID / 4) + 2;
            if (rank >= 2 && rank <= 10) {
                score += rank;
            } else if (rank >= 11 && rank <= 13) {
                score += 10;
            } else {
                aces++;
                score += 11;
            }
        }
        while (score > 21 && aces > 0) {
            score -= 10;
            aces--;
        }
        return score;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public Player getOtherPlayer() {
        return otherPlayer;
    }

    public void addDealerCard(int cardId) {
        dealerHand.add(cardId);
    }

    public boolean hasDealerHiddenCard() {
        return dealerHasHiddenCard;
    }

    public void setDealerHiddenCard(boolean state) {
        dealerHasHiddenCard = state;
    }

    public void setPlayers(Player currentPlayer, Player otherPlayer) {
        this.currentPlayer = currentPlayer;
        this.otherPlayer = otherPlayer;
    }
}