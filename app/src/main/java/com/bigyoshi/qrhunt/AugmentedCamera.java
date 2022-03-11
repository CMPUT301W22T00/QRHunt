package com.bigyoshi.qrhunt;

public class AugmentedCamera {

    public void scanQRCode(String scannedCode, Integer isExternal){
        ExternalQRCode qrCode;
        // Scans QRCode -> reads whether it is a internal or external -> makes it either external or internal
        // -> If external -> calculate the value -> save into db -> player sets up QRProfile
        // -> If internal, show the game status (as a pop up) or log-in to the account
        
    }

    private int calculateValue(){
        // Need to calculate score here
        // Probably have to pass in the hash or whatever we use to calculate the value
        return 0;
    }

    private void capturePhoto(){
        // Saves the photo and correlates it to QRProfile
    }
}
