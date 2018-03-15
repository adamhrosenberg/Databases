package cs5530;


import java.lang.*;
import java.sql.*;
import java.io.*;
import java.util.Scanner;

/**
 * ENUM for reading in the resposnes from the user for the login/initial menu.
 */
enum LOGIN_RESPONSES{
    LOGIN(1), REGISTER(2);

    private int value;
    private LOGIN_RESPONSES(int value){
        this.value = value;
    }

    public int getValue(){
        return this.value;
    }
}

/**
 * ENUM for reading in the responses from the user for the logged in menu.
 */
enum MENU_RESPONSES{
    RESERVE(1), NEWUC(2);

    private int value;
    private LOGIN_RESPONSES(int value){
        this.value = value;
    }

    public int getValue(){
        return this.value;
    }
}

public class Driver {

	/**
	 * @param args
	 */

	private static UU user;
	public static void displayInitialMenu()
	{
		 System.out.println("        Welcome to UUber System     ");
    	 System.out.println("1. Login");
    	 System.out.println("2. Register");
    	 System.out.println("3. Exit");
    	 System.out.println("Enter your choice:");
	}

	public static void promptLogin(){

    }
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Connector con=null;
		String choice;
        String cname;
        String dname;
        String sql=null;

        int c=0;
        try
		 {
			//remember to replace the password

			 	 con= new Connector();
	             System.out.println ("Database connection established");
	         
	             BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	             
	             while(true)
	             {
	            	 displayInitialMenu();
	            	 while ((choice = in.readLine()) == null && choice.length() == 0);
	            	 try{
	            		 c = Integer.parseInt(choice);
	            	 }catch (Exception e)
	            	 {
	            		 
	            		 continue;
	            	 }
//	            	 if (c<1 | c>3)
//	            		 continue;
	            	 if (c==LOGIN_RESPONSES.LOGIN.getValue())
	            	 {

	            	 }
	            	 else if (c==LOGIN_RESPONSES.REGISTER.getValue())
	            	 {
	            	     getUserInfo(con.stmt);
	            	 }
	            	 else
	            	 {   
	            		 System.out.println("EoM");
	            		 con.stmt.close(); 
	        
	            		 break;
	            	 }
	             }
		 }
         catch (Exception e)
         {
        	 e.printStackTrace();
        	 System.err.println ("Either connection error or query execution error!");
         }
         finally
         {
        	 if (con != null)
        	 {
        		 try
        		 {
        			 con.closeConnection();
        			 System.out.println ("Database connection terminated");
        		 }
        	 
        		 catch (Exception e) { /* ignore close errors */ }
        	 }	 
         }
	}

	private static void getUserInfo(Statement stmt){
        //create new user to pass into user.createUser. get login, address, etc.
        String login = promptUserForString("Enter username:");

        //TODO make sure username is unique. if not unique call this method again (probs)

        String first = promptUserForString("Enter first name:");
        String last = promptUserForString("Enter last name:");
        String address = promptUserForString("Enter address");
        String num = promptUserForString("Enter phone number: i.e. 8015555555");
        String pass = promptUserForString("Enter password");

    }


    /******
     *  I/O METHODS
     ******/


    /**
     *
     * @param message
     * @return users response
     */
	private static String promptUserForString(String message){
        System.out.println(message);
        Scanner scanner = new Scanner(System.in);
	    return scanner.nextLine();
    }
}


//	            		 System.out.println("please enter your query below:");
//	            		 while ((sql = in.readLine()) == null && sql.length() == 0)
//	            			 System.out.println(sql);
//	            		 ResultSet rs=con.stmt.executeQuery(sql);
//	            		 ResultSetMetaData rsmd = rs.getMetaData();
//	            		 int numCols = rsmd.getColumnCount();
//	            		 while (rs.next())
//	            		 {
//	            			 //System.out.print("cname:");
//	            			 for (int i=1; i<=numCols;i++)
//	            				 System.out.print(rs.getString(i)+"  ");
//	            			 System.out.println("");
//	            		 }
//	            		 System.out.println(" ");
//	            		 rs.close();