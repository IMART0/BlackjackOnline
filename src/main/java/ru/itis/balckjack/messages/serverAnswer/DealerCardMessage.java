package ru.itis.balckjack.messages.serverAnswer;

import ru.itis.balckjack.messages.Message;
import ru.itis.balckjack.messages.MessageType;

public class DealerCardMessage extends Message {
    private final int cardID;

    public DealerCardMessage(int cardID) {
        super(MessageType.DEALLERCARD);
        this.cardID = cardID;
    }

    @Override
    public String toMessageString() {
        return "type:9;attributes:{cardID:%s}".formatted(cardID);
    }
}
