package be.tftic.java.dao;

import be.tftic.java.entity.Post;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PostDAO implements CrudDAO<Post, Long>{

    @Override
    public void insert(Post toInsert) {
        String query = """
            INSERT INTO post(id, title, content)
            VALUES (?,?,?)
        """;

        try(
                Connection connection = ConnectionProvider.getConnection();
                PreparedStatement statement = connection.prepareStatement(query);
        ) {
            statement.setLong(1, toInsert.getId());
            statement.setString(2, toInsert.getTitle());
            statement.setString(3, toInsert.getContent());

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public List<Post> getAll() {
        String query = "SELECT * FROM Post";
        try(
                Connection connection = ConnectionProvider.getConnection();
                PreparedStatement stmt = connection.prepareStatement(query);
                ResultSet rs = stmt.executeQuery();
        ) {
            List<Post> posts = new ArrayList<>();
            while( rs.next() ) {
                posts.add( extractLine(rs) );
            }
            return posts;
        }
        catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Optional<Post> getOne(Long postId) {
        String query = "SELECT * FROM Post WHERE id = ?";
        try (
                Connection connection = ConnectionProvider.getConnection();
                PreparedStatement stmt = connection.prepareStatement(query);
        ){
            stmt.setLong(1, postId);
            try( ResultSet rs = stmt.executeQuery() ){
                if( rs.next() ) {
                    return Optional.of( extractLine(rs) );
                }
                else {
                    return Optional.empty();
                }
            }
        }
        catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void update(Long id, Post entity) {
        String query = """
            UPDATE post
            SET
                title = ?,
                content = ?
            WHERE
                id= ?
        """;

        try(
            Connection co = ConnectionProvider.getConnection();
            PreparedStatement preparedStatement = co.prepareStatement(query);
        ){

            preparedStatement.setString(1, entity.getTitle());
            preparedStatement.setString(2, entity.getContent());
            preparedStatement.setLong(3, id);
            preparedStatement.executeUpdate();
        }
        catch(SQLException ex){
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Optional<Post> delete(Long id) {
        Optional<Post> postOptional = getOne(id);

        if( postOptional.isPresent() ){
            String query = "DELETE FROM post WHERE id = ?";
            try(
                    Connection co = ConnectionProvider.getConnection();
                    PreparedStatement stmt = co.prepareStatement(query);
            ){
                stmt.setLong(1, id);
                stmt.executeUpdate();
                return postOptional;
            }
            catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
        else
            return Optional.empty();
    }

    public Optional<Post> deleteVReturning(Long id) {
        String query = "DELETE FROM post WHERE id = ? RETURNING *";
        try(
                Connection co = ConnectionProvider.getConnection();
                PreparedStatement stmt = co.prepareStatement(query);
        ){
            stmt.setLong(1, id);
            try ( ResultSet rs = stmt.executeQuery() ){
                if( rs.next() )
                    return Optional.of( extractLine(rs));
                else
                    return Optional.empty();
            }
        }
        catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }


    public Optional<Post> deleteV2ResultSetUpdatable(long id){
        String query = "SELECT * FROM post WHERE id = ?";

        try(
                Connection co = ConnectionProvider.getConnection();
                PreparedStatement stmt = co.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        ){
            stmt.setLong(1, id);
            try( ResultSet rs = stmt.executeQuery() ){
                if( rs.next() ){
                    Post post = extractLine(rs);
                    rs.deleteRow();
                    return Optional.of(post);
                }
                else
                    return Optional.empty();
            }
        }
        catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    private Post extractLine(ResultSet rs) throws SQLException {
        if( rs.isAfterLast() || rs.isBeforeFirst() )
            throw new IllegalStateException("ResultSet is invalid state");

        long id = rs.getLong("id");
        String title = rs.getString("title");
        String content = rs.getString("content");

        return new Post(id, title, content);
    }
}
