package games;

public class ToH {
    private final String playerOne;
    private final String playerTwo;
    private int roundNumber;
    private int scorePlayerOne;
    private int scorePlayerTwo;
    private String choicePlayerOne;
    private String choicePlayerTwo;

    public ToH(String playerOne, String playerTwo) {
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
        this.roundNumber = 1;
        this.scorePlayerOne = 0;
        this.scorePlayerTwo = 0;
    }

    public void setChoice(String playerName, String choice) {
        if (playerName.equals(playerOne))
            choicePlayerOne = choice;
        else
            choicePlayerTwo = choice;
    }

    public boolean bothPlayersChosen() {
        return choicePlayerOne != null && choicePlayerTwo != null;
    }

    public void clearChoices() {
        choicePlayerOne = null;
        choicePlayerTwo = null;
    }

    public void incrementRoundNumber() {
        roundNumber++;
    }

    public void incrementScore(String playerName) {
        if (playerName.equals(playerOne))
            scorePlayerOne++;
        else
            scorePlayerTwo++;
    }

    public String getPlayerOne() {
        return this.playerOne;
    }

    public String getPlayerTwo() {
        return this.playerTwo;
    }

    public int getRoundNumber() {
        return this.roundNumber;
    }

    public int getScorePlayerOne() {
        return this.scorePlayerOne;
    }

    public int getScorePlayerTwo() {
        return this.scorePlayerTwo;
    }
}
