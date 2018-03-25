package cs5530;


import java.lang.*;
import java.sql.*;
import java.io.*;

/**
 * This class holds methods related to the UU table.
 */

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
    private String city;
    private String state;

    /**
     *
     * @param login Primary key. unique.
     * @param first
     * @param last
     * @param address
     * @param num
     * @param pass
     */
    public UU(String login, String first, String last, String address, String num, String pass, String city, String state) {
        this.login = login;
        this.first = first;
        this.last = last;
        this.address = address;
        this.pass = pass;
        this.num = num;
        this.city = city;
        this.state = state;
    }


    /**
     *
     * @param user user to create. passed in from Driver.
     * @return whether or not creation was succesful
     */
    public static boolean createUser(UU user, Statement stmt){
        //TODO use prepared statements instead of concatanting strings for SQL QUERY!!!!!
        //very important

        ResultSet rs;

        String query = "insert into UU (login, firstName, lastName, address, phone, password, city, state) values ('" +
                user.getLogin() + "', '" +
                user.getFirst() + "', '" +
                user.getLast() + "', '" +
                user.getAddress() + "', '" +
                user.getNum() + "', '" +
                user.getPassword() + "', '" +
                user.getCity() + "', '" +
                user.getState() +"');";

        try {
            int result = stmt.executeUpdate(query);

            if(result > 0){
                //insert worked
                return true;
            }else{
                return false;
            }
        } catch (Exception e){
            System.err.println("There was an error creating the user" + e);
            return false;
        }
    }


    public static UU checkPassword(String login, String password, Statement stmt){
        String query = "select * from UU where login = '" + login + "' and password = '" +
        password + "';";

        UU currentUser = null;
        ResultSet rs;

        try{
            rs = stmt.executeQuery(query);
            if(rs.next()){
                // if this user is returned null, driver knows the password was incorrect.
                currentUser = convertRsToUser(rs);
                return currentUser;
            }
        }catch(Exception e){
            System.err.println("Error loggin in" + e);
        }
        // if this user is returned null, driver knows the password was incorrect.
        return currentUser;
    }

    /**
     *
     * @param rs
     * @return
     */
    private static UU convertRsToUser(ResultSet rs){
        UU user = null;
        try{
            String login = rs.getString("login");
            String first = rs.getString("firstName");
            String last = rs.getString("lastName");
            String address = rs.getString("address");
            String city = rs.getString("city");
            String state = rs.getString("state");
            String num = rs.getString("phone");
            String pass = rs.getString("password");
            user = new UU(login, first, last, address, num, pass, city, state);
        }catch(Exception e){
            System.err.println("Error converting RS to User " + e);
        }

        return user;
    }

    /**
     *
     * @param login
     * @param stmt
     * @return whether or not a user exists with this login.
     */
    public static boolean isLoginDuplicate(String login, Statement stmt){

        ResultSet rs;
        String query = "select * from UU where login = '" + login + "';";

        try {
            rs = stmt.executeQuery(query);
            ResultSetMetaData metaData = rs.getMetaData();
           if(!rs.next()){
               //no duplicate
               rs.close();
               return false;
           }else{
               //duplicate found
               rs.close();
               return true;
           }

        } catch(Exception e){
            System.err.println("Error while checking for duplicate login field. " + e);
        }
    return false;
    }
    
    public static boolean addTrusted(Connector con, String user1, String user2, String trusted) {

		String query = "insert into Trust values ('" + user1 + "', '" + user2 + "', '" + trusted + "');";

		try {
			int result = con.stmt.executeUpdate(query);

			if (result > 0) {
				// insert worked
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			System.err.println("There was an error creating the user" + e);
			return false;
		}
	}


    /**
     *
     * Award methods
     *
     */
	public static boolean awardUser(Connector con, String login){

        if(!isDuplicateAward(con, login)){
            String query = "insert into Awards values ('" + login + "');";

            try{
                int result = con.stmt.executeUpdate(query);

                if(result > 0){
                    // insert worked
                    return true;
                }else{
                    return false;
                }
            }catch (Exception e){
                System.err.println("There was an error addaing award");
            }

        }
        return true;
    }

    private static boolean isDuplicateAward(Connector con, String login){

        ResultSet rs;
        String query = "select * from Awards where user_login = '" + login + "';";

        try {
            rs = con.stmt.executeQuery(query);
            ResultSetMetaData metaData = rs.getMetaData();
            if(!rs.next()){
                //no duplicate
                rs.close();
                return false;
            }else{
                //duplicate found
                rs.close();
                return true;
            }

        } catch(Exception e){
            System.err.println("Error while checking for duplicate award. " + e);
        }
        return false;
    }


    /**
     *
     * Statistic Function
     *
     */


    /**
     * most popular UCs (in terms of total rides) for each category.
     * @param con
     * @param m
     */
    public static void mMostPopularUC(Connector con, int m){

    }

    /**
     * most expensive UC's (defined by average cost of all rides on a UC) for each category
     * @param con
     * @param m
     */
    public static void mMostExpevsiveUC(Connector con, int m){

    }


    /**
     * most highly rated UDs (defined by average scores from all feedbacks a UD has received for all of his UCs)
     * @param con
     * @param m
     */
    public static void mHighestRatedUD(Connector con, int m){

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
        return this.first + " " + this.last;
    }

    public String getPassword() {
        return this.pass;
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
    
    public String getCity() {
        return this.city;
    }
    
    public String getState() {
        return this.state;
    }

    public static void printUser(UU user){
        System.out.println("Printing user: " +
                user.getLogin() + " " + user.getFirst() + " " + user.getLast());
    }
}
