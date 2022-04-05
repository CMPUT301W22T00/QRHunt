package com.bigyoshi.qrhunt.player;

import java.io.Serializable;

/**
 * Definition: Represents the rank information
 * Note: N/A
 * Issues: N/A
 */
public class RankInfo implements Serializable {
    private int totalScore;
    private int bestUniqueQr;
    private int totalScanned;

    /**
     * Getter method
     *
     * @return Gets the total number scanned
     */
    public int getTotalScanned() {
        return totalScanned;
    }

    /**
     * Setter method
     *
     * @param totalScanned Gets the total number rank
     */
    public void setTotalScannedRank(int totalScanned) {
        this.totalScanned = totalScanned;
    }

    /**
     * Gets the best unique qr
     *
     * @return Best unique Qr
     */
    public int getBestUniqueQr() {
        return bestUniqueQr;
    }

    /**
     * Setter method
     *
     * @param bestUniqueQr New best unique Qr
     */
    public void setBestUniqueQrRank(int bestUniqueQr) {
        this.bestUniqueQr = bestUniqueQr;
    }

    /**
     * Getter method
     *
     * @return Total score
     */
    public int getTotalScore() {
        return totalScore;
    }

    /**
     * Setter method
     *
     * @param totalScore New total score rank
     */
    public void setTotalScoreRank(int totalScore) {
        this.totalScore = totalScore;
    }
}
