package ru.itis.balckjack.messages.serverAnswer;

import ru.itis.balckjack.messages.Message;
import ru.itis.balckjack.messages.MessageType;

public class DealerFirstCardMessage extends Message {
    private final int cardID;

    public DealerFirstCardMessage(int cardID) {
        super(MessageType.DEALLERFIRSTCARD);
        this.cardID = cardID;
    }

    @Override
    public String toMessageString() {
        return "type:4;attributes:{cardID:%s}".formatted(cardID);
    }
}
