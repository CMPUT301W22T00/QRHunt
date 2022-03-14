package com.bigyoshi.qrhunt;

public class InternalQRCode {

    public InternalQRCode(){
    }

    public Boolean isGameStatus(){
        // True if it is game status, false then it is LogIn; could have a isLogIn for completeness though
        return true;
    }

    public void getGameStatusInfo(){
        // Function used to get game status features and displaying it in a view (UI)
    }

    public void getLogInInfo(){
        // Function used to get log-in specifications
        // We need to somehow, get this info -> log in immediately
    }
}
