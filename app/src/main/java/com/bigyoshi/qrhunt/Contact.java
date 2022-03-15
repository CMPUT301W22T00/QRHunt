package com.bigyoshi.qrhunt;

/**
 * Definition: Stores the social media handle and email of the player
 * Note: NA
 * Issue: NA
 */
public class Contact {
    private String socialMedia;
    private String email;

    /**
     * Constructor
     * Note: Initialized as empty when the player is first created
     */
    public Contact(){
        this.socialMedia = "";
        this.email = "";
    }

    /**
     * Getter method
     * @return socialMedia
     */
    public String getSocial(){

        return socialMedia;
    }

    /**
     * Getter method
     * @return email
     */
    public String getEmail(){

        return email;
    }

    /**
     * Setter method
     * @param newSocial
     */
    public void setSocial(String newSocial){

        socialMedia = newSocial;
    }

    /**
     * Setter method
     * @param newEmail
     */
    public void setEmail(String newEmail){

        email = newEmail;
    }
}
