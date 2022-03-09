package com.bigyoshi.qrhunt;

public class ExternalQRCode extends QRCode {
    private int value;
    private int latitude;
    private int longitude;
    private int numScanned;


    // We need to distinguish QRCodes already scanned and those who have not been scanned yet
    //  Since initialization of numScanned would either be an update OR just 1
    public ExternalQRCode(int longitude, int latitude, int value){
        this.value = value;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public void updateNumScanned(){
        this.numScanned += 1;
    }


}
