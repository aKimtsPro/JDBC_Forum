package be.tftic.java.bonus;

import be.tftic.java.dao.ConnectionProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Query<T> implements AutoCloseable {

    private final Connection connection;
    private final PreparedStatement statement;
    private final boolean executed = false;

    public Query(String query) throws SQLException {
        this.connection = ConnectionProvider.getConnection();
        this.statement = this.connection.prepareStatement(query);
    }

    public void setLong(int position, long data) throws SQLException {
        this.statement.setLong(position, data);
    }

    public void setString(int position, String data) throws SQLException {
        this.statement.setString(position, data);
    }

    public Optional<T> fetchFirstResult(ResultSetExtractor<T> extractor) throws SQLException {
        if( executed )
            throw new IllegalStateException("this query already was executed");

        try (ResultSet rs = this.statement.executeQuery()) {
            if( rs.next() )
                return Optional.of( extractor.extract(rs) );
            else
                return Optional.empty();
        }
    }

    public List<T> fetchAllResult(ResultSetExtractor<T> extractor) throws SQLException {
        if( executed )
            throw new IllegalStateException("this query already was executed");

        try( ResultSet rs = this.statement.executeQuery() ){
            List<T> results = new ArrayList<>();
            while( rs.next() ){
                results.add( extractor.extract(rs) );
            }
            return results;
        }
    }

    public int update() throws SQLException {
        if( executed )
            throw new IllegalStateException("this query already was executed");

        return this.statement.executeUpdate();
    }

    @Override
    public void close() {
        try {
            if( this.statement != null ) {
                statement.close();
                System.out.println("-- STATEMENT CLOSED --");
            }
        }
        catch (Exception ignored){}
        try {
            if(this.connection != null) {
                connection.close();
                System.out.println("-- CONNECTION CLOSED --");
            }
        }
        catch (Exception ignored){}
    }
}
