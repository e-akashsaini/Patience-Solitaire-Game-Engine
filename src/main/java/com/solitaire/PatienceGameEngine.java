package com.solitaire;

import java.util.*;

class PatienceGameEngine {
    private List<PlayingCard> drawCardStack;  // Draw pile
    private List<PlayingCard> unusedCards;  // Cards that were drawn but not used
    private List<List<PlayingCard>> gameLanes;  // The seven lanes
    private List<PlayingCard> heartsStack, diamondsStack, clubsStack, spadesStack;  // Suit piles
    private boolean[] recentPlacement;  // Tracks if a card was placed on each lane
    private int totalScore;
    private int moveCounter; //Counts the total number of moves
    private List<String> recentCommands; // Oscillation problem solver - tracks recent commands


    // Initialize the game
    public PatienceGameEngine() {
        drawCardStack = new ArrayList<>();
        unusedCards = new ArrayList<>();
        gameLanes = new ArrayList<>();
        heartsStack = new ArrayList<>();
        diamondsStack = new ArrayList<>();
        clubsStack = new ArrayList<>();
        spadesStack = new ArrayList<>();
        recentPlacement = new boolean[7];  // Track Recent Card Placement in 7 Lanes
        recentCommands = new ArrayList<>();  // Store recent commands
        totalScore = 0;
        moveCounter = 0;
        setupGame();  // Setup deck and deal cards
    }

    // Setup and shuffle the deck, deal cards into lanes
    protected void setupGame() {
        String[] suits = {"H", "D", "C", "S"};
        String[] ranks = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};

        List<PlayingCard> fullDeck = new ArrayList<>();
        for (String suit : suits) {
            for (String rank : ranks) {
                fullDeck.add(new PlayingCard(suit, rank,true));  // All cards are hidden by default
            }
        }

        Collections.shuffle(fullDeck); // Make every game unique

        // Deal cards to the 7 lanes
        for (int i = 0; i < 7; i++) {
            gameLanes.add(new ArrayList<>());
            // Use j as the parameter with i to add cards in the lane
            for (int j = 0; j <= i; j++) {
                PlayingCard card = fullDeck.remove(fullDeck.size() - 1);
                if (j == i) {
                    card.setHidden(false);  // The top card in each lane should be visible
                }
                gameLanes.get(i).add(card);  // Deal cards into lanes
            }
        }

        // Remaining cards should be 24 and become the draw pile
        for (PlayingCard card : fullDeck) {
            card.setHidden(true);  // Cards in the draw pile should be hidden initially
        }
        drawCardStack.addAll(fullDeck);
    }

    // Display the current game state (including the number of cards in the draw pile)
    public void displayGameState() {
        System.out.println("--------------------------------------------------------------------------");
        System.out.println("Score: " + totalScore + " || Moves: " + moveCounter);
        System.out.println("Draw Pile: " + drawCardStack.size() + " cards remaining.");  // Show the number of cards remaining in the draw pile
        System.out.println("--------------------------------------------------------------------------");

        // Display lanes
        for (int i = 0; i < gameLanes.size(); i++) {
            System.out.print("Lane " + (i + 1) + ": ");
            List<PlayingCard> lane = gameLanes.get(i); // Get the current lane (list of cards)

            if (!lane.isEmpty()) {
                for (PlayingCard card : lane) {
                    if (card.isHidden()) {          // If the card is hidden, show *; otherwise, show the card
                        System.out.print("* ");
                    } else {
                        System.out.print(card + " ");
                    }
                }
            } else {
                System.out.print("<- Empty ->");
            }
            System.out.println();
        }

        // Display suit piles
        System.out.println("--------------------------------------------------------------------------");
        System.out.println("                            Suit Piles");
        System.out.println("--------------------------------------------------------------------------");
        System.out.println("Hearts  : " + heartsStack);
        System.out.println("Diamonds: " + diamondsStack);
        System.out.println("Clubs   : " + clubsStack);
        System.out.println("Spades  : " + spadesStack);
    }

    // Move a card between lanes (supports moving multiple cards)
    protected void moveBetweenLanes(String fromLane, String toLane, int numCards) {
        try {
            int sourceLaneIndex = Integer.parseInt(fromLane) - 1;
            int destinationLaneIndex = Integer.parseInt(toLane) - 1;

            List<PlayingCard> sourceLane = gameLanes.get(sourceLaneIndex);
            List<PlayingCard> destinationLane = gameLanes.get(destinationLaneIndex);

            // Check if the source lane is empty
            if (sourceLane.isEmpty()) {
                System.out.println("Cannot move from an empty lane.");
                return;  // Exit early if source lane is empty
            }

            // Check if the number of cards being moved is valid
            if (numCards > sourceLane.size()) {
                System.out.println("Not enough cards in the source lane to move.");
                return;  // Exit early if there are not enough cards in the source lane
            }

            // Get the sublist of cards to move (the last 'numCards' cards)
            List<PlayingCard> cardsToMove = new ArrayList<>(sourceLane.subList(sourceLane.size() - numCards, sourceLane.size()));

            // Check the bottom card of the stack and the top card of the destination lane
            PlayingCard bottomCardToMove = cardsToMove.get(0);

            // Check if the destination lane is empty
            if (destinationLane.isEmpty()) {
                // If the destination lane is empty, only Kings can be placed
                if (!bottomCardToMove.getCardRank().equals("K")) {
                    System.out.println("Only Kings can be placed in an empty lane.");
                    return;  // Exit early if not a King
                }
            } else {
                // Check the top card of the destination lane
                PlayingCard topCardInDestination = destinationLane.get(destinationLane.size() - 1);
                // Ensure that the cards form a valid sequence (alternating color and one rank lower)
                if (!isValidSequence(bottomCardToMove, topCardInDestination)) {
                    System.out.println("Invalid move. The bottom card of the stack cannot be placed on the top card of the destination lane.");
                    return;  // Exit early if not a valid sequence
                }
            }

            // Valid move, so perform the move
            destinationLane.addAll(cardsToMove);  // Add the cards to the destination lane

            // Set the moved cards as visible
            for (PlayingCard card : cardsToMove) {
                card.setHidden(false);  // Reveal the moved cards
            }

            // Remove the moved cards from the source lane
            sourceLane.subList(sourceLane.size() - numCards, sourceLane.size()).clear();

            // Reveal the next top card in the source lane (if any cards are left)
            if (!sourceLane.isEmpty()) {
                PlayingCard nextTopCard = sourceLane.get(sourceLane.size() - 1);  // Get the new top card
                nextTopCard.setHidden(false);  // Reveal the new top card
            }

            // Update the score for the valid move
            updateScore(fromLane, toLane, numCards);
            moveCounter++;

        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());  // Catch any unexpected error and print a message
        }
    }

    // Move a card from a lane to a suit pile
    protected void moveToSuitPile(String fromLane, String suitPile) {
        int sourceLaneIndex = Integer.parseInt(fromLane) - 1;
        List<PlayingCard> sourceLane = gameLanes.get(sourceLaneIndex);

        if (!sourceLane.isEmpty()) {
            PlayingCard cardToMove = sourceLane.get(sourceLane.size() - 1);
            if (canPlaceInSuitPile(cardToMove, suitPile)) {
                sourceLane.remove(sourceLane.size() - 1);
                addToSuitPile(cardToMove, suitPile);

                if (!sourceLane.isEmpty()) {
                    PlayingCard nextTopCard = sourceLane.get(sourceLane.size() - 1);
                    nextTopCard.setHidden(false);  // Reveal the next card
                }

                updateScore(fromLane, suitPile, 1);  // Update score
                moveCounter++;
                System.out.println("Moved " + cardToMove + " to Suit Pile " + suitPile);
            } else {
                System.out.println("Invalid move. Card cannot be placed in the suit pile.");
            }
        } else {
            System.out.println("Source lane is empty.");
        }
    }

    // Draw a card from the draw pile and store it in unused cards
    protected void drawCard() {
        if (!drawCardStack.isEmpty()) {
            PlayingCard cardToMove = drawCardStack.remove(drawCardStack.size() - 1);  // Remove the last card
            unusedCards.add(cardToMove);  // Store the drawn card temporarily
            moveCounter++;
            System.out.println("Drew card: " + cardToMove);  // Display the drawn card to the player
        } else {
            // If the draw pile is empty, recycle unused cards
            if (!unusedCards.isEmpty()) {
                recycleDrawPile();  // Recycle the unused cards into the draw pile
                System.out.println("<- Recycled the unused cards back into the draw pile ->");
            } else {
                System.out.println("!! No more cards to draw.");
            }
        }
    }

    // Process card movement from one pile/lane to another
    private void processCardMovement(String command) {
        String from = command.substring(0, 1);  // First character for the source
        String to = command.substring(1, 2);    // Second character for the destination

        if (from.equalsIgnoreCase("P")) {
            moveFromDrawPile(to);  // Moving from the draw pile to a lane or suit pile
        } else if (isLane(from) && isLane(to)) {
            moveBetweenLanes(from, to, 1);  // Moving between lanes
        } else if (isLane(from) && isSuitPile(to)) {
            moveToSuitPile(from, to);  // Moving from a lane to a suit pile
        } else {
            System.out.println("Invalid move command. Please try again.");
        }
    }

    // Handle user input commands for moving cards or quitting the game
    public void handleUserCommand(String userCommand) {
        userCommand = userCommand.toUpperCase().trim();  // Convert to uppercase and trim spaces

        if (userCommand.matches("\\d{3}")) {  // Command is a three-digit move, like 562

            trackRecentCommands(userCommand);   // Add the command to recent commands

            String fromLane = userCommand.substring(0, 1);
            String toLane = userCommand.substring(1, 2);
            int numCards = Integer.parseInt(userCommand.substring(2, 3));

            if (!detectOscillation()) {
                moveBetweenLanes(fromLane, toLane, numCards);
            } else {
                System.out.println("!! Oscillation detected! No score will be added.");
            }
        } else if (userCommand.length() == 2) {
            processCardMovement(userCommand);               // Handle simple commands
        } else if (userCommand.equals("Q")) {
            System.out.println("!! Exiting the game.");
            System.exit(0);                          // Quit the game
        } else if (userCommand.equals("D")) {
            drawCard();                                     // Draw a card from the draw pile
        } else {
            System.out.println("!! Invalid command. Please try again.");
        }
    }

    private void trackRecentCommands(String command) {
        recentCommands.add(command);                        // Add the new command to the list

        if (recentCommands.size() > 3) {                    // Keep only the last 3 commands
            recentCommands.remove(0);
        }
    }

    // Code to overcome Oscillation
    private boolean detectOscillation() {
        if (recentCommands.size() < 3) {
            return false;
        }

        String first = recentCommands.get(0);
        String second = recentCommands.get(1);
        String third = recentCommands.get(2);

        // Check if the first and third commands are the same and the second one is the reverse
        if (first.substring(0, 2).equals(third.substring(0, 2)) &&
                second.substring(0, 2).equals(new StringBuilder(first.substring(0, 2)).reverse().toString())) {
            System.out.println("!! Warning: Repeating moves detected.");
            return true;    // Oscillation detected
        }
        return false;
    }
    // Move a card from the unused cards (drawn but not used) to a lane or suit pile
    protected void moveFromDrawPile(String destination) {
        if (!unusedCards.isEmpty()) {
            PlayingCard cardToMove = unusedCards.remove(unusedCards.size() - 1);  // Get the last drawn card

            // Ensure the card becomes visible once drawn from the draw pile
            cardToMove.setHidden(false);  // Set the card as visible (unhide it)

            if (isLane(destination)) {
                int laneIndex = Integer.parseInt(destination) - 1;
                if (canPlaceInLane(cardToMove, gameLanes.get(laneIndex))) {  // Check whether we can place the card
                    gameLanes.get(laneIndex).add(cardToMove);  // Add the card to the lane
                    recentPlacement[laneIndex] = true;  // Mark that a card was placed on this lane
                    updateScore("P", destination, 1);  // Update score
                    moveCounter++;
                    System.out.println("Moved " + cardToMove + " to Lane " + destination);
                } else {
                    System.out.println("Invalid move. Card cannot be placed in this lane.");
                    unusedCards.add(cardToMove);  // Return card to unused pile if invalid
                }
            } else if (isSuitPile(destination)) {
                if (canPlaceInSuitPile(cardToMove, destination)) {
                    addToSuitPile(cardToMove, destination);
                    updateScore("P", destination, 1);  // Update score
                    moveCounter++;
                    System.out.println("Moved " + cardToMove + " to Suit Pile " + destination);
                } else {
                    System.out.println("Invalid move. Card cannot be placed in the suit pile.");
                    unusedCards.add(cardToMove);  // Return card to unused pile if invalid
                }
            }
        } else {
            System.out.println("The draw pile is empty.");
        }
    }


    // Recycle the unused cards back into the draw pile
    protected void recycleDrawPile() {
        drawCardStack.addAll(unusedCards);
        unusedCards.clear();                    // Clear the temporary storage
    }


    // Check if a stack of cards forms a valid sequence (alternating colors and descending rank)
    private boolean isValidCardStack(List<PlayingCard> cardsToMove) {
        for (int i = 0; i < cardsToMove.size() - 1; i++) {
            PlayingCard card = cardsToMove.get(i);
            PlayingCard nextCard = cardsToMove.get(i + 1);
            System.out.println("Checking sequence: " + card + " -> " + nextCard);  // Debug print: Show each card being compared

            if (!isValidSequence(card, nextCard)) {
                System.out.println("Invalid sequence between " + card + " and " + nextCard);  // Debug print: Invalid sequence
                return false;
            }
        }
        return true;
    }


    private void handleCardMove(String from, String to, int numCards) {
        if (isLane(from) && isLane(to)) {
            moveBetweenLanes(from, to, numCards);
        } else {
            System.out.println("Invalid move command. Please try again.");
        }
    }

    protected boolean canPlaceInLane(PlayingCard card, List<PlayingCard> lane) {
        if (lane.isEmpty()) {
            //System.out.println("Trying to place " + card + " in an empty lane.");
            if (card.getCardRank().equals("K")) {
                //System.out.println("Card is a King, placing in empty lane.");
                return true;  // Only Kings can be placed in an empty lane
            } else {
                System.out.println("Card is not a King, cannot place in empty lane.");
                return false;
            }
        } else {
            PlayingCard topCard = lane.get(lane.size() - 1);
            return isValidSequence(card, topCard);  // Check if the move follows the rules
        }
    }


    // Check if a card can be placed in a suit pile
    private boolean canPlaceInSuitPile(PlayingCard card, String suitPile) {
        List<PlayingCard> targetPile = getSuitPile(suitPile);

        // Check if the suit pile is empty and ensure only Aces can be placed in an empty pile
        if (targetPile.isEmpty()) {
            if (!card.getCardRank().equals("A")) {
                throw new IllegalArgumentException("Only Aces can be placed in an empty suit pile.");
            }
            return true;
        } else {
            PlayingCard topCard = targetPile.get(targetPile.size() - 1);
            // Check if the card is the next in sequence and has the same suit
            return card.getSuitType().equals(topCard.getSuitType()) && isRankOneMore(card.getCardRank(), topCard.getCardRank());
        }
    }


    // Add a card to the corresponding suit pile
    private void addToSuitPile(PlayingCard card, String suitPile) {
        getSuitPile(suitPile).add(card);  // Add the card to the suit pile
    }

    // Get the suit pile based on the suit label
    protected List<PlayingCard> getSuitPile(String suit) {
        switch (suit) {
            case "H":
                return heartsStack;
            case "D":
                return diamondsStack;
            case "C":
                return clubsStack;
            case "S":
                return spadesStack;
            default:
                return null;
        }
    }

    // Check if two cards can be placed in sequence (alternating colors and descending rank)
    protected boolean isValidSequence(PlayingCard card, PlayingCard topCard) {
        boolean isOppositeColor = ((card.getSuitType().equals("H") || card.getSuitType().equals("D")) &&
                (topCard.getSuitType().equals("C") || topCard.getSuitType().equals("S"))) ||
                ((card.getSuitType().equals("C") || card.getSuitType().equals("S")) &&
                        (topCard.getSuitType().equals("H") || topCard.getSuitType().equals("D")));

        boolean isOneRankLower = isRankOneLess(card.getCardRank(), topCard.getCardRank());

        System.out.println("Comparing " + card + " and " + topCard + ": isOppositeColor = " + isOppositeColor + ", isOneRankLower = " + isOneRankLower);  // Debug print: Show comparison details

        return isOppositeColor && isOneRankLower;
    }


    // Check if the rank of the card is one less than the top card
    protected boolean isRankOneLess(String rank, String topRank) {
        List<String> ranks = Arrays.asList("A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K");

        int cardRankIndex = ranks.indexOf(rank);
        int topCardRankIndex = ranks.indexOf(topRank);

        System.out.println("Comparing ranks: " + rank + " (" + cardRankIndex + ") and " + topRank + " (" + topCardRankIndex + ")");  // Debug print: Show rank indices

        // FIX: Make sure the logic checks if cardRankIndex is one less than topCardRankIndex
        return cardRankIndex == topCardRankIndex - 1;  // Corrected comparison
    }

    // Check if the rank is one more for valid suit pile moves
    private boolean isRankOneMore(String rank, String topRank) {
        List<String> ranks = Arrays.asList("A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K");
        return ranks.indexOf(rank) == ranks.indexOf(topRank) + 1;
    }

    // Scoring function that updates score based on the type of move
    private void updateScore(String from, String to, int numCards) {
        if (from.equals("P") && isSuitPile(to)) {
            totalScore += 10;  // 10 points for moving from draw pile to suit pile
        } else if (isLane(from) && isSuitPile(to)) {
            totalScore += 20;  // 20 points for moving from a lane to a suit pile
        } else if (isLane(from) && isLane(to)) {
            totalScore += 5 * numCards;   // 5 points for moving between lanes
        }
    }

    // Helper methods to check if a label corresponds to a lane or a suit pile
    private boolean isLane(String label) {
        return label.matches("[1-7]");  // Lanes are numbered 1 to 7
    }

    private boolean isSuitPile(String label) {
        return label.equals("H") || label.equals("D") || label.equals("C") || label.equals("S");  // Suit piles
    }


    // Return the size of a specific lane (number of cards in a lane)
    public int getLaneSize(int laneIndex) {
        return gameLanes.get(laneIndex - 1).size();
    }

    // Return the size of a specific suit pile (number of cards in the suit pile)
    public int getSuitPileSize(String suit) {
        switch (suit.toUpperCase()) {
            case "H":
                return heartsStack.size();
            case "D":
                return diamondsStack.size();
            case "C":
                return clubsStack.size();
            case "S":
                return spadesStack.size();
            default:
                throw new IllegalArgumentException("Invalid suit: " + suit);
        }
    }

    // Return the last drawn card from the draw pile
    public PlayingCard getLastDrawnCard() {
        return unusedCards.isEmpty() ? null : unusedCards.get(unusedCards.size() - 1);
    }

    // Return the size of the draw pile (number of cards left in the draw pile)
    public int getDrawPileSize() {
        return unusedCards.size();
    }

    // Check if there are any possible moves left in the game
    public boolean hasPossibleMoves() {
        // Implement logic to check if any moves are possible
        // This could involve checking all lanes, draw pile, and suit piles
        return !unusedCards.isEmpty() || gameLanes.stream().anyMatch(lane -> !lane.isEmpty());
    }

    // Method to get a specific lane by index (1-based)
    public List<PlayingCard> getLane(int laneIndex) {
        // Convert 1-based index to 0-based for internal usage
        return gameLanes.get(laneIndex - 1);
    }



}


