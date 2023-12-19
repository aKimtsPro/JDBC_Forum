package be.tftic.java;


import be.tftic.java.dao.ConnectionProvider;
import be.tftic.java.dao.PostDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        demoTransaction();
    }


    public static void demoTransaction(){

        String query = "INSERT INTO post VALUES (1,'okok', 'okok')";

        try(
                Connection co = ConnectionProvider.getConnection();
                PreparedStatement stmt = co.prepareStatement(query);
        ){
            co.setAutoCommit(false);
            stmt.executeUpdate();
            // applique les modifs
            co.commit();
            // annule les modifs
//            co.rollback();

            PreparedStatement fetchStmt = co.prepareStatement("SELECT * FROM post WHERE id = 1");
            ResultSet rs = fetchStmt.executeQuery();
            while (rs.next()){
                System.out.println( PostDAO.extractLine(rs) );
            }


        }
        catch (SQLException ex){
            throw new RuntimeException(ex);
        }
    }

}
