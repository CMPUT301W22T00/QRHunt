package com.bigyoshi.qrhunt;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AugmentedCamera {

    public void scanQRCode(String scannedCode, Integer isExternal){
        ExternalQRCode qrCode;
        // Scans QRCode -> reads whether it is a internal or external -> makes it either external or internal
        // -> If external -> calculate the value -> save into db -> player sets up QRProfile
        // -> If internal, show the game status (as a pop up) or log-in to the account


        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        assert md != null;
        byte[] digest = md.digest(scannedCode.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
        }
        String hash = sb.toString();
        // sha1 gives 160 bits → max value is therefore 2¹⁶⁰
        int score = new BigInteger(1, digest).multiply(new BigInteger("100")).divide((new  BigInteger("2").pow(160))).intValue();

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
