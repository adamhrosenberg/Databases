package cs5530;


import java.lang.*;
import java.sql.*;
import java.io.*;


public class UU {

    /**
    Member fields on the UU class
     */
    private String login;
    private String first;
    private String last;
    private String address;
    private String num;
    private String pass;

    /**
     *
     * @param login Primary key. unique.
     * @param first
     * @param last
     * @param address
     * @param num
     * @param pass
     */
    public UU(String login, String first, String last, String address, String num, String pass) {
        this.login = login;
        this.first = first;
        this.last = last;
        this.address = address;
        this.pass = pass;
        this.num = num;
    }


    /**
     *
     * @param user user to create. passed in from Driver.
     * @return whether or not creation was succesful
     */
    public static boolean createUser(UU user, Statement stmt){
        //TODO check for uniqueness.

        String query = "insert into UU (login, name, address, phone) values ('" +
                user.getLogin() + "', '" +
                user.getFullName() + "', '" +
                user.getAddress() + "', ," +
                user.getNum() + "';";

        try {
            ResultSet rs = stmt.executeQuery(query);
            //check result somehow before returning true. TODO.
            return true;
        } catch (Exception e){
            System.err.println("There was an error creating the user" + e);
            return false;
        }
    }


    /**
     * Getter functions for private variables
     */

    public String getLogin() {
        return this.login;
    }

    public String getFirst() {
        return this.first;
    }

    public String getFullName() {
        return this.first + this.last;
    }

    public String getLast() {
        return this.last;
    }

    public String getAddress() {
        return this.address;
    }

    public String getNum() {
        return this.num;
    }
}
