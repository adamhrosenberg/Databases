package cs5530;


import java.lang.*;
import java.sql.*;
import java.io.*;
import java.util.ArrayList;

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
     * Given two users, determine their degree of seperation.
     *
     * @param con
     * @param login1 first username login
     * @param login2 second username login
     * @return
     * 0 - if no degree
     * 1 - if 1 degree of seperation
     * 2 - if 2 degrees of seperation
     */
    public static int usersDegreeOfSeperation(Connector con, String login1, String login2){
        if(checkFirstDegree(con, login1, login2)){
            return 1;
        }

        if(checkForSecondDegree(con, login1, login2)){
            return 2;
        }

        return 0;
    }

    private static boolean checkFirstDegree(Connector con, String login1, String login2){
        ResultSet rs = null;

        // checking for first degree of separation
        ArrayList<Integer> user1VINs = new ArrayList<>();
        ArrayList<Integer> user2VINs = new ArrayList<>();
        String query1 = "select Favorites.vin from Favorites where Favorites.login = '"+login1+"';";
        String query2 = "select Favorites.vin from Favorites where Favorites.login = '"+login2+"';";

        try{
            rs = con.stmt.executeQuery(query1);

            // iterate through rs to get vins.

            while (rs.next()) {
                String columnValue = rs.getString(1);
                int vin = rs.getInt("vin");
                user1VINs.add(vin);
            }

            rs = con.stmt.executeQuery(query2);

            while(rs.next()){
                int vin = rs.getInt("vin");
                user2VINs.add(vin);
            }

            ArrayList<Integer> commonVINs = new ArrayList<>(user1VINs);
            commonVINs.retainAll(user2VINs);

            if(commonVINs.size() > 0){
                return true;
            }

        }catch(Exception e){
            System.err.println("Error fetching degrees of separation for users");
        }

        return false;
    }

    /**
     * Checking if a user connects the two users.
     * @param con
     * @param login1
     * @param login2
     * @return
     */
    private static boolean checkForSecondDegree(Connector con, String login1, String login2){

        //select UU.login from UU where UU.login != 'adam' and UU.login != 'adddy';
        String query = "select UU.login from UU where UU.login != '" + login1 + "' and UU.login != '" + login2 + "';";
        ArrayList<String> users = new ArrayList<>();
        ResultSet rs = null;

        try{
            rs = con.stmt.executeQuery(query);

            while (rs.next()) {
                String user = rs.getString("login");
                users.add(user);
            }
        }catch(Exception e){
            System.err.println("Error checknig second degree" + e);
        }


        // now we have a list of users.
        // so, for every user check if it is 1 degree separated from user 1. if it is, check if its 1 degree from 2.

        for(int i = 0; i < users.size(); i++){
            if(checkFirstDegree(con, users.get(i), login1)){
                if(checkFirstDegree(con, users.get(i), login2)){
                    // print out the user who is the second degree of separation.
//                    System.out.println(users.get(i) + " separates " + login1 + " and " + login2);
                    return true;
                }
            }
        }

        return false;
    }

    public static void statsDriver(Connector con, int m){
        ArrayList<String> categories = new ArrayList<>();
        categories.add("SUV");
        categories.add("Sedan");
        categories.add("Truck");
        categories.add("Tesla");
        mMostPopularUC(con, m, categories);
//        mMostExpevsiveUC(con, m, categories);
//        mHighestRatedUD(con, m, categories);
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
    public static void mMostPopularUC(Connector con, int m, ArrayList<String> categories){
        ArrayList<String> mMostPopularUCs = new ArrayList<>();
        System.out.println(m + " most popular UCs by category:");
        ResultSet rs = null;
        try{
            // select uc.vin, Count(ride.rid) as NumberRidesForVin from UC uc, Ride ride where (ride.vin = uc.vin and uc.category = 'SUV')
            // Group by uc.vin order by NumberRidesForVin DESC;
            for(int i = 0; i < categories.size(); i++){
                String query = "select uc.vin, Count(ride.rid) as NumberRidesForVin, uc.login from UC uc, Ride ride where (ride.vin = uc.vin and uc.category = '"
                        + categories.get(i) +"') " +
                        "Group by uc.vin order by NumberRidesForVin DESC;";
                rs = con.stmt.executeQuery(query);
                int index = 0;
                System.out.println("Printing most popular UCs for category: " + categories.get(i));
                while (rs.next() && index < m) {
                    String vin = rs.getString("vin");
                    String num = rs.getString("NumberRidesForVin");
                    String login = rs.getString("login");

                    System.out.println("VIN #: " + vin + "\tNum rides: " + num + "\tDriver: " + login);
                    index++;
                }
            }

        }catch(Exception e){
            System.err.println("Error getting most popular UCs" + e);
        }

    }



    /**
     * most expensive UC's (defined by average cost of all rides on a UC) for each category
     * @param con
     * @param m
     */
    public static void mMostExpevsiveUC(Connector con, int m, ArrayList<String> categories){
        ArrayList<String> mMostExpensiveUCs = new ArrayList<>();
        System.out.println(m + " most expensive UCs by category:");

    }


    /**
     * most highly rated UDs (defined by average scores from all feedbacks a UD has received for all of his UCs)
     * @param con
     * @param m
     */
    public static void mHighestRatedUD(Connector con, int m, ArrayList<String> c){

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
