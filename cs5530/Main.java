package cs5530;


import java.lang.*;
import java.sql.*;
import java.io.*;
import java.util.Scanner;

/**
 * ENUM for reading in the resposnes from the user for the login/initial menu.
 */
enum LOGIN_RESPONSES{
    LOGIN(1), REGISTER(2), EXIT(3);

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
    private MENU_RESPONSES(int value){
        this.value = value;
    }

    public int getValue(){
        return this.value;
    }
}

public class Main {

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

    public static void displayLoggedInMenu(){
        System.out.println("        USER LOGGED IN     ");
        System.out.println("1. Reserve");
        System.out.println("2. New UC");
        System.out.println("3. New UC");
        System.out.println("4. Exit");
        System.out.println("Enter your choice:");
    }
	public static void promptLogin(){

    }
	
	public static void main(String[] args) {
	    //we only want ONE connector in this entire program.
        Connector connector = null;

        try{
            connector = new Connector();
            go(connector);
        }catch(Exception e){
            System.err.println("Connector error or sql error");
        }finally
        {
            if (connector != null)
            {
                try
                {
                    connector.closeConnection();
                    System.out.println ("Database connection terminated");
                }

                catch (Exception e) { /* ignore close errors */ }
            }
        }


	}

	public static void go(Connector con) throws IOException, SQLException{
        // TODO Auto-generated method stub
            String choice;


            int c=0;


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

                if (c==LOGIN_RESPONSES.LOGIN.getValue())
                {
                    login(con.stmt);

                }
                else if (c==LOGIN_RESPONSES.REGISTER.getValue())
                {
                    getUserInfo(con);
                    break;
                }
                else if (c==LOGIN_RESPONSES.EXIT.getValue()){
                    System.out.println("Closing connection");
                    con.stmt.close();
                    break;
                }
                else{
                    System.err.println("Please enter a valid option");
                }

            }
	}


    private static void login(Statement stmt) throws IOException{
        String choice;
        int c=0;
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        while(true){
            displayLoggedInMenu();

            while ((choice = in.readLine()) == null && choice.length() == 0);

            try{
                c = Integer.parseInt(choice);
            }catch (Exception e)
            {
                continue;
            }

            if(c == MENU_RESPONSES.RESERVE.getValue()){

                break;
            }else if(c == MENU_RESPONSES.NEWUC.getValue()){

                break;
            }
        }


    }


    /**
     * Used for registering a new user.
     * @param connector
     */
	private static void getUserInfo(Connector connector) throws IOException{
        //create new user to pass into user.createUser. get login, address, etc.

        String login = "";
        boolean duplicate = true;
        while(duplicate){
            login = promptUserForString("Enter username:");
            if(!user.isLoginDuplicate(login, connector.stmt)){
                duplicate = false;
            }else{
                System.out.println("ERROR: Login must unique. Enter another username.");

            }
        }

        String first = promptUserForString("Enter first name: ");
        String last = promptUserForString("Enter last name: ");
        String address = promptUserForString("Enter address: ");
        String num = promptUserForString("Enter phone number: i.e. 8015555555 ");
        String pass = promptUserForString("Enter password: ");

        UU user = new UU(login, first, last, address, num, pass);
        boolean wasCreated = UU.createUser(user, connector.stmt);

        if(wasCreated){
            login(connector.stmt);
        }else{
            //this error wil have already be caught in UU.java. probably not needed.
            System.err.println("ERROR while registering user. Please try again.");
//            go(connector);
        }
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

