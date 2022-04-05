package com.bigyoshi.qrhunt;

import com.bigyoshi.qrhunt.player.RankInfo;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class RankInfoTest {
    private RankInfo mockRankInfo() {
        RankInfo mockRankInfo = new RankInfo();
        mockRankInfo.setTotalScannedRank(1);
        mockRankInfo.setBestUniqueQrRank(1);
        mockRankInfo.setTotalScoreRank(1);
        return mockRankInfo;
    }

    @Test
    void testGetTotalScanned(){
        RankInfo mockRankInfo = mockRankInfo();
        assertEquals(1, mockRankInfo.getTotalScanned());
    }

    @Test
    void testSetTotalScannedRank(){
        RankInfo mockRankInfo = mockRankInfo();
        mockRankInfo.setTotalScannedRank(2);
        assertEquals(2, mockRankInfo.getTotalScanned());
    }

    @Test
    void testGetBestUniqueQr(){
        RankInfo mockRankInfo = mockRankInfo();
        assertEquals(1, mockRankInfo.getBestUniqueQr());
    }

    @Test
    void testSetBestUniqueQrRank(){
        RankInfo mockRankInfo = mockRankInfo();
        mockRankInfo.setBestUniqueQrRank(2);
        assertEquals(2, mockRankInfo.getBestUniqueQr());
    }

    @Test
    void testGetTotalScore(){
        RankInfo mockRankInfo = mockRankInfo();
        assertEquals(1, mockRankInfo.getTotalScore());
    }

    @Test
    void testSetTotalScoreRank(){
        RankInfo mockRankInfo = mockRankInfo();
        mockRankInfo.setTotalScoreRank(2);
        assertEquals(2, mockRankInfo.getTotalScore());
    }
}

