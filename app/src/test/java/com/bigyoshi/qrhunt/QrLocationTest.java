package com.bigyoshi.qrhunt;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.bigyoshi.qrhunt.qr.QrLocation;

import org.junit.jupiter.api.Test;

public class QrLocationTest {
    private QrLocation mockQrLocation() {
        QrLocation location = new QrLocation();
        location.setLatitude(53);
        location.setLong(-113);
        return location;
    }

    @Test
    void testGetLongitude(){
        QrLocation location = mockQrLocation();
        location.setLong(113);
        assertEquals("113.0", String.valueOf(location.getLongitude()));
    }

    @Test
    void testGetLatitude(){
        QrLocation location = mockQrLocation();
        location.setLatitude(51);
        assertEquals("51.0", String.valueOf(location.getLatitude()));
    }

    @Test
    void testGetGeoHash(){
        QrLocation location = mockQrLocation();
        assertEquals(location.getGeoHash(), mockQrLocation().getGeoHash());
    }

}
