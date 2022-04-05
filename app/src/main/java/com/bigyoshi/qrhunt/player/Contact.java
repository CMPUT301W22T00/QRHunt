package com.bigyoshi.qrhunt.player;

import java.io.Serializable;

/**
 * Definition: Stores the social media handle and email of the player
 * Note: N/A
 * Issue: N/A
 */
public class Contact implements Serializable {
    private String socialMedia;
    private String email;

    /**
     * Constructor method
     *
     * Note: Initialized as empty when the player is first created
     */
    public Contact(){
        this.socialMedia = "";
        this.email = "";
    }

    /**
     * Getter method
     *
     * @return socialMedia
     */
    public String getSocial(){

        return socialMedia;
    }

    /**
     * Getter method
     *
     * @return email
     */
    public String getEmail(){

        return email;
    }

    /**
     * Setter method
     *
     * @param newSocial new social
     */
    public void setSocial(String newSocial){

        socialMedia = newSocial;
    }

    /**
     * Setter method
     *
     * @param newEmail new email
     */
    public void setEmail(String newEmail){

        email = newEmail;
    }
}
