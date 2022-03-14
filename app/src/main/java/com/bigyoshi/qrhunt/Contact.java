package com.bigyoshi.qrhunt;
/*
Stores user's social contact information
 - Email
 - One social media handle
 */


/**
 * Definition:
 *
 *
 */
public class Contact {
    private String socialMedia;
    private String email;

    /**
     *
     *
     */
    public Contact(){
        this.socialMedia = "";
        this.email = "";
    }

    /**
     *
     * @return
     */
    public String getSocial(){
        return socialMedia;
    }

    /**
     *
     * @return
     */
    public String getEmail(){
        return email;
    }

    /**
     *
     * @param newSocial
     */
    public void setSocial(String newSocial){
        socialMedia = newSocial;
    }

    /**
     *
     * @param newEmail
     */
    public void setEmail(String newEmail){
        email = newEmail;
    }
}
