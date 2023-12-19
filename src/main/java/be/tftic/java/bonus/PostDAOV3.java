package be.tftic.java.bonus;

import be.tftic.java.dao.CrudDAO;
import be.tftic.java.entity.Post;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class PostDAOV3 implements CrudDAO<Post, Long> {

    @Override
    public void insert(Post toInsert) {
    }

    @Override
    public List<Post> getAll() {
        return QueryV2.forQuery("SELECT * FROM post", Post.class)
                .fetchAll(this::extractLine);
    }

    @Override
    public Optional<Post> getOne(Long aLong) {
        return QueryV2.forQuery("SELECT * FROM post WHERE id = ?", Post.class)
                .addParam(new QueryV2.QueryParam(1, QueryV2.SQLType.LONG, aLong))
                .fetchOne(this::extractLine);
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
