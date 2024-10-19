package com.solitaire;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

public class PatienceGameEngineTest {

    // Create an instance of the game engine to test
    PatienceGameEngine game = new PatienceGameEngine();


    //UNIT TESTS


    @Test
    public void testValidSequence() {
        PlayingCard card1 = new PlayingCard("H", "5", false);  // 5 of Hearts
        PlayingCard card2 = new PlayingCard("C", "6", false);  // 6 of Clubs
        assertTrue(game.isValidSequence(card1, card2), "5H should be valid to place on 6C (alternate colors, one rank lower)");

        PlayingCard card3 = new PlayingCard("H", "7", false);  // 7 of Hearts
        assertFalse(game.isValidSequence(card1, card3), "5H cannot be placed on 7H (same color, wrong rank)");
    }

    @Test
    public void testRankOneLess() {
        assertTrue(game.isRankOneLess("5", "6"), "5 should be one less than 6");
        assertFalse(game.isRankOneLess("5", "7"), "5 should not be one less than 7");
    }

    @Test
    public void testCanPlaceInLane() {
        PlayingCard king = new PlayingCard("S", "K", false);  // King of Spades
        assertTrue(game.canPlaceInLane(king, List.of()), "King should be allowed in an empty lane");

        PlayingCard queen = new PlayingCard("D", "Q", false);  // Queen of Diamonds
        assertFalse(game.canPlaceInLane(queen, List.of()), "Queen should not be allowed in an empty lane");
    }



    // FUNCTIONALITY TESTS



    @Test
    public void testMoveKingToEmptyLane() {
        game.setupGame();  // Ensure the game is set up

        // Clear lane 1 to make it empty
        game.getLane(1).clear();

        // Set up a King of Spades in lane 2
        PlayingCard kingSpades = new PlayingCard("S", "K", false);
        game.getLane(2).add(kingSpades);

        // Move the King to the empty lane 1
        game.moveBetweenLanes("2", "1", 1);

        // Ensure lane 1 now has the King of Spades
        assertEquals(1, game.getLane(1).size(), "Lane 1 should have 1 card (King of Spades)");
        assertEquals(kingSpades, game.getLane(1).get(0), "The King of Spades should be in lane 1");
    }

    // Test for moving a card to a suit pile
    @Test
    public void testMoveAceToSuitPile() {
        game.setupGame();  // Ensure the game is set up

        // Set up an Ace of Hearts in lane 1
        PlayingCard aceOfHearts = new PlayingCard("H", "A", false);
        game.getLane(1).add(aceOfHearts);

        // Move the Ace of Hearts to the Hearts suit pile
        game.moveToSuitPile("1", "H");

        // Ensure the suit pile has the Ace of Hearts
        assertEquals(1, game.getSuitPileSize("H"), "The Hearts suit pile should contain the Ace of Hearts");
    }


    // Test for handling an invalid move command
    @Test
    public void testHandleInvalidCommand() {
        assertDoesNotThrow(() -> {
            game.handleUserCommand("INVALID");
        }, "Invalid commands should not throw exceptions, but provide feedback");
    }

    @Test
    public void testCardVisibilityAfterMove() {
        game.setupGame();  // Ensure the game is set up

        // Set up two cards in lane 1, where only the top one is visible
        PlayingCard kingSpades = new PlayingCard("S", "K", false);  // King of Spades
        PlayingCard queenHearts = new PlayingCard("H", "Q", true);  // Queen of Hearts (hidden)
        game.getLane(1).add(queenHearts);
        game.getLane(1).add(kingSpades);

        // Ensure lane 2 is empty so the King can be moved there
        game.getLane(2).clear();

        // Move the King from lane 1 to lane 2 (this is now a valid move because lane 2 is empty)
        game.moveBetweenLanes("1", "2", 1);

        // Ensure the Queen of Hearts is now visible
        assertFalse(game.getLane(1).get(0).isHidden(), "Queen of Hearts should be visible after the King is moved");
    }

    @Test
    public void testDrawUntilEmpty() {
        game.setupGame();  // Ensure the game is set up

        // Draw all cards from the draw pile
        int initialDrawPileSize = game.getDrawPileSize();
        for (int i = 0; i < initialDrawPileSize; i++) {
            game.drawCard();
        }

        // Check that the draw pile is now empty
        assertEquals(0, game.getDrawPileSize(), "Draw pile should be empty after drawing all cards");
    }



    // EDGE CASES

/*
    @Test
    public void testMoveFromEmptyLane() {
        // Make sure lane 1 is empty before the test
        game.getLane(1).clear();  // Clear lane 1 to ensure it's empty

        // Now try to move a card from empty lane 1 to lane 2
        assertThrows(IllegalStateException.class, () -> {
            game.moveBetweenLanes("1", "2", 1);
        }, "Moving from an empty lane should throw an exception");
    }
*/
    /*
    // Test case for invalid move to an empty lane (non-Kings should not be allowed)
    @Test
    public void testInvalidMoveToEmptyLane() {
        game.setupGame();  // Ensure the game is set up

        // Clear lane 1 to make it empty
        game.getLane(1).clear();

        PlayingCard queen = new PlayingCard("D", "Q", false);  // Queen of Diamonds
        game.getLane(2).add(queen);  // Add the Queen to lane 2

        // Try to move the Queen from lane 2 to lane 1 (empty)
        assertThrows(IllegalArgumentException.class, () -> {
            game.moveBetweenLanes("2", "1", 1);
        }, "Only Kings should be allowed in an empty lane");
    }*/

    @Test
    public void testInvalidInput() {
        assertDoesNotThrow(() -> {
            game.handleUserCommand("XYZ");
        }, "Invalid commands should not throw exceptions");
    }

    @Test
    public void testInvalidMoveToEmptySuitPile() {
        game.setupGame();  // Ensure the game is set up

        // Set up a 5 of Diamonds in lane 1
        PlayingCard fiveDiamonds = new PlayingCard("D", "5", false);
        game.getLane(1).add(fiveDiamonds);

        // Try to move the 5 of Diamonds to the Diamonds suit pile (which is empty)
        assertThrows(IllegalArgumentException.class, () -> {
            game.moveToSuitPile("1", "D");
        }, "Only Aces should be allowed to start a suit pile");
    }

    //Test Moving Cards in Invalid Sequence Between Lanes
    /*
    @Test
    public void testInvalidMoveBetweenLanes() {
        game.setupGame();  // Ensure the game is set up

        // Set up two cards: 7 of Spades in lane 1, and 6 of Clubs in lane 2
        PlayingCard sevenSpades = new PlayingCard("S", "7", false);
        PlayingCard sixClubs = new PlayingCard("C", "6", false);
        game.getLane(1).add(sevenSpades);
        game.getLane(2).add(sixClubs);

        // Try to move 6 of Clubs to 7 of Spades (invalid since both are black)
        assertThrows(IllegalArgumentException.class, () -> {
            game.moveBetweenLanes("2", "1", 1);
        }, "Cards of the same color cannot be placed on each other");
    }*/

    @Test
    public void testInvalidMoveToSuitPileDifferentSuit() {
        game.setupGame();  // Ensure the game is set up

        // Set up a 2 of Hearts in the lane
        PlayingCard twoOfHearts = new PlayingCard("H", "2", false);
        game.getLane(1).add(twoOfHearts);

        // Try to move the 2 of Hearts to the Spades suit pile (should fail)
        assertThrows(IllegalArgumentException.class, () -> {
            game.moveToSuitPile("1", "S");
        }, "Only Aces of the corresponding suit should be allowed in an empty suit pile");
    }



    // INTEGRATION TEST



    @Test
    public void testDisplayGameState() {
        game.setupGame();
        game.displayGameState();
    }

    @Test
    public void testHandleInvalidMoveCommandGracefully() {
        assertDoesNotThrow(() -> {
            game.handleUserCommand("INVALID");  // Invalid command should not throw exceptions
        }, "Invalid commands should not throw exceptions");
    }


}
