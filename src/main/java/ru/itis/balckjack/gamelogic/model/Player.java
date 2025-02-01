package ru.itis.balckjack.gamelogic.model;


import lombok.Getter;
import lombok.Setter;

import java.net.Socket;
import java.util.ArrayList;

@Setter
@Getter
public class Player {
    private int id;

    private int bet;
    private int balance;
    private Socket socket;
    private ArrayList<Integer> hand;

    public Player(int id, int balance, Socket socket) {
        this.id = id;
        this.balance = balance;
        this.socket = socket;
    }

}
