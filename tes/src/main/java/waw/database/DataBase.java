package waw.database;

public class DataBase {

    public static String serverURL = "jdbc:mysql://localhost:3306/";

    public String databaseName = "";

    public String tableName = "";

    public DataBase(String databaseName, String tableName){
        this.databaseName = databaseName;
        this.tableName = tableName;
    }

    public void checkTable(){

    }



}
