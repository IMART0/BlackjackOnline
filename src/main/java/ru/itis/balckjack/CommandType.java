package ru.itis.balckjack;

public enum CommandType {
    CONNECTED(0), //Клиент подает при подключении
    CONNECTIONACCEPTED(1), //  <currentPlayerID> [otherPlayerID] //Сервер отвечает при подключении (посылается всем)
    BET(2), // <playerID> <amount> //Ставка игрока
    BETACCEPTED(3), // <betPlayerID> <amount> //Принятие ставки другого игрока (посылается всем)
    DILLERFIRSTCARD(4), // <cardID> //Первая карта дилера, визуально показывается вторая закрытая
    RECEVIEDCARD(5), // <cardID> //Сервер посылает карту на руку конкретному игроку
    GOTCARD(6), // <playerID> //Другой игрок получает карту рубашкой вверх (посылается всем)
    REQUESTCARD(7), // <playerID> //Клиент просит карту на руку
    ENDMOVE(8), // <playerID> //Закончить свой ход
    DILLERCARD(9), // <cardID> //Дилер в конце выдает себе карты, пока не станет больше 17
    WINNER(10), // <playerID> <balance> //для каждого победившего игрока просчитывается его баланс, и что он победил (посылается всем)
    NEWGAME(11); //Новая игра

    private final int id;

    CommandType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
    public static CommandType getById(int id) {
        for (CommandType value : CommandType.values()) {
            if (value.getId() == id) {
                return value;
            }
        }
        throw new IllegalArgumentException("No enum constant with id " + id);
    }
}
