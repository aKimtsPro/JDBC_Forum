package be.tftic.java.bonus;

import be.tftic.java.dao.CrudDAO;
import be.tftic.java.entity.Post;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class PostDAOV2 implements CrudDAO<Post, Long> {

    @Override
    public void insert(Post toInsert) {
        String query = """
            INSERT INTO post(id, title, content)
            VALUES (?,?,?)
        """;

        try( Query<Post> postQuery = new Query<>(query) ){
            postQuery.setLong(1, toInsert.getId());
            postQuery.setString(2, toInsert.getTitle());
            postQuery.setString(3, toInsert.getContent());
            postQuery.update();
        }
        catch (SQLException ex){
            throw new RuntimeException(ex);
        }
    }

    @Override
    public List<Post> getAll() {
        String query = "SELECT * FROM post";
        try( Query<Post> postQuery = new Query<>(query) ){
            return postQuery.fetchAllResult(this::extractLine);
        }
        catch (SQLException ex){
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Optional<Post> getOne(Long id) {
        String query = "SELECT * FROM post WHERE id = ?";
        try( Query<Post> postQuery = new Query<>(query) ){
            postQuery.setLong(1, id);
            return postQuery.fetchFirstResult(this::extractLine);
        }
        catch (SQLException ex){
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void update(Long aLong, Post entity) {

    }

    @Override
    public Optional<Post> delete(Long aLong) {
        return Optional.empty();
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
