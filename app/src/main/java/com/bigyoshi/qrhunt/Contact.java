package com.bigyoshi.qrhunt;

public class Contact {
    private String socialMedia;
    private String email;

    public Contact(){
        this.socialMedia = "";
        this.email = "";
    }

    public void updateSocial(String newSocial){
        socialMedia = newSocial;
    }

    public void updateEmail(String newEmail){
        email = newEmail;
    }

    public String getSocial(){
        return socialMedia;
    }

    public String getEmail(){
        return email;
    }
}
