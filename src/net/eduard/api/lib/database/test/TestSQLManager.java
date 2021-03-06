package net.eduard.api.lib.database.test;

import net.eduard.api.lib.database.DBManager;
import net.eduard.api.lib.database.SQLEngineType;
import net.eduard.api.lib.database.SQLManager;

import java.util.List;

public class TestSQLManager {
    public static void main(String[] args) {
        DBManager dbManager = new DBManager("root", "", "localhost");
        dbManager.openConnection();
        SQLManager manager = new SQLManager(dbManager);
        testComplexEntityTable(manager);


        dbManager.closeConnection();
    }
    public static void testComplexEntityTable(SQLManager manager){


        manager.deleteTable(ComplexEntity.class);
        manager.createTable(ComplexEntity.class);
        ComplexEntity dado = new ComplexEntity();
        manager.insertData(dado);
        ComplexEntity result = manager.getData(ComplexEntity.class, 1);

        System.out.println(result.toString());

    }
    public static void testPlayerDataTable(SQLManager manager){
        manager.clearTable(PlayerData.class);
        manager.deleteTable(PlayerData.class);
        manager.createTable(PlayerData.class);
        PlayerData dado = new PlayerData(1,"Nome","Porco",100);
        PlayerData dado2 = new PlayerData(2,"Nome2","Galinha",200);
        manager.insertData(dado);
        manager.insertData(dado2);
        dado = manager.getData(PlayerData.class,1);
        System.out.println(dado);
        List<PlayerData> players = manager.getAllData(PlayerData.class);
        manager.deleteData(dado2);
        dado.setCashAmount(150);
        manager.updateData(dado);
        dado = manager.getData(PlayerData.class,1);
        players = manager.getAllData(PlayerData.class);
        System.out.println(players);
    }
}
