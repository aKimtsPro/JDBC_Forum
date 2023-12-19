package be.tftic.java.bonus;

import be.tftic.java.dao.ConnectionProvider;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class QueryV2<T> {

    private final String query;
    private final List<QueryParam> queryParams = new LinkedList<>();
    private QueryV2(String query){
        this.query = query;
    }

    public static <T> QueryV2<T> forQuery(String queryString, Class<T> returnType){
        return new QueryV2<T>(queryString);
    }

    Optional<T> fetchOne(ResultSetExtractor<T> extractor){
        try(
                Connection co = ConnectionProvider.getConnection();
                PreparedStatement preparedStatement = co.prepareStatement(query);
        ) {
            for (QueryParam queryParam : queryParams) {
                addParamToStatement(preparedStatement, queryParam);
            }

            try (ResultSet rs= preparedStatement.executeQuery()){
                if( rs.next() )
                    return Optional.of(extractor.extract(rs));
                else
                    return Optional.empty();
            }
        }
        catch (SQLException ex){
            throw new RuntimeException(ex);
        }
    }

    List<T> fetchAll(ResultSetExtractor<T> extractor){
        try(
                Connection co = ConnectionProvider.getConnection();
                PreparedStatement preparedStatement = co.prepareStatement(query);
        ) {
            for (QueryParam queryParam : queryParams) {
                addParamToStatement(preparedStatement, queryParam);
            }

            try (ResultSet rs= preparedStatement.executeQuery()){
                List<T> results = new ArrayList<>();
                while( rs.next() ) {
                    results.add(extractor.extract(rs));
                }
                return results;
            }
        }
        catch (SQLException ex){
            throw new RuntimeException(ex);
        }
    }

    int update(){
        try(
                Connection co = ConnectionProvider.getConnection();
                PreparedStatement preparedStatement = co.prepareStatement(query);
        ) {
            for (QueryParam queryParam : queryParams) {
                addParamToStatement(preparedStatement, queryParam);
            }
            return preparedStatement.executeUpdate();
        }
        catch (SQLException ex){
            throw new RuntimeException(ex);
        }
    }

    public QueryV2<T> addParam(QueryParam param){
        this.queryParams.add(param);
        return this;
    }

    private void addParamToStatement(PreparedStatement statement, QueryParam param) throws SQLException {
        switch (param.type){
            case LONG -> statement.setLong(param.position, (long)param.value);
            case INT -> statement.setInt(param.position, (int)param.value);
            case STRING -> statement.setString(param.position, (String)param.value);
        }
    }

    public static record QueryParam(
            int position,
            SQLType type,
            Object value
    ){}

    public static enum SQLType {
        LONG(Types.BIGINT),
        INT(Types.INTEGER),
        STRING(Types.VARCHAR);


        private final int constantValue;

        SQLType(int constantValue) {
            this.constantValue = constantValue;
        }

        public int getConstantValue() {
            return constantValue;
        }
    }

}
