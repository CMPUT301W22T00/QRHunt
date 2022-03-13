package com.bigyoshi.qrhunt;

public class Ranking {

    private String playerUsername;
    private String playerRank;
    private String highestUnique;
    private String scansTotal;
    private String scoreTotal;

    public Ranking(String playerUsername, String playerRank, String highestUnique,
                   String scansTotal, String scoreTotal) {
        this.playerUsername = playerUsername;
        this.playerRank = playerRank;
        this.highestUnique = highestUnique;
        this.scansTotal = scansTotal;
        this.scoreTotal = scoreTotal;
    }

    public void setPlayerUsername(String playerUsername) {
        this.playerUsername = playerUsername;
    }

    public void setPlayerRank(String playerRank) {
        this.playerRank = playerRank;
    }

    public void setHighestUnique(String highestUnique) {
        this.highestUnique = highestUnique;
    }

    public void setScansTotal(String scansTotal) {
        this.scansTotal = scansTotal;
    }

    public void setScoreTotal(String scoreTotal) {
        this.scoreTotal = scoreTotal;
    }
}
