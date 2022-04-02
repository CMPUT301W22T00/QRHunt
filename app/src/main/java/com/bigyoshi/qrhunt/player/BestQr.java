package com.bigyoshi.qrhunt.player;

import java.io.Serializable;

// Can be used to represent best unique code, best scoring code, best anything
public class BestQr implements Serializable {
    private String qrId;
    private int score;

    public String getQrId() {
        return qrId;
    }

    public void setQrId(String qrId) {
        this.qrId = qrId;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
