import java.lang.*;
import java.sql.*;
import java.io.*;

package cs5530;

public class UU {

     private String login;
     private String first;
     private String last;
     private String address;
     private String num;
     private String pass;

     public UU(String login, String first, String last, String address, String num, String pass){
        this.login = login;
        this.first = first;
        this.last = last;
        this.address = address;
        this.pass = pass;
        this.num = num;
     }


    /**
     * Getter functions for private variables
     */

    public getLogin(){
        return this.login;
    }

    public getFirst(){
        return this.first;
    }

    public getLast(){
        return this.last;
    }

    public getAddress(){
        return this.address;
    }

    public getNum(){
        return this.num;
    }
}
