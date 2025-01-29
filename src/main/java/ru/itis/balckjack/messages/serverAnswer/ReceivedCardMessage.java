package ru.itis.balckjack.messages.serverAnswer;

import ru.itis.balckjack.messages.Message;
import ru.itis.balckjack.messages.MessageType;

public class ReceivedCardMessage extends Message {

    private final int cardID;

    public ReceivedCardMessage(int cardID) {
        super(MessageType.RECEIVEDCARD);
        this.cardID = cardID;
    }


    @Override
    public String toMessageString() {
        return "type:5;attributes:{cardID:%s}".formatted(cardID);
    }

}
