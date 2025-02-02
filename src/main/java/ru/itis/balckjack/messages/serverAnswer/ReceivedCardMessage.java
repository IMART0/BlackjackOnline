package ru.itis.balckjack.messages.serverAnswer;

import lombok.Getter;
import ru.itis.balckjack.messages.Message;
import ru.itis.balckjack.messages.MessageType;

@Getter
public class ReceivedCardMessage extends Message {

    private final int playerID;
    private final int cardID;

    public ReceivedCardMessage(int playerID, int cardID) {
        super(MessageType.RECEIVEDCARD);
        this.playerID = playerID;
        this.cardID = cardID;
    }


    @Override
    public String toMessageString() {
        return "type:5;attributes:{playerID:%s;cardID:%s}".formatted(playerID, cardID);
    }

}
