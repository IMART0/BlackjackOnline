// MessageHandler.java
package ru.itis.balckjack.ui;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import ru.itis.balckjack.gamelogic.model.Player;
import ru.itis.balckjack.messages.Message;
import ru.itis.balckjack.messages.clientQuery.*;
import ru.itis.balckjack.messages.serverAnswer.*;

import java.util.ArrayList;

public class MessageHandler {
    public void handleMessage(Message message, GameManager gameManager, UIManager uiManager,
                              CardRenderer cardRenderer, ClientNetworkHandler networkHandler) {
        switch (message.getType()) {
            case CONNECTIONACCEPTED:
                handleConnectionAccepted((ConnectionAcceptedMessage) message, gameManager, uiManager, networkHandler);
                break;
            case BETACCEPTED:
                handleBetAccepted((BetAcceptedMessage) message, gameManager, uiManager);
                break;
            case RECEIVEDCARD:
                handleReceivedCard((ReceivedCardMessage) message, gameManager, uiManager, cardRenderer, networkHandler);
                break;
            case DEALERFIRSTCARD:
                handleDealerFirstCard((DealerFirstCardMessage) message, gameManager, uiManager, cardRenderer);
                break;
            case DEALERCARD:
                handleDealerCard((DealerCardMessage) message, gameManager, uiManager, cardRenderer);
                break;
            case WINNER:
                handleWinner((WinnerMessage) message, gameManager, uiManager, networkHandler);
                break;
            case LOOSER:
                handleLooser((LooserMessage) message, gameManager, uiManager, networkHandler);
                break;
            case NEWGAME:
                handleNewGame(gameManager, uiManager);
                break;
        }
    }

    public void handleServerShutdown(UIManager uiManager) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Игрок отключился");
            alert.setHeaderText(null);
            alert.setContentText("Другой игрок покинул игру. Игра будет завершена.");

            alert.showAndWait().ifPresent(response -> {
                Platform.exit();
                System.exit(0);
            });
        });
    }

    private void handleConnectionAccepted(ConnectionAcceptedMessage message, GameManager gameManager,
                                          UIManager uiManager, ClientNetworkHandler networkHandler) {
        Platform.runLater(() -> {
            if (gameManager.getCurrentPlayer() == null) {
                Player current = new Player(message.getCurrentPlayerID(), 1000, null);
                current.setHand(new ArrayList<>());
                Player other = new Player(message.getOtherPlayerID() != null ? message.getOtherPlayerID()
                        : (message.getCurrentPlayerID() == 0 ? 1 : 0), 1000, null);
                other.setHand(new ArrayList<>());
                gameManager.setPlayers(current, other);
            }

            // Принудительно обновляем ссылку на текущего игрока
            uiManager.loadScene(
                    message.getOtherPlayerID() != null ? "/main-scene.fxml" : "/wait-scene.fxml",
                    networkHandler,
                    gameManager
            );
        });
    }

    private void handleBetAccepted(BetAcceptedMessage message, GameManager gameManager, UIManager uiManager) {
        Player target = message.getBetPlayerID() == gameManager.getCurrentPlayer().getId() ?
                gameManager.getCurrentPlayer() : gameManager.getOtherPlayer();

        target.setBet(message.getAmount());
        target.setBalance(target.getBalance() - message.getAmount());

        // Обновляем статус готовности
        if (target == gameManager.getCurrentPlayer()) {
            gameManager.setCurrentPlayerReady(true);
        } else {
            gameManager.setOtherPlayerReady(true);
        }

        // Проверяем готовность обоих игроков
        if (gameManager.isBothPlayersReady()) {
            uiManager.showActionButtons(true);
        }

        uiManager.updateUI(gameManager.getCurrentPlayer(), gameManager.getOtherPlayer());
    }

    private void handleReceivedCard(ReceivedCardMessage message, GameManager gameManager,
                                    UIManager uiManager, CardRenderer cardRenderer, ClientNetworkHandler networkHandler) {
        Player target = message.getPlayerID() == gameManager.getCurrentPlayer().getId() ?
                gameManager.getCurrentPlayer() : gameManager.getOtherPlayer();
        target.addCard(message.getCardID());
        uiManager.addCardToPlayer(message.getPlayerID(), cardRenderer.createCardImage(message.getCardID(), false));
        uiManager.updateUI(gameManager.getCurrentPlayer(), gameManager.getOtherPlayer());

        if (target == gameManager.getCurrentPlayer() && target.score() > 21) {
            uiManager.showAlert("Перебор!", "Сумма очков: " + target.score(), Alert.AlertType.WARNING);
            networkHandler.sendCommand(new EndMoveMessage(target.getId()));
            uiManager.toggleActionButtons(false);
        }
    }

    private void handleDealerFirstCard(DealerFirstCardMessage message, GameManager gameManager,
                                       UIManager uiManager, CardRenderer cardRenderer) {
        gameManager.addDealerCard(message.getCardID());
        uiManager.addDealerCard(cardRenderer.createCardImage(message.getCardID(), false), false);
        uiManager.addDealerCard(cardRenderer.createCardImage(0, true), true);
        gameManager.setDealerHiddenCard(true);
    }

    private void handleDealerCard(DealerCardMessage message, GameManager gameManager,
                                  UIManager uiManager, CardRenderer cardRenderer) {
        if (gameManager.hasDealerHiddenCard()) {
            uiManager.removeHiddenCards();
            gameManager.setDealerHiddenCard(false);
        }
        gameManager.addDealerCard(message.getCardID());
        uiManager.addDealerCard(cardRenderer.createCardImage(message.getCardID(), false), false);
    }

    private void handleWinner(WinnerMessage message, GameManager gameManager, UIManager uiManager, ClientNetworkHandler networkHandler) {
        if (message.getPlayerID() == gameManager.getCurrentPlayer().getId()) {
            gameManager.getCurrentPlayer().setBalance(message.getBalance());
            uiManager.showGameResultDialog(true, gameManager.getCurrentPlayer().score(),
                    gameManager.calculateDealerScore(), networkHandler, gameManager.getCurrentPlayer());
        } else {
            gameManager.getOtherPlayer().setBalance(message.getBalance());
        }
        uiManager.updateUI(gameManager.getCurrentPlayer(), gameManager.getOtherPlayer());
    }

    private void handleLooser(LooserMessage message, GameManager gameManager, UIManager uiManager, ClientNetworkHandler networkHandler) {
        if (message.getPlayerID() == gameManager.getCurrentPlayer().getId()) {
            gameManager.getCurrentPlayer().setBalance(message.getBalance());
            uiManager.showGameResultDialog(false, gameManager.getCurrentPlayer().score(),
                    gameManager.calculateDealerScore(), networkHandler, gameManager.getCurrentPlayer());
        } else {
            gameManager.getOtherPlayer().setBalance(message.getBalance());
        }
        uiManager.updateUI(gameManager.getCurrentPlayer(), gameManager.getOtherPlayer());
    }

    private void handleNewGame(GameManager gameManager, UIManager uiManager) {
        gameManager.resetGame();
        uiManager.resetGameUI();
        uiManager.updateUI(gameManager.getCurrentPlayer(), gameManager.getOtherPlayer());
    }
}