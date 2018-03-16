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
    RESERVE(1), NEWUC(2), RECORD(3), FAV(4), FEEDBACK(5), RATE(6), TRUST(7), SEARCH(8), SUGGESTIONS(9),
    TWODEGREES(10), STATS(11), AWARD(12), EXIT(13);

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

	// user that just registered or is logged in.
	private static UU user;
	public static void displayInitialMenu()
	{
		 System.out.println("        Welcome to U-Uber System     ");
    	 System.out.println("1. Login");
    	 System.out.println("2. Register");
    	 System.out.println("3. Exit");
    	 System.out.println("Enter your choice:");
	}

    public static void displayLoggedInMenu(){
        System.out.println("        WELCOME TO U-UBER     ");
        System.out.println("1. Reserve");
        System.out.println("2. Manage UCs");
        System.out.println("3. Record Ride");
        System.out.println("4. Declare a UC as favorite");
        System.out.println("5. Give feedback on UC");
        System.out.println("6. Rate");
        System.out.println("7. Trust another user");
        System.out.println("8. Search for UC");
        System.out.println("9. Get suggestions");
        System.out.println("10. Two Degrees");
        System.out.println("11. Get user stats");
        System.out.println("12. Give user award");
        System.out.println("13. Exit");
        System.out.println("Enter your choice:");
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
                }

                catch (Exception e) { /* ignore close errors */ }
            }
        }


	}

    public static void promptLogin(Connector con){

	    String login = "";
	    String password = "";

	    boolean wrongUser = true;
	    while(wrongUser){
            login = promptUserForString("Please enter login:");
            if(!UU.isLoginDuplicate(login, con.stmt)){
                System.err.println("Invalid username please try again");
            }else{
                wrongUser = false;
            }
        }

        boolean wrongPass = true;
	    while(wrongPass){
            password = promptUserForString(("Please enter password:"));
            UU loggedUser = UU.checkPassword(login, password, con.stmt);

            if(loggedUser == null){
                System.err.println("Inocrrect password. Try again.");
            }else{
                //user logged in.
                user = loggedUser;
                wrongPass = false;
                login(con);
            }
        }

    }


    public static void go(Connector con) throws IOException, SQLException{
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
                    promptLogin(con);
                    break;

                }
                else if (c==LOGIN_RESPONSES.REGISTER.getValue())
                {
                    getUserInfo(con);
                    break;
                }
                else if (c==LOGIN_RESPONSES.EXIT.getValue()){
                    closeConnection(con);
                    break;
                }
                else{
                    System.err.println("Please enter a valid option");
                }

            }
	}


	private static void closeConnection(Connector con){
        System.out.println("Closing connection");
        try{
            con.stmt.close();
            System.out.println ("Database connection terminated");
        }catch (Exception e){
            System.err.println("Error closing connection" + e);
        }

    }


    /**
     * Once user is logged in theyre redirected to this menu.
     * @param con
     * @throws IOException
     */
    private static void login(Connector con){
        UU.printUser(user);
        String choice = "";
        int c=0;
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        while(true){
            displayLoggedInMenu();

            try{
                while ((choice = in.readLine()) == null && choice.length() == 0);
            }catch(Exception e){
                System.err.println("Error reading input" + e);
            }

            try{
                c = Integer.parseInt(choice);
            }catch (Exception e)
            {
                continue;
            }

            /**

             RESERVE(1), NEWUC(2), RECORD(3), FAV(4), FEEDBACK(5), RATE(6),
             TRUST(7), SEARCH(8), SUGGESTIONS(9),
             TWODEGREES(10), STATS(11), AWARD(12), EXIT(13);

             */
            if(c == MENU_RESPONSES.RESERVE.getValue()){

            }else if(c == MENU_RESPONSES.NEWUC.getValue()){
                manageUC(con);
            }else if(c == MENU_RESPONSES.RECORD.getValue()){

                break;
            }else if(c == MENU_RESPONSES.FAV.getValue()){

            }else if(c == MENU_RESPONSES.FEEDBACK.getValue()){

            }else if(c == MENU_RESPONSES.RATE.getValue()){

            }else if(c == MENU_RESPONSES.TRUST.getValue()){

            }else if(c == MENU_RESPONSES.SEARCH.getValue()){

            }else if(c == MENU_RESPONSES.SUGGESTIONS.getValue()){

            }else if(c == MENU_RESPONSES.TWODEGREES.getValue()){

            }else if(c == MENU_RESPONSES.STATS.getValue()){

            }else if(c == MENU_RESPONSES.AWARD.getValue()){

            }else if(c == MENU_RESPONSES.EXIT.getValue()){
                closeConnection(con);
                break;
            }
        }


    }

    /**
     * Prompt user for new UC information. call new UC method in the UC class.
     * @param con
     */
    private static void manageUC(Connector con){

        // only drivers can have cars.
        if(!UD.isUserADriver(con, user)){
            System.err.println("You must be a driver to be able to manage cars");
            return;
        }

        boolean invalidResponse = true;
        boolean userCreating = false;
        while(invalidResponse){
            String response = promptUserForString("Enter 1 to create a new UC or enter 2 to " +
                    "edit a current UC");
            if(response.equals("1")){
                userCreating = true;
                invalidResponse = false;
            }else if(response.equals("2")){
                userCreating = false; //therefore the user is editing.
                invalidResponse = false;
            }else{
                System.err.println("Please enter a valid option, 1 or 2.");
            }
        }

        if(userCreating){
            createUC(con);
        }else{
            editUC(con);
        }
    }


    private static void createUC(Connector con){

    }

    private static void editUC(Connector con){

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
                System.err.println("ERROR: Login must unique. Enter another username.");
            }
        }

        boolean invalidResponseToDriver = true;
        boolean userIsDriver = false;

        while(invalidResponseToDriver){
            String driver = promptUserForString("Are you a driver? y/n");
            if(driver.equals("y")){
                userIsDriver = true;
                invalidResponseToDriver = false;
            }else if(driver.equals("n")){
                // in the future if we add a isDriver field to the UU, set it to false here. same with above for true.
                invalidResponseToDriver = false;
            }else{
                System.err.println("Invalid response please try again");
            }
        }
        String first = promptUserForString("Enter first name: ");
        String last = promptUserForString("Enter last name: ");
        String address = promptUserForString("Enter address: ");
        String num = promptUserForString("Enter phone number: i.e. 8015555555 ");
        String pass = promptUserForString("Enter password: ");

        UU userToCreate = new UU(login, first, last, address, num, pass);
        boolean wasCreated = UU.createUser(userToCreate, connector.stmt);

        if(userIsDriver){
            UD.addToUD(connector, userToCreate);
        }

        if(wasCreated){
            // set driver's user to the user that was created / registered
            user = userToCreate;
            login(connector);
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

