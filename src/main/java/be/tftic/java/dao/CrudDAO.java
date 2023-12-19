package be.tftic.java.dao;

import java.sql.ResultSet;
import java.util.List;
import java.util.Optional;

public interface CrudDAO<T, ID> {

    // CREATE
    void insert(T toInsert);

    // READ
    List<T> getAll();
    Optional<T> getOne(ID id);

    // UPDATE
    void update(ID id, T entity);

    // DELETE
    Optional<T> delete(ID id);
}
