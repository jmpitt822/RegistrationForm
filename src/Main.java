import jodd.json.JsonParser;
import jodd.json.JsonSerializer;
import jodd.json.meta.JSON;
import org.h2.tools.Server;
import spark.Spark;

import java.sql.*;
import java.util.ArrayList;

/**
 * Created by jeremypitt on 9/29/16.
 */
public class Main {

    public static void main(String[] args) throws SQLException {
        Server.createWebServer().start();
        Connection conn = DriverManager.getConnection("jdbc:h2:./main");
        createTables(conn);

        Spark.externalStaticFileLocation("public");
        Spark.init();
        Spark.get(
                "/user",
                ((request, response) -> {
                    ArrayList<User> users = selectUsers(conn);
                    JsonSerializer s = new JsonSerializer();
                    return s.serialize(users);
                })
        );

        Spark.post(
                "/user",
                ((request, response) -> {
                    String body = request.body();
                    JsonParser p = new JsonParser();
                    User u = p.parse(body, User.class);
                    insertUser(conn, u.username, u.address, u.email);
                    return "";
                })
        );

        Spark.put(
                "/user",
                ((request, response) -> {
                    String body = request.body();
                    JsonParser p = new JsonParser();
                    User u = p.parse(body, User.class);
                    editUser(conn, u.id,u.username, u.address, u.email);
                    return "";
                })
        );

        Spark.delete(
                "/user/:id",
                ((request, response) -> {
                    int id = Integer.parseInt(request.params("id"));
                    deleteUser(conn, id);
                    return "";
                })
        );
    }

    public static void createTables(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE TABLE IF NOT EXISTS users (id IDENTITY , user_name VARCHAR , address VARCHAR , email VARCHAR)");
    }

    public static void insertUser(Connection conn, String userName, String address, String email) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO users VALUES (NULL, ?, ?, ?)");
        stmt.setString(1, userName);
        stmt.setString(2, address);
        stmt.setString(3, email);
        stmt.execute();
    }

    public static ArrayList<User> selectUsers(Connection conn) throws SQLException {
        ArrayList<User> users = new ArrayList<>();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users");
        ResultSet results = stmt.executeQuery();
        while (results.next()){
            int id = results.getInt("id");
            String userName = results.getString("user_name");
            String address = results.getString("address");
            String email = results.getString("email");
            users.add(new User(id, userName, address, email));
        }
        return users;
    }

    public static void deleteUser(Connection conn, Integer id) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM users WHERE id = ?");
        stmt.setInt(1, id);
        stmt.execute();
    }

    public static void editUser(Connection conn, Integer id, String userName, String address, String email) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("UPDATE users SET user_name = ?, address = ?, email = ? WHERE id = ?");
        stmt.setString(1, userName);
        stmt.setString(2, address);
        stmt.setString(3, email);
        stmt.setInt(4, id);
        stmt.execute();
    }




}
