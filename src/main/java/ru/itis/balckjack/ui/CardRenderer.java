// CardRenderer.java
package ru.itis.balckjack.ui;

import javafx.animation.RotateTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class CardRenderer {
    public ImageView createCardImage(int cardId, boolean isHidden) {
        String imagePath = isHidden ? "/images/rect_cards/card_back.png" : convertCardIdToImagePath(cardId);
        ImageView cardImage = new ImageView(new Image(getClass().getResourceAsStream(imagePath)));
        cardImage.setFitWidth(60);
        cardImage.setPreserveRatio(true);
        animateCard(cardImage);
        return cardImage;
    }

    private String convertCardIdToImagePath(int cardId) {
        String[] suits = {"club", "diamond", "heart", "spade"};
        String suit = suits[cardId % 4];
        int valueIndex = cardId / 4;

        String value;
        if (valueIndex < 9) {
            value = String.valueOf(valueIndex + 2);
        } else {
            String[] faceCards = {"J", "Q", "K", "A"};
            value = faceCards[valueIndex - 9];
        }

        return "/images/rect_cards/" + suit + "/" + value + ".png";
    }

    private void animateCard(ImageView cardImage) {
        RotateTransition rotateTransition = new RotateTransition(Duration.millis(500), cardImage);
        rotateTransition.setByAngle(360);
        rotateTransition.setCycleCount(1);
        rotateTransition.setAutoReverse(false);
        rotateTransition.play();
    }
}