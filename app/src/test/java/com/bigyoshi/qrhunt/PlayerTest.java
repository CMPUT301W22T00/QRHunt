package com.bigyoshi.qrhunt;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import com.bigyoshi.qrhunt.player.Player;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class PlayerTest {
    private FirebaseFirestore db;
    private CollectionReference collectionReference;

    private Player mockPlayer(){
        return new Player("TEST-T00", "TEST-TOO", null);
    }

    private Player mockAdmin(){
        Player mockAdmin = new Player("TEST-T00-Admin", "TEST-TOO-Admin", null);
        mockAdmin.setAdmin(true);
        return mockAdmin;
    }

    @Test
    void testFromPlayerId(){
        db = FirebaseFirestore.getInstance();
        collectionReference = db.collection("users");
        Player mockPlayer = mockPlayer();
        Player mockPlayer2 = Player.fromPlayerId(mockPlayer.getPlayerId());
        assertEquals(mockPlayer, mockPlayer2);
    }

    @Test
    void testFromDoc(){

    }

    @Test
    void testGetPlayerId(){

    }

    @Test
    void testSetPlayerId(){

    }

    @Test
    void testGetBestUniqueQr(){

    }

    @Test
    void testGetBestScoringQr(){

    }

    @Test
    void testGetRankInfo(){

    }

    @Test
    void testGetContact(){

    }

    @Test
    void testGetTotalScore(){

    }

    @Test
    void testGetUsername(){

    }

    @Test
    void testSetContact(){

    }

    @Test
    void testSetUsername(){

    }

    @Test
    void testMakeAdmin(){

    }

    @Test
    void testSetAdmin(){

    }

    @Test
    void testIsAdmin(){

    }

    @Test
    void testSavePlayer(){

    }

    @Test
    void testInitialize(){

    }

    @Test
    void testSetPropsFromDoc(){

    }

    @Test
    void testUpdateDB(){

    }
}
