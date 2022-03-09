package com.bigyoshi.qrhunt;

public class ExternalQRCode extends QRCode {
    private String id; // Hash of the actual data from the scan
    private int value; // The score of the QR code
    private int latitude;
    private int longitude;
    private int numScanned;


    // We need to distinguish QRCodes already scanned and those who have not been scanned yet
    //  Since initialization of numScanned would either be an update OR just 1
    public ExternalQRCode(int longitude, int latitude, int value, String id){
        this.value = value;
        this.longitude = longitude;
        this.latitude = latitude;
        this.id = id;
        this.numScanned = 1;
    }

    // Just a bunch of getters and setters
    public int getNumScanned() { return this.numScanned; }

    public void updateNumScanned(){
        this.numScanned += 1;
    }

    public int getValue() { return this.value; }

    public void setValue(int value) { this.value = value; }

    public int getLatitude() { return latitude; }

    public void setLatitude(int latitude) { this.latitude = latitude; }

    public int getLongitude() { return longitude; }

    public void setLongitude(int longitude) { this.longitude = longitude; }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }
}
