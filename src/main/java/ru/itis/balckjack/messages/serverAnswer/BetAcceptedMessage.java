package ru.itis.balckjack.messages.serverAnswer;

import ru.itis.balckjack.messages.Message;
import ru.itis.balckjack.messages.MessageType;

public class BetAcceptedMessage extends Message {



    public BetAcceptedMessage(MessageType type) {
        super(type);
    }

    @Override
    public String toMessageString() {
        return "";
    }
}
