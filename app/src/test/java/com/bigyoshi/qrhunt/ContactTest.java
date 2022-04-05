package com.bigyoshi.qrhunt;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import com.bigyoshi.qrhunt.player.Contact;

public class ContactTest {
    private Contact mockContact(){
        Contact contact = new Contact();
        return contact;
    }

    @Test
    public void testEmail(){
        Contact contact = mockContact();
        assertEquals(contact.getEmail(), "");

        String mockEmail = "email@domain.ca";
        contact.setEmail(mockEmail);

        assertNotNull(contact.getEmail());
        assertEquals(contact.getEmail(), mockEmail);
    }

    @Test
    public void testSocial(){
        Contact contact = mockContact();
        assertEquals(contact.getSocial(), "");

        String mockSocial = "twitch.tv";
        contact.setSocial(mockSocial);

        assertNotEquals(contact.getSocial(),"");
        assertEquals(contact.getSocial(), mockSocial);
    }
}
