package be.tftic.java;

import java.sql.*;

public class Main {
    public static void main(String[] args) {




    }

    public static void getOnePost(long id){
        // ...
    }

    public static void getAllPosts() {
        // SELECT * FROM Post

        // Class.forName("org.postgresql.Driver");
        try {
            String query = "SELECT * FROM Post";
            Connection connection = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/forum_db",
                    "user",
                    "pass"
            );
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while( rs.next() ) {
                long id = rs.getLong("id");
                String title = rs.getString("title");
                String content = rs.getString("content");

                System.out.println("Post { id: " +id+", title: " +title+ ", content: " + content + "}"  );
            }
        }
        catch (SQLException ex) {
            System.out.println("an error occured: " + ex.getMessage());
        }
    }
}
