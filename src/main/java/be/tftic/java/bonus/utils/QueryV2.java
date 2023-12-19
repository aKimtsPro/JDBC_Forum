package be.tftic.java.bonus.utils;

import be.tftic.java.dao.ConnectionProvider;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public abstract class QueryV2 {

    private final String query;
    private final List<QueryParam> queryParams = new LinkedList<>();
    private QueryV2(String query){
        this.query = query;
    }

    protected String getQuery() {
        return query;
    }

    protected List<QueryParam> getQueryParams() {
        return queryParams;
    }

    public static UpdateQuery forUpdate(String query){
        return new UpdateQuery(query);
    }
    public static <T> FetchQuery<T> forFetch(String queryString, ResultSetExtractor<T> extractor){
        return new FetchQuery<T>(queryString, extractor);
    }

    public static class FetchQuery<T> extends QueryV2 {

        private final ResultSetExtractor<T> extractor;

        private FetchQuery(String query, ResultSetExtractor<T> extractor) {
            super(query);
            this.extractor = extractor;
        }

        public FetchQuery<T> addParam(QueryParam param){
            this.getQueryParams().add(param);
            return this;
        }

        public Optional<T> fetchOne(){
            try(
                    Connection co = ConnectionProvider.getConnection();
                    PreparedStatement preparedStatement = co.prepareStatement(getQuery());
            ) {
                for (QueryParam queryParam : getQueryParams()) {
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

        public List<T> fetchAll(){
            try(
                    Connection co = ConnectionProvider.getConnection();
                    PreparedStatement preparedStatement = co.prepareStatement(getQuery());
            ) {
                for (QueryParam queryParam : getQueryParams()) {
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
    }

    public static class UpdateQuery extends QueryV2{

        public UpdateQuery addParam(QueryParam param){
            this.getQueryParams().add(param);
            return this;
        }

        public UpdateQuery(String query) {
            super(query);
        }

        public int update(){
            try(
                    Connection co = ConnectionProvider.getConnection();
                    PreparedStatement preparedStatement = co.prepareStatement(getQuery());
            ) {
                for (QueryParam queryParam : getQueryParams()) {
                    addParamToStatement(preparedStatement, queryParam);
                }
                return preparedStatement.executeUpdate();
            }
            catch (SQLException ex){
                throw new RuntimeException(ex);
            }
        }
    }


    protected void addParamToStatement(PreparedStatement statement, QueryParam param) throws SQLException {
        switch (param.type()){
            case LONG -> statement.setLong(param.position(), (long)param.value());
            case INT -> statement.setInt(param.position(), (int)param.value());
            case STRING -> statement.setString(param.position(), (String)param.value());
        }
    }

}
