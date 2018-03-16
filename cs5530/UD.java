package cs5530;
import java.lang.*;
import java.sql.*;
import java.io.*;


public class UD {


    public static boolean addToUD(Connector con, UU user){

        // TODO may need to add first / last name to UD table to handle names. not sure if needed though.

        String query = "insert into UD values ('" + user.getLogin() + "', '" + user.getFirst() + "', '0x000');";
        try{
            int result  = con.stmt.executeUpdate(query);

            if(result > 0){
                //insert worked
                return true;
            }else{
                return false;
            }
        }catch (Exception e){
            System.err.println("Error adding to UD table. " + e);
        }

        return true;
    }


    public static boolean isUserADriver(Connector con, UU user){
        // should be very similar to isLoginDuplicate in UU.java

        return false;
    }
}
