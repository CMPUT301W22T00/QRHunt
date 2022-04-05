package com.bigyoshi.qrhunt.player;

import java.io.Serializable;


/**
 * Definition: Can be used to represent best unique code, best scoring code, best anything
 * Note: N/A
 * Issues: N/A
 */
public class BestQr implements Serializable {
    private String qrId;
    private int score;

    /**
     * Retrieves the QR id
     *
     * @return Qr id
     */
    public String getQrId() {
        return qrId;
    }

    /**
     * Sets Qr id
     * @param qrId Qr id
     */
    public void setQrId(String qrId) {
        this.qrId = qrId;
    }

    /**
     * Gets score
     * @return score of Qr
     */
    public int getScore() {
        return score;
    }

    /**
     * Sets the Qr score
     * @param score new Qr score
     */
    public void setScore(int score) {
        this.score = score;
    }
}
