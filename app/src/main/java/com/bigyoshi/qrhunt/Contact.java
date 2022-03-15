package com.bigyoshi.qrhunt;

/**
 * Definition: Stores the user's contact information
 *
 *
 */
public class Contact {
    private String socialMedia;
    private String email;

    /**
     * Constructor
     *
     */
    public Contact(){
        this.socialMedia = "";
        this.email = "";
    }

    /**
     *
     * @return socialMedia
     */
    public String getSocial(){
        return socialMedia;
    }

    /**
     *
     * @return email
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
