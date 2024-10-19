package com.solitaire;

import java.util.Scanner;

// Main class for the Patience (Solitaire) game
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        PatienceGameEngine game = new PatienceGameEngine();

        while (true) {
            game.displayGameState();
            System.out.print("Enter command: ");
            String userCommand = scanner.nextLine().trim();
            game.handleUserCommand(userCommand);
        }
    }
}