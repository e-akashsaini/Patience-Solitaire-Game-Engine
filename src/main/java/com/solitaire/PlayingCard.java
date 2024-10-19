package com.solitaire;

// Class representing a playing card with a suit and rank
class PlayingCard {
    private String suitType;
    private String cardRank;
    private boolean isHidden;  // New attribute to track visibility of the card

    // Constructor
    public PlayingCard(String suitType, String cardRank, boolean isHidden) {
        this.suitType = suitType;
        this.cardRank = cardRank;
        this.isHidden = isHidden;  // Assign the passed 'isHidden' value
    }


    // Getters and setters for card properties
    public String getSuitType() {
        return suitType;
    }

    public String getCardRank() {
        return cardRank;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public void setHidden(boolean hidden) {
        this.isHidden = hidden;  // Setter for the hidden state
    }

    @Override
    public String toString() {
        return cardRank + suitType;  // Format: 5H, QS, etc.
    }
}


