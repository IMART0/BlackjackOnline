package ru.itis.balckjack.gamelogic.model;


import java.net.Socket;
import java.util.ArrayList;

public class Player {
    private int id;
    private int bet;
    private int balance;
    private Socket socket;
    private ArrayList<Integer> hand;
}
