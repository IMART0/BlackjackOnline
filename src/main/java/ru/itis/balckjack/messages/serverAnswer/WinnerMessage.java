package ru.itis.balckjack.messages.serverAnswer;

import ru.itis.balckjack.messages.Message;
import ru.itis.balckjack.messages.MessageType;

public class WinnerMessage extends Message {
    private final int playerID;
    private final int balance;

    public WinnerMessage(int playerID, int balance) {
        super(MessageType.WINNER);
        this.playerID = playerID;
        this.balance = balance;
    }


    @Override
    public String toMessageString() {
        return "type:10;attributes:{playerID:%s;balance:%s}".formatted(
                playerID,
                balance
        );
    }
}
