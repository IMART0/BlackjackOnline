package ru.itis.balckjack.messages;

import lombok.Getter;

@Getter
public enum MessageType {
    CONNECTED(0), //Клиент подает при подключении
    CONNECTIONACCEPTED(1), //  <currentPlayerID> [otherPlayerID] //Сервер отвечает при подключении (посылается всем)
    BET(2), // <playerID> <amount> //Ставка игрока
    BETACCEPTED(3), // <betPlayerID> <amount> //Принятие ставки другого игрока (посылается всем)
    DEALLERFIRSTCARD(4), // <cardID> //Первая карта дилера, визуально показывается вторая закрытая
    RECEIVEDCARD(5), // <cardID> //Сервер посылает карту на руку конкретному игроку
    GOTCARD(6), // <playerID> //Другой игрок получает карту рубашкой вверх (посылается всем)
    REQUESTCARD(7), // <playerID> //Клиент просит карту на руку
    ENDMOVE(8), // <playerID> //Закончить свой ход
    DEALLERCARD(9), // <cardID> //Дилер в конце выдает себе карты, пока не станет больше 17
    WINNER(10), // <playerID> <balance> //для каждого победившего игрока просчитывается его баланс, и что он победил (посылается всем)
    NEWGAME(11); //Новая игра

    private final int id;

    MessageType(int id) {
        this.id = id;
    }

    public static MessageType fromId(int id) {
        for (MessageType type : values()) {
            if (type.id == id) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown MessageType ID: " + id);
    }
}
