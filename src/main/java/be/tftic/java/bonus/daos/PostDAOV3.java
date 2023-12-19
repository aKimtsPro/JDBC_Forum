package be.tftic.java.bonus.daos;

import be.tftic.java.bonus.utils.Query;
import be.tftic.java.bonus.utils.QueryParam;
import be.tftic.java.bonus.utils.QueryV2;
import be.tftic.java.dao.CrudDAO;
import be.tftic.java.entity.Post;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static be.tftic.java.bonus.utils.SQLType.*;

public class PostDAOV3 implements CrudDAO<Post, Long> {

    @Override
    public void insert(Post toInsert) {
        String queryString = """
                INSERT INTO post
                VALUES (?,?,?)
                """;
        QueryV2.forUpdate(queryString)
                .addParam(new QueryParam(1, LONG, toInsert.getId()))
                .addParam(new QueryParam(2, STRING, toInsert.getTitle()))
                .addParam(new QueryParam(3, STRING, toInsert.getContent()))
                .update();
    }

    @Override
    public List<Post> getAll() {
        return QueryV2.forFetch("SELECT * FROM post", PostDAOV3::extractLine)
                .fetchAll();
    }

    @Override
    public Optional<Post> getOne(Long aLong) {
        return QueryV2.forFetch("SELECT * FROM post WHERE id = ?", PostDAOV3::extractLine)
                .addParam(new QueryParam(1, LONG, aLong))
                .fetchOne();
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

        QueryV2.forUpdate(query)
                .addParam(new QueryParam(1, STRING, entity.getTitle()))
                .addParam(new QueryParam(2, STRING, entity.getContent()))
                .addParam(new QueryParam(3, LONG, id))
                .update();
    }

    @Override
    public Optional<Post> delete(Long id) {
        String query = "DELETE FROM post WHERE id = ? RETURNING *";
        return QueryV2.forFetch(query, PostDAOV3::extractLine)
                .addParam(new QueryParam(1, LONG, id))
                .fetchOne();
    }

    private static Post extractLine(ResultSet rs) throws SQLException {
        if( rs.isAfterLast() || rs.isBeforeFirst() )
            throw new IllegalStateException("ResultSet is invalid state");

        long id = rs.getLong("id");
        String title = rs.getString("title");
        String content = rs.getString("content");

        return new Post(id, title, content);
    }
}
