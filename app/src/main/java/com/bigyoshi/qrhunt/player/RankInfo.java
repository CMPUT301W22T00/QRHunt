package com.bigyoshi.qrhunt.player;

import java.io.Serializable;

public class RankInfo implements Serializable {
    private int totalScore;
    private int bestUniqueQr;
    private int totalScanned;

    public int getTotalScanned() {
        return totalScanned;
    }

    public void setTotalScannedRank(int totalScanned) {
        this.totalScanned = totalScanned;
    }

    public int getBestUniqueQr() {
        return bestUniqueQr;
    }

    public void setBestUniqueQrRank(int bestUniqueQr) {
        this.bestUniqueQr = bestUniqueQr;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScoreRank(int totalScore) {
        this.totalScore = totalScore;
    }
}