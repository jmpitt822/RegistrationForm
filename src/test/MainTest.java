import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by jeremypitt on 9/29/16.
 */
public class MainTest {

    public Connection startConnection() throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:h2:mem:test");
        Main.createTables(conn);
        return conn;
    }
    @Test
    public void testUsers() throws SQLException {
        Connection conn = startConnection();
        Main.insertUser(conn, "Jimbo", "address", "email");
        Main.insertUser(conn, "James", "address2", "email2");
        Main.insertUser(conn, "Jerry", "address3", "email3");
        ArrayList<User> users = Main.selectUsers(conn);
        conn.close();

        assertTrue(users.size() == 3);
    }

    @Test
    public void testUpdateToDo() throws SQLException {
        Connection conn = startConnection();
        Main.insertUser(conn, "Jimbo", "address", "email");
        Main.editUser(conn, 1, "James", "new address", "new email");
        ArrayList<User> users = Main.selectUsers(conn);
        assertTrue(users.size() == 1);
        User user = users.get(0);
        assertTrue(user.username != "Jimbo");
    }

    @Test
    public void testDeleteToDo() throws SQLException {
        Connection conn = startConnection();
        Main.insertUser(conn, "Jimbo", "address", "email");
        Main.insertUser(conn, "Janice", "her address", "her email");
        ArrayList<User> users = Main.selectUsers(conn);
        assertTrue(users.size() == 2);

        Main.deleteUser(conn, 1);
        ArrayList<User> users1 = Main.selectUsers(conn);
        assertTrue(users1.size() == 1);
    }

}