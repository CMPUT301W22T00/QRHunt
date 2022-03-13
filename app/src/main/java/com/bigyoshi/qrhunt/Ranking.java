package com.bigyoshi.qrhunt;

public class Ranking {

    private String playerUsername;

    private int playerRank;
    private int highestUnique;
    private int scansTotal;
    private int scoreTotal;

    public Ranking(String playerUsername, int playerRank, int highestUnique,
                   int scansTotal, int scoreTotal) {

    
        this.playerUsername = playerUsername;
        this.playerRank = playerRank;
        this.highestUnique = highestUnique;
        this.scansTotal = scansTotal;
        this.scoreTotal = scoreTotal;
    }

    public void setPlayerUsername(String playerUsername) {
        this.playerUsername = playerUsername;
    }

    public void setPlayerRank(int playerRank) {
        this.playerRank = playerRank;
    }

    public void setHighestUnique(int highestUnique) {
        this.highestUnique = highestUnique;
    }

    public void setScansTotal(int scansTotal) {
        this.scansTotal = scansTotal;
    }

    public void setScoreTotal(int scoreTotal) {

        this.scoreTotal = scoreTotal;
    }
}
