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

        ResultSet rs;
        String query = "select UU_login from UD where UU_login = '" + user.getLogin() + "';";

        try{
            rs = con.stmt.executeQuery(query);

            if(!rs.next()){
                //not found.
                rs.close();
                return false;
            }else{
                rs.close();
                return true;
            }
        }catch (Exception e) {
            System.err.println("Error checking if user is in UD" + e);
        }
        return false;
    }
}
