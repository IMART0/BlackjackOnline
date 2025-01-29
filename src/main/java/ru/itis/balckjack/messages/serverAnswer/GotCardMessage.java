package ru.itis.balckjack.messages.serverAnswer;

import ru.itis.balckjack.messages.Message;
import ru.itis.balckjack.messages.MessageType;

public class GotCardMessage extends Message {
    private final int playerID;


    public GotCardMessage(int playerID) {
        super(MessageType.GOTCARD);
        this.playerID = playerID;
    }


    @Override
    public String toMessageString() {
        return "type:6;attributes:{playerID:%s}".formatted(playerID);
    }
}
