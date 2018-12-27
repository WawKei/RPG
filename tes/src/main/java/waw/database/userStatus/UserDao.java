package waw.database.userStatus;

import waw.Main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDao {

    /**
     * @ Table Name : Users
     */

    private Connection connection;

    public UserDao(){
        this.connection = Main.con;
    }

    /**
     *  get user one recode
     */
    public User selectUser(String userId) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("select * from users where userId = ?");
        ps.setObject(1, userId);
        ResultSet rs = ps.executeQuery();
        User bean = null;
        if (rs.next()) {
            bean= new User();
            bean.userId = (Integer)rs.getObject("userId");
            bean.userName = (String)rs.getObject("userName");
        }
        return bean;
    }


    /**
     *  get user one recode
     */
    public User selectUserByName(String userName) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("select * from users where userName = ?");
        ps.setString(1, userName);
        ResultSet rs = ps.executeQuery();
        User bean = null;
        if (rs.next()) {
            bean= new User();
            bean.userId = (Integer)rs.getObject("userId");
            bean.userName = (String)rs.getObject("userName");
        }
        return bean;
    }

    public void insertUser(String userName) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("insert into users (userName) VALUES (?)");
        ps.setString(1, userName);
        ps.executeUpdate();
    }

    public void updateUser(String userId, String key, Object value) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("update Users ? = ? WHERE userId = ? ");
        ps.setString(1, key);
        ps.setObject(2, value);
        ps.setObject(3,userId);
        ps.executeUpdate();
    }



}
