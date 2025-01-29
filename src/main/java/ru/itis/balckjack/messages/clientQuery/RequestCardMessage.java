package ru.itis.balckjack.messages.clientQuery;

import ru.itis.balckjack.messages.Message;
import ru.itis.balckjack.messages.MessageType;

public class RequestCardMessage extends Message {

    private final int playerID;

    public RequestCardMessage(int playerID) {
        super(MessageType.REQUESTCARD);
        this.playerID = playerID;
    }


    @Override
    public String toMessageString() {
        return "type:7;attributes:{playerID:%s}".formatted(playerID);
    }

}
