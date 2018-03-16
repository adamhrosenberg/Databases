package cs5530;

import javax.swing.plaf.metal.MetalComboBoxButton;
import java.lang.*;
import java.sql.*;
import java.io.*;



public class UC {

    private String vin;
    private String login; //foreign key in UU table.
    private String category;

    public UC(String vin, String category, String login){
        this.vin = vin;
        this.login = login;
        this.category = category;
    }

    public static UC newUC(Connector con, String vin, String category, String login){

        //insert into UC (vin, category, login) values ('42242', 'SUV', 'adam');

        String query = "insert into UC (vin, category, login) values ('" + vin + "', '" +
                category + "', '" + login + "');";

        try{
            int result = con.stmt.executeUpdate(query);
            if(result > 0){
                //insert succesful
                return new UC(vin, category, login);
            }else{
                // insert not succesful
                return null;
            }
        }catch (Exception e){
            System.err.println("Error adding UC" + e);
        }

        //default return.
        return null;
    }

    public static boolean doesThisVinExist(Connector con, String vin){

        ResultSet rs;

        String query = "select vin from UC where vin ='" + vin + "';";

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
        } catch (Exception e){
            System.err.println("Error checking for vin" + e);
        }
        return false;
    }

    public static UC editUC(Connector con, UC car){


        return null;
    }


    /**
     * GETTER FUNCTIONS.
     */

    public String getVin(){
        return this.vin;
    }

    public String getLogin(){
        return this.login;
    }

    public String getCategory(){
        return this.category;
    }

    public static void printUC(UC car){
        System.out.println("***CAR INFORMATION***\nvin: " + car.getVin() +
        "\ncategory: " + car.getCategory() + "\nowner: " + car.getLogin());
    }
}
