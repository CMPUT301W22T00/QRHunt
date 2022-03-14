package com.bigyoshi.qrhunt;

public class Contact {
    private String socialMedia;
    private String email;

    public Contact(){
        this.socialMedia = "";
        this.email = "";
    }

    public String getSocial(){
        return socialMedia;
    }

    public String getEmail(){
        return email;
    }

    public void setSocial(String newSocial){
        socialMedia = newSocial;
    }

    public void setEmail(String newEmail){
        email = newEmail;
    }
}
