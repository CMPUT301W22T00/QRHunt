package com.bigyoshi.qrhunt;

import com.bigyoshi.qrhunt.player.BestQr;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class BestQRTest {
    private BestQr mockBestQRR(){
        BestQr mockBestQR = new BestQr();
        mockBestQR.setQrId("TEST-T00");
        mockBestQR.setScore(999);
        return mockBestQR;
    }

    @Test
    void testGetQrId(){
        BestQr mockBestQr = mockBestQRR();
        assertEquals("TEST-T00", mockBestQr.getQrId());
    }

    @Test
    void testSetQrId(){
        BestQr mockBestQr = mockBestQRR();
        mockBestQr.setQrId("TEST-T002");
        assertEquals("TEST-T002", mockBestQr.getQrId());
    }

    @Test
    void testGetScore(){
        BestQr mockBestQr = mockBestQRR();
        assertEquals(999, mockBestQr.getScore());
    }

    @Test
    void testSetScore(){
        BestQr mockBestQr = mockBestQRR();
        mockBestQr.setScore(0);
        assertEquals(0, mockBestQr.getScore());
    }

}
