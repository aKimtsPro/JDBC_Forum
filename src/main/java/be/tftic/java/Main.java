package be.tftic.java;

import be.tftic.java.entity.Post;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Main {
    public static void main(String[] args) {

        Post post1 = new Post();
//        System.out.println(post1);

        // Créer une copie du post d'id 1 avec comme title le même mais augmenté de ' - Copie'
        post1.setId(99);
        post1.setTitle("New Title");
        post1.setContent("');DROP TABLE post; SELECT ('");
        insert(post1);

        // Afficher tous les posts
        getAllPosts().forEach(System.out::println);

    }

    public static Optional<Post> getOnePost(long postId){
        String query = "SELECT * FROM Post WHERE id = " + postId;
        try (
            Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/forum_db", "user", "pass");
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
        ){
            if( rs.next() ) {
                long id = rs.getLong("id");
                String title = rs.getString("title");
                String content = rs.getString("content");

                return Optional.of( new Post(id, title, content) );
            }
        }
        catch (SQLException ex) {
            System.out.println("an error occured: " + ex.getMessage());
        }

        return Optional.empty();
    }

    public static List<Post> getAllPosts() {
        String query = "SELECT * FROM Post";
        try(
                Connection connection = DriverManager.getConnection(
                        "jdbc:postgresql://localhost:5432/forum_db",
                        "user",
                        "pass"
                );
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(query);
        ) {
            List<Post> posts = new ArrayList<>();
            while( rs.next() ) {
                long id = rs.getLong("id");
                String title = rs.getString("title");
                String content = rs.getString("content");

                Post post = new Post(id, title, content);
                posts.add( post );
            }
            return posts;
        }
        catch (SQLException ex) {
            System.out.println("an error occured: " + ex.getMessage());
            return new ArrayList<>();
        }
    }

    public static void insert(Post post){
        String query = "INSERT INTO post(id, title, content) " +
                "VALUES (?,?,?)";

        try(
            Connection connection = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/forum_db",
                    "user",
                    "pass"
            );
            PreparedStatement stmt = connection.prepareStatement(query);
        ) {
            stmt.setLong(1, post.getId());
            stmt.setString(2, post.getTitle());
            stmt.setString(3, post.getContent());

            /*int modifiedRows = */ stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
