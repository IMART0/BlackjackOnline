package ru.itis.balckjack.messages.serverAnswer;

import lombok.Getter;
import ru.itis.balckjack.messages.Message;
import ru.itis.balckjack.messages.MessageType;

@Getter
public class ConnectionAcceptedMessage extends Message {
    private final int currentPlayerID;
    private Integer otherPlayerID = null;

    public ConnectionAcceptedMessage(MessageType type, int currentPlayerID, int otherPlayerID) {
        super(type);
        this.currentPlayerID = currentPlayerID;
        this.otherPlayerID = otherPlayerID;
    }

    public ConnectionAcceptedMessage(MessageType type, int currentPlayerID) {
        super(type);
        this.currentPlayerID = currentPlayerID;
    }

    @Override
    public String toMessageString() {
        return "type:" + getType().getId() + ";attributes:{currentPlayerID:" + currentPlayerID + ";" +
                "otherPlayerID:" + otherPlayerID + "}";
    }
}
