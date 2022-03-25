package com.bigyoshi.qrhunt.qr;

/**
 * Definition: Generates a QR code with the account specifications to access the account on other devices
 * Note: NA
 * Issues: This is not implement yet
 */
public class UnplayableQrCode {

    /**
     * Constructor method
     *
     */
    public UnplayableQrCode(){
    }

    /**
     * Checks if it has stored a player's game status
     *
     * @return true
     */
    public Boolean isGameStatus(){
        /* True if it is game status, false then it is LogIn;
           could have a isLogIn for completeness though
         */
        return true;
    }

    /**
     * Gets game status features and displays it in a view
     *
     */
    public void getGameStatusInfo(){
        // Function used to get game status features and displaying it in a view (UI)
    }

    /**
     * Gets accessing account or "log-in" specifications
     *
     */
    public void getLogInInfo(){
        // Function used to get log-in specifications
        // We need to somehow, get this info -> log in immediately
    }
}
