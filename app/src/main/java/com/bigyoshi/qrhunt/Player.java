package com.bigyoshi.qrhunt;

public class Player {
    private PlayerInfo pI;
    private QRLibrary qL;

    public Player(){
        pI = new PlayerInfo();
        qL = new QRLibrary();
    }

    public void deletePlayer(PlayerInfo playerToDelete, Player admin){
        PlayerInfo adminInfo = admin.getPlayerInfo();
        if (adminInfo.isAdmin()){
            // Delete Player from database
            adminInfo.deletePlayerInfo(playerToDelete);
        }
    }

    public PlayerInfo getPlayerInfo(){
        return pI;
    }
}
