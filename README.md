# Patience (Solitaire) Game Engine
## Project Description
This project implements the classic card game Patience (also known as Solitaire) in Java. The game engine simulates the process of playing Solitaire, handling card movement, game state updates, and detecting invalid moves. The main objective is to build sequences of cards in alternating colors and descending rank, and move them to the corresponding suit piles.

The game supports basic commands for moving cards between lanes and to the suit piles, along with tracking player moves and scores. The project demonstrates object-oriented programming principles, game logic, user input handling, and test-driven development (TDD) through unit testing.

## Features
* **Game Engine:** Implements the rules of Patience (Solitaire), including card movement, draw pile mechanics, and suit pile stacking.

* **Game Interface:** A console-based interface that allows the player to input commands and see the current state of the game.

* **Score Tracking:** Keeps track of the player’s score based on valid card movements.
* **Oscillation Detection:** Prevents oscillation by disallowing repetitive moves without any progress.
* **Unit Tests:** Thoroughly tested using JUnit for correctness of the game mechanics, with specific tests for each game feature.

## Technologies Used:
* **Java:** Core language used for implementing the game logic and engine.
* **JUnit:** Used for unit testing to ensure game rules and mechanics work as expected.
* **OOP Principles:** Encapsulation, polymorphism, and abstraction are applied throughout the game design.

## Project Structure:
* **PatienceGameEngine:** Core class that manages the game logic, including setting up the game, handling card movements, and checking for valid moves.
* **PlayingCard:** A class representing individual playing cards, with attributes such as suit, rank, and visibility.
* **Main:** The entry point of the game, which runs the console interface and allows users to interact with the game engine.
* **PatienceGameEngineTest:** Unit tests to verify that the game engine behaves as expected, covering valid/invalid moves, scoring, and card movements.

## Installation Instructions:

1. Clone the repository

* git clone https://github.com/your-username/patience-solitaire.git
* cd patience-solitaire

2. Compile the project

_Ensure you have Java and Maven installed. Compile the project with_

* mvn compile
3. Run the game

_Start the game by running the Main class_

* mvn exec:java -Dexec.mainClass="com.solitaire.Main"

4. Run tests

_Unit tests can be executed using Maven_

* mvn test

## How to Play:

The game starts with cards dealt into seven lanes, with one card visible on the top of each lane.

Use the following commands to interact with the game:

* Move cards between lanes: XYn (e.g., 562 to move two cards from lane 5 to lane 6).
* Draw a card: Enter D.
* Move cards to suit piles: PX or LnS (e.g., P1 to move from the draw pile to lane 1, or 1H to move from lane 1 to the Hearts suit pile).
* Quit the game: Enter Q.

The goal is to move all the cards to the suit piles in ascending order from Ace to King for each suit.

**Commands Overview**
* Lane to Lane Move: XYn (Move n cards from lane X to lane Y).
* Draw a Card: D.
* Move to Suit Pile: LnS (Move card from lane L to suit pile S).
* Quit: Q.

**Example Commands**

* 562 — Move 2 cards from lane 5 to lane 6.
* P1 — Move a card from the draw pile to lane 1.
* 1H — Move a card from lane 1 to the Hearts pile.
* D — Draw a card from the draw pile.

## Unit Testing:
JUnit tests have been written to verify the correctness of game rules, such as:

* Valid and invalid card moves.
* Detection of oscillation in moves.
* Proper stacking of cards in lanes and suit piles.
* Scoring based on valid moves.

**Run the tests using**

* mvn test

## Future Improvements:
Implement a graphical user interface (GUI) for a more interactive experience.
Add support for multiple versions of Solitaire (e.g., Spider, FreeCell).
Enhance the scoring system with time-based challenges or bonuses.

## Contact:
For any questions or suggestions, feel free to reach out to akash.saini@ucdconnect.ie