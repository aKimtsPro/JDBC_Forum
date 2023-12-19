package be.tftic.java;


import be.tftic.java.dao.PostDAO;

public class Main {
    public static void main(String[] args) {

        PostDAO dao = new PostDAO();
        System.out.println( dao.delete(99L) );
    }

}
