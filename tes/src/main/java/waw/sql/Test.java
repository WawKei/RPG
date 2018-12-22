package waw.sql;

import java.sql.*;

public class Test {

    public static void main(String[] args){
        Connection con = null;
        try {

            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/sample?useSSL=false", "root", "1qaz!QAZ");
            System.out.println("MySQLに接続できました。");

            Statement statement= con.createStatement();

            String sql="insert into employees values('123459','Viz', 19);";

            statement.executeUpdate(sql);

        } catch (SQLException e) {
            System.out.println( "Connection Failed. : " + e.getSQLState());
            System.out.println(e.getErrorCode());
            System.out.println(e.toString());

            System.out.println("MySQLに接続できませんでした。");
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    System.out.println("MySQLのクローズに失敗しました。");
                }
            }
        }
    }
}
