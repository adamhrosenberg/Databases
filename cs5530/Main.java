package cs5530;

import java.lang.*;
import java.sql.*;
import java.text.ParseException;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * ENUM for reading in the resposnes from the user for the login/initial menu.
 */
enum LOGIN_RESPONSES {
	LOGIN(1), REGISTER(2), ADMIN_LOGIN(3), EXIT(4);

	private int value;

	private LOGIN_RESPONSES(int value) {
		this.value = value;
	}

	public int getValue() {
		return this.value;
	}
}

/**
 * ENUM for reading in the responses from the user for the logged in menu.
 */
enum MENU_RESPONSES {
	RESERVE(1), NEWUC(2), RECORD(3), FAV(4), FEEDBACK(5), RATE(6), TRUST(7), SEARCH(8), USEFULFEEDBACKS(9), TWODEGREES(
			10), STATS(11), AWARD(12), EXIT(13);

	private int value;

	private MENU_RESPONSES(int value) {
		this.value = value;
	}

	public int getValue() {
		return this.value;
	}
}

public class Main {

	/**
	 * @param args
	 */

	// user that just registered or is logged in.
	private static UU user;

	public static void displayInitialMenu() {
		System.out.println("\n \tWelcome to U-Uber System");
		System.out.println("1. UU Login");
		System.out.println("2. Register");
		System.out.println("3. Admin Login");
		System.out.println("4. Exit ");
		System.out.println("Enter your choice:");
	}

	public static void displayLoggedInMenu() {
		System.out.println("        WELCOME TO U-UBER     ");
		System.out.println("1. Reserve");
		System.out.println("2. Manage UCs");
		System.out.println("3. Record Ride");
		System.out.println("4. Declare a UC as favorite");
		System.out.println("5. Give feedback on UC");
		System.out.println("6. Rate");
		System.out.println("7. Trust another user");
		System.out.println("8. Search for UC");
		System.out.println("9. Get useful feedback");
		System.out.println("10. Determine degrees of separation for 2 users");
		System.out.println("11. Get user stats");
		System.out.println("12. Give user award");
		System.out.println("13. Exit");
		System.out.println("Enter your choice:");
	}

	public static void displayAdminMeun() {
		System.out.println("	Welcome Admin	");
		System.out.println("1. Award user ");
		System.out.println("Enter your choice");
	}

	public static String categorySelect() {
		String category = "";
		boolean invalidResponse = true;
		while (invalidResponse) {
			String categoryResponse = promptUserForString("Select a category: \n1. SUV\n2. Sedan\n3.Truck\n4. Tesla");

			// TODO At some point maybe we should get these categories from the
			// database
			if (categoryResponse.equals("1")) {
				category = "SUV";
				invalidResponse = false;
			} else if (categoryResponse.equals("2")) {
				category = "Sedan";
				invalidResponse = false;
			} else if (categoryResponse.equals("3")) {
				category = "Truck";
				invalidResponse = false;
			} else if (categoryResponse.equals("4")) {
				category = "Tesla";
				invalidResponse = false;
			} else {
				System.err.println("Please enter a valid option for the category of car. 1, 2, 3 or 4.");
			}
		}
		return category;
	}

	public static String comfortSelect() {
		String comfort = "";
		boolean invalidComfortResponse = true;
		while (invalidComfortResponse) {
			String categoryResponse = promptUserForString(
					"Select a comfort category: \n1. Economy\n2. Comfort\n3. Luxury");

			if (categoryResponse.equals("1")) {
				comfort = "Economy";
				invalidComfortResponse = false;
			} else if (categoryResponse.equals("2")) {
				comfort = "Comfort";
				invalidComfortResponse = false;
			} else if (categoryResponse.equals("3")) {
				comfort = "Luxury";
				invalidComfortResponse = false;
			} else {
				System.err.println("Please enter a valid option for the category of car. 1, 2, or 3.");
			}
		}
		return comfort;
	}

	public static void displayUCBrowsingMenu() {
		System.out.println("        PLEASE SELECT SEARCHING CONSTRAINTS     ");

	}

	public static void main(String[] args) {
		// we only want ONE connector in this entire program.
		Connector connector = null;

		try {
			connector = new Connector();
			go(connector);
		} catch (Exception e) {
			System.err.println("Connector error or sql error");
		} finally {
			if (connector != null) {
				try {
					connector.closeConnection();
				}

				catch (Exception e) {
					/* ignore close errors */ }
			}
		}

	}

	public static void promptLogin(Connector con) {

		String login = "";
		String password = "";

		boolean wrongUser = true;
		while (wrongUser) {
			login = promptUserForString("Please enter login:");
			if (!UU.isLoginDuplicate(login, con.stmt)) {
				System.err.println("Invalid username please try again");
			} else {
				wrongUser = false;
			}
		}

		boolean wrongPass = true;
		while (wrongPass) {
			password = promptUserForString(("Please enter password:"));
			UU loggedUser = UU.checkPassword(login, password, con.stmt);

			if (loggedUser == null) {
				System.err.println("Inocrrect password. Try again.");
			} else {
				// user logged in.
				user = loggedUser;
				wrongPass = false;
				login(con);
			}
		}

	}

	public static void go(Connector con) throws IOException, SQLException {
		String choice;
		int c = 0;
		System.out.println("Database connection established");
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

		while (true) {
			displayInitialMenu();
			while ((choice = in.readLine()) == null && choice.length() == 0)
				;
			try {
				c = Integer.parseInt(choice);
			} catch (Exception e) {
				continue;
			}

			if (c == LOGIN_RESPONSES.LOGIN.getValue()) {
				promptLogin(con);
				break;

			} else if (c == LOGIN_RESPONSES.REGISTER.getValue()) {
				getUserInfo(con);
				break;
			} else if (c == LOGIN_RESPONSES.ADMIN_LOGIN.getValue()) {

				break;
			} else if (c == LOGIN_RESPONSES.EXIT.getValue()) {
				closeConnection(con);
				break;
			} else {
				System.err.println("Please enter a valid option");
			}

		}
	}

	private static void closeConnection(Connector con) {
		System.out.println("Closing connection");
		try {
			con.stmt.close();
			System.out.println("Database connection terminated");
		} catch (Exception e) {
			System.err.println("Error closing connection" + e);
		}

	}

	/**
	 * Once user is logged in they are redirected to this menu.
	 * 
	 * @param con
	 * @throws IOException
	 */
	private static void login(Connector con) {
		UU.printUser(user);
		String choice = "";
		int c = 0;
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			displayLoggedInMenu();

			try {
				while ((choice = in.readLine()) == null && choice.length() == 0)
					;
			} catch (Exception e) {
				System.err.println("Error reading input" + e);
			}

			try {
				c = Integer.parseInt(choice);
			} catch (Exception e) {
				continue;
			}

			/**
			 * 
			 * RESERVE(1), NEWUC(2), RECORD(3), FAV(4), FEEDBACK(5), RATE(6),
			 * TRUST(7), SEARCH(8), SUGGESTIONS(9), TWODEGREES(10), STATS(11),
			 * AWARD(12), EXIT(13);
			 * 
			 */
			if (c == MENU_RESPONSES.RESERVE.getValue()) {
				Reserve(con);
			} else if (c == MENU_RESPONSES.NEWUC.getValue()) {
				manageUC(con);
			} else if (c == MENU_RESPONSES.RECORD.getValue()) {
				Rides(con);
			} else if (c == MENU_RESPONSES.FAV.getValue()) {
				Favorites(con);
			} else if (c == MENU_RESPONSES.FEEDBACK.getValue()) {
				Feedback(con);
			} else if (c == MENU_RESPONSES.RATE.getValue()) {
				RateFeedback(con);
			} else if (c == MENU_RESPONSES.TRUST.getValue()) {
				isTrusted(con);
			} else if (c == MENU_RESPONSES.SEARCH.getValue()) {
				UCBrowsing(con); // not done yet
			} else if (c == MENU_RESPONSES.USEFULFEEDBACKS.getValue()) {
				usefulFeedbacks(con);
			} else if (c == MENU_RESPONSES.TWODEGREES.getValue()) {
				degreesOfSeperation(con);
			} else if (c == MENU_RESPONSES.STATS.getValue()) {
                getStats(con);
			} else if (c == MENU_RESPONSES.AWARD.getValue()) {
				System.out.println("REMOVE THIS");
			} else if (c == MENU_RESPONSES.EXIT.getValue()) {
				closeConnection(con);
				break;
			}
		}

	}

	/**
	 * Reserve a UC and display the information
	 * 
	 * @param con
	 */
	private static void UCBrowsing(Connector con) {

		ArrayList<String> category = new ArrayList<String>();
		ArrayList<String> address = new ArrayList<String>();
		ArrayList<String> make = new ArrayList<String>();
		String sort = "";

		boolean isAnd = true;

		boolean inputDone = false;
		while (!inputDone) {
			boolean invalidConstraintResponse = true;
			while (invalidConstraintResponse) {
				String categoryResponse = promptUserForString(
						"Select a search constraint: \n1. Car Category\n2. Address\n3. Model/Make");

				if (categoryResponse.equals("1")) {
					if(isAnd){
						category.add("and");
					} else {
						category.add("or");
					}
					String cat = categorySelect();
					category.add(cat);

				} else if (categoryResponse.equals("2")) {

					if(isAnd){
						address.add("and");
					} else {
						address.add("or");
					}
					
					String Saddress = promptUserForString("Search by: \n1. City\n2. State");
					if (Saddress.equals("1")) {
						address.add("city");
						String city = promptUserForString("Enter city name:");
						address.add(city);
					} else if (Saddress.equals("2")) {
						address.add("state");
						String state = promptUserForString("Enter state abbreviation:");
						address.add(state);
					} else {
						System.err.println("Invalid response please try again");
					}

				} else if (categoryResponse.equals("3")) {

					if(isAnd){
						make.add("and");
					} else {
						make.add("or");
					}
					
					String result = promptUserForString("Search by: \n1. Make\n2. Model");
					if (result.equals("1")) {
						make.add("make");
						String Smake = promptUserForString("Enter Make company:");
						make.add(Smake);
					} else if (result.equals("2")) {
						make.add("model");
						String model = promptUserForString("Enter Model of car:");
						make.add(model);
					} else {
						System.err.println("Invalid response please try again");
					}

				} else {
					System.err.println("");
				}

				// ask if more constraint wanted
				boolean validResponse = false;
				while (!validResponse) {
					String response = promptUserForString("Add more constraints? (y/n):");
					if (response.equals("n")) {
						validResponse = true;
						inputDone = true;
					} else if (response.equals("y")) {
						validResponse = true;
						
						// ask user if wanted to add constraint with and/or
						boolean andOrResponse = false;
						while (!andOrResponse) {
							String andOR = promptUserForString("add constraints with:\n1. And\n2. Or");
							if (andOR.equals("1")) {
								andOrResponse = true;
								isAnd = true;
							} else if (andOR.equals("2")) {
								andOrResponse = true;
								isAnd = false;
							} else {
								System.err.println("Invalid response please try again");
							}
						}
						
					} else {
						System.err.println("Invalid response please try again");
					}
				}
			}

			// asks for sorting method
			boolean validResponse = false;
			while (!validResponse) {
				String feed = promptUserForString(
						"Sort by: \n1. Average score of all feedbacks\n2. Average score of trusted user feedbacks");
				if (feed.equals("1")) {
					sort = "1";
					validResponse = true;
				} else if (feed.equals("2")) {
					sort = "2";
					validResponse = true;
				} else {
					System.err.println("Invalid response please try again");
				}
			}
		}

		// TODO: search
		// still working on how I should add all the constraints together into
		// searching,
		// let me know if you have any suggestions

		ResultSet results = UC.UCBrosing(con, category, address, make, sort);

		int i = 1;
		try {
			if (results == null) {
				System.err.println("No cars with these constraints.");
			}
			while (results.next()) {

				System.out.println(i + ". Car ID: " + results.getString("vin") + " Driver ID: "
						+ results.getString("login") + " Category: " + results.getString("category") + " Comfort: "
						+ results.getString("comfort"));
				i++;
			}
		} catch (SQLException e) {
			System.err.println("Error printing suggestions" + e);
		}

	}


	private static void getStats(Connector con){
	    int m = Integer.parseInt(promptUserForString("Enter number of users you'd like to see stats for (m)"));

	    UU.statsDriver(con, m);
    }

	/**
	 * Reserve a UC and display the information
	 * 
	 * @param con
	 */
	private static void Reserve(Connector con) {

		String vin = "";
		String pid = "";
		String cost = "";
		String from = "";
		String to = "";
		String date = "";

		boolean correctVin = false;
		while (!correctVin) {
			vin = promptUserForString("Enter vin of UC that you want to reserve");
			if (!UC.doesThisIdExist(con, vin, "vin", "UC")) {
				System.err.println("There are no car with this Vin. Please enter a new one.");
			} else {
				correctVin = true;
			}
		}

		// get date from user
		// TODO: need to check date formating
		date = promptUserForString("Enter the date that you want to record the ride (with the form YYYY/MM/DD) :");

		// get from, to time and check if the driver is available using Vin
		boolean isAvailable = false;
		while (!isAvailable) {
			boolean correctTime = false;
			while (!correctTime) {
				System.out.println("Enter time that you want to record the ride with, (0-24)");
				from = promptUserForString("From: ");
				to = promptUserForString("To: ");
				if (Integer.parseInt(from) > Integer.parseInt(to)) {
					System.err.println("From time needs to be less than To time. Please enter a new one.");
				} else if (Integer.parseInt(from) > 24 || Integer.parseInt(from) < 0 || Integer.parseInt(to) > 24
						|| Integer.parseInt(to) < 0) {
					System.err.println("Time inteval needs to be within 0-24. Please enter a new one.");
				} else {
					correctTime = true;
				}
			}
			pid = Period.timeExist(from, to, con.stmt);
			if (pid == null || pid.equals("")) {
				pid = Period.getNextPid(con);
				Period time = new Period(pid, from, to);
				Period.createNewPeriod(time, con.stmt);
			} else {
				isAvailable = true;
			}
		}

		boolean validCost = false;
		while (!validCost) {
			cost = promptUserForString("Enter the amout that you are willing to pay for this trip");
			if (cost.matches("[0-9]+") && cost.length() >= 1) {
				validCost = true;
			} else {
				System.err.println("The amount must only contain numbers. Please enter a new one.");
			}
		}

		boolean isReserved = false;
		while (!isReserved) {
			System.out.println("User: " + user.getLogin());
			System.out.println("VIN: " + vin);
			System.out.println("Cost: " + cost);
			System.out.println("Date: " + date);
			System.out.println("From: " + from);
			System.out.println("To: " + to);
			String toReserve = promptUserForString("Are the information correct? y/n");
			if (toReserve.equals("y")) {
				if (UC.Reserve(con, vin, pid, cost, date, user.getLogin())) {
					isReserved = true;
					System.out.println("RESERVED");
					suggestions(con, vin);
				}
			} else if (toReserve.equals("n")) {
				Reserve(con);
				break;
			} else {
				System.err.println("Invalid response please try again");
			}
		}

	}

	private static void suggestions(Connector con, String vin) {

		// get and display suggestions
		ResultSet results = UC.getSuggestions(con, vin);

		int i = 1;
		try {
			if (results == null) {
				System.err.println("No suggestions.");
			}
			while (results.next()) {

				System.out.println(i + ". Car ID: " + results.getString("vin") + " Driver ID: "
						+ results.getString("login") + " Category: " + results.getString("category") + " Comfort: "
						+ results.getString("comfort"));
				i++;
			}
		} catch (SQLException e) {
			System.err.println("Error printing suggestions" + e);
		}
	}

	/**
	 * Record a ride and display the information
	 * 
	 * @param con
	 */
	private static void Rides(Connector con) {

		String vin = "";
		String pid = "";
		String rid = "";
		String date = "";
		String cost = "";
		String from = "";
		String to = "";

		// get Vin for car and check if it exists
		boolean correctVin = false;
		while (!correctVin) {
			vin = promptUserForString("Enter vin of UC that you want to record ride with");
			if (!UC.doesThisIdExist(con, vin, "vin", "UC")) {
				System.err.println("There are no car with this Vin. Please enter a new one.");
			} else {
				correctVin = true;
			}
		}

		// get date from user
		// TODO: need to check date formating
		date = promptUserForString("Enter the date that you want to record the ride (with the form YYYY/MM/DD) :");

		// get from, to time and check if the driver is available using Vin
		boolean isAvailable = false;
		while (!isAvailable) {
			boolean correctTime = false;
			while (!correctTime) {
				System.out.println("Enter time that you want to record the ride with, (0-24)");
				from = promptUserForString("From: ");
				to = promptUserForString("To: ");
				if (Integer.parseInt(from) > Integer.parseInt(to)) {
					System.err.println("From time needs to be less than To time. Please enter a new one.");
				} else if (Integer.parseInt(from) > 24 || Integer.parseInt(from) < 0 || Integer.parseInt(to) > 24
						|| Integer.parseInt(to) < 0) {
					System.err.println("Time inteval needs to be within 0-24. Please enter a new one.");
				} else {
					correctTime = true;
				}
			}
			pid = UD.isDriverAvailableRide(con, vin, from, to);
			System.out.println("pid = " + pid);
			if (pid == null || pid.equals("")) {
				System.err.println("The driver of this car is not available within this time. Please enter a new one.");
			} else {
				isAvailable = true;
			}
		}

		// get cost from user
		boolean validCost = false;
		while (!validCost) {
			cost = promptUserForString("Enter the amout that you are willing to pay for this trip");
			if (cost.matches("[0-9]+") && cost.length() >= 1) {
				validCost = true;
			} else {
				System.err.println("The amount must only contain numbers. Please enter a new one.");
			}
		}

		// generate new rid
		rid = UC.getNextId(con, "rid", "Ride");

		boolean isReserved = false;
		while (!isReserved) {
			System.out.println("User: " + user.getLogin());
			System.out.println("VIN: " + vin);
			System.out.println("Cost: " + cost);
			System.out.println("Date: " + date);
			System.out.println("From: " + from);
			System.out.println("To: " + to);
			String toReserve = promptUserForString("Are the information correct? y/n");
			if (toReserve.equals("y")) {
				if (UC.recordRides(con, rid, vin, cost, date, user.getLogin(), from, to)) {
					isReserved = true;
					System.out.println("RECORDED");
				}
			} else if (toReserve.equals("n")) {
				Rides(con);
				break;
			} else {
				System.err.println("Invalid response please try again");
			}
		}

	}

	/**
	 * Ask user for vin and date to declare their favorite UC
	 * 
	 * @param con
	 */
	private static void Favorites(Connector con) {

		boolean FavAdded = false;
		while (!FavAdded) {
			String vin = "";
			String date = "";

			// get Vin for car and check if it exists
			boolean correctVin = false;
			while (!correctVin) {
				vin = promptUserForString("Enter vin of car you want to declare as favorite");
				if (!UC.doesThisIdExist(con, vin, "vin", "UC")) {
					System.err.println("There are no car with this Vin. Please enter a new one.");
				} else {
					correctVin = true;
				}
			}

			date = promptUserForString("Enter the date that you took the ride (with the form YYYY/MM/DD) :");

			if (UC.addFav(con, user.getLogin(), vin, date)) {
				FavAdded = true;
			}
		}
	}

	/**
	 * Add Feedback
	 * 
	 * @param con
	 */
	private static void Feedback(Connector con) {

		boolean FeedbackAdded = false;
		while (!FeedbackAdded) {
			String vin = "";
			String date = "";
			String text = "";
			String score = "";

			// get Vin for car and check if it exists
			boolean correctVin = false;
			while (!correctVin) {
				vin = promptUserForString("Enter vin of car you want to give feedback to");
				if (!UC.doesThisIdExist(con, vin, "vin", "UC")) {
					System.err.println("There are no car with this Vin. Please enter a new one.");
				} else {
					correctVin = true;
				}
			}

			boolean correctScore = false;
			while (!correctScore) {
				score = promptUserForString("Enter score (0 = terrible, 10 = excellent):");
				if (score.matches("[0-9]+") && score.length() >= 1) {
					int s = Integer.parseInt(score);
					if (s >= 0 && s <= 10) {
						correctScore = true;
					} else {
						System.err.println("The score needs to be between 0 to 10. Please enter a new one.");
					}

				} else {
					System.err.println("The score needs to be between 0 to 10. Please enter a new one.");
				}
			}

			date = promptUserForString("Enter today's date (with the form YYYY/MM/DD) :");
			text = promptUserForString("Enter the additional comments (Optional):");
			String fid = UC.getNextId(con, "fid", "Feedback");
			if (UC.addFeedback(con, fid, vin, user.getLogin(), score, text, date)) {
				FeedbackAdded = true;
				System.out.println("Feedback added! ID = " + fid);
			}
		}
	}

	/**
	 * Rate feedbacks
	 * 
	 * @param con
	 */
	private static void RateFeedback(Connector con) {

		boolean RatingAdded = false;
		while (!RatingAdded) {
			String fid = "";
			String rating = "";

			// get Vin for car and check if it exists
			boolean correctVin = false;
			while (!correctVin) {
				fid = promptUserForString("Enter fid for Feedback being rated:");
				if (!UC.doesThisIdExist(con, fid, "fid", "Feedback")) {
					System.err.println("There are no car with this Vin. Please enter a new one.");
				} else {
					correctVin = true;
				}
			}

			boolean correctScore = false;
			while (!correctScore) {
				rating = promptUserForString(
						"Enter rating 0, 1, or 2 ('useless', 'useful', 'very useful' respectively):");
				if (rating.matches("[0-2]+") && rating.length() == 1) {
					correctScore = true;

				} else {
					System.err.println("The score needs to be between 0 to 2. Please enter a new one.");
				}
			}

			if (UC.addRating(con, fid, user.getLogin(), rating)) {
				RatingAdded = true;
			}
		}
	}

	/**
	 * rate if the user is trusted or not
	 * 
	 * @param con
	 */
	private static void isTrusted(Connector con) {

		boolean TrustAdded = false;
		while (!TrustAdded) {
			String ratedUser = "";

			// get User for car and check if it exists
			boolean correctUser = false;
			while (!correctUser) {
				ratedUser = promptUserForString("Enter username of User being rated:");
				if (!UU.isLoginDuplicate(ratedUser, con.stmt)) {
					System.err.println("There are user with this username. Please enter a new one.");
				} else {
					correctUser = true;
				}
			}

			String trusted = "";
			boolean validResponse = false;
			while (!validResponse) {
				trusted = promptUserForString("Is this user trusted? (Enter true/false):");
				if (!trusted.equals("true") && !trusted.equals("false")) {
					System.err.println("Invalid response. Please enter a new one.");
				} else {
					validResponse = true;
				}
			}

			if (UU.addTrusted(con, user.getLogin(), ratedUser, trusted)) {
				TrustAdded = true;
			}
		}
	}

	/**
	 * gets top n numbers of most useful feedback from certain driver with
	 * driver login id
	 * 
	 * @param con
	 */
	private static void usefulFeedbacks(Connector con) {

		String login = "";
		boolean validResponse = false;
		while (!validResponse) {
			login = promptUserForString("Enter id(login) for the Driver:");
			if (!UD.isUserADriver(con, login)) {
				System.err.println("No driver with this id. Please enter a new one.");
			} else {
				validResponse = true;
			}
		}

		String numOfFeedbacks = "";
		// get User for car and check if it exists
		boolean validInput = false;
		while (!validInput) {
			numOfFeedbacks = promptUserForString("Enter number of feedbacks wanted:");
			if (numOfFeedbacks.matches("[0-9]+") && numOfFeedbacks.length() >= 1) {
				validInput = true;
			} else {
				System.err.println("There are user with this username. Please enter a new one.");
			}
		}

		// get and display feedback
		ResultSet results = UC.getUsefulFeedbacks(con, login, numOfFeedbacks);

		int i = 1;
		try {
			if (results == null) {
				System.err.println("No feedbacks for this driver.");
			}
			while (results.next()) {
				System.out.println(i + ". ID: " + results.getString("fid") + " Score: " + results.getString("score")
						+ " Comments: " + results.getString("text") + " Date: " + results.getString("date")
						+ " Useful rating (0-2): " + results.getString("avgRates"));
				i++;
			}
		} catch (SQLException e) {
			System.err.println("Error printing useful feedbacks" + e);
		}

	}


	private static void degreesOfSeperation(Connector con){
		String user1 = promptUserForString("Please enter the first login");
		String user2 = promptUserForString("Pleaes enter the second long");

		int degree = UU.usersDegreeOfSeperation(con, user1, user2);

		if(degree == 0){
			System.out.println("Users have no degree of separation.");
		}else if(degree == 1){
			System.out.println("Users are separated by 1 degree");
		}else if(degree == 2){
			System.out.println("Users are separated by 2 degres");

		}
//		if(!UU.isLoginDuplicate(user1, con.stmt)){
//
//		}
	}

	/**
	 * Prompt user for new UC information. call new UC method in the UC class.
	 * 
	 * @param con
	 */
	private static void manageUC(Connector con) {

		// only drivers can have cars.
		if (!UD.isUserADriver(con, user.getLogin())) {
			System.err.println("You must be a driver to be able to manage cars");
			return;
		}

		boolean invalidResponse = true;
		boolean userCreating = false;
		while (invalidResponse) {
			String response = promptUserForString("Enter 1 to create a new UC or enter 2 to " + "edit a current UC");
			if (response.equals("1")) {
				userCreating = true;
				invalidResponse = false;
			} else if (response.equals("2")) {
				userCreating = false; // therefore the user is editing.
				invalidResponse = false;
			} else {
				System.err.println("Please enter a valid option, 1 or 2.");
			}
		}

		if (userCreating) {
			createUC(con);
		} else {
			editUC(con);
		}
	}

	private static void createUC(Connector con) {

		String vin = "";
		boolean incorrectVin = true;
		while (incorrectVin) {
			vin = promptUserForString("Please enter a VIN # for your car");
			if (UC.doesThisIdExist(con, vin, "vin", "UC")) {
				System.err.println("There already exists a car with this vin. Please enter a new one.");
			} else {
				incorrectVin = false;
			}
		}

		String category = categorySelect();
		String comfort = comfortSelect();

		String make = "";
		String model = "";
		boolean CtypeInfo = true;
		while (CtypeInfo) {
			make = promptUserForString("Please enter the make year for your car");
			if (make.matches("[0-9]+") && make.length() == 4) {
				CtypeInfo = false;
			} else {
				System.out.println("Please enter the make year in xxxx format. Please enter a new one.");
			}
		}

		model = promptUserForString("Please enter the model for your car");

		UC car = UC.newUC(con, vin, category, user.getLogin(), comfort, make, model);
		if (car != null) {
			// UC created. print out the details.
			System.out.println("Succesfully added car with these details:");
			UC.printUC(car);
		} else {
			System.err.println("There was en error adding your car");
		}

	}

	private static void editUC(Connector con) {
		// get the UC's a UD has and list them here. ask which one the UD wants
		// to edit.
		// this way we ensure that the UD is only editing UCs that the UD owns.
		System.out.println("List of your car(s)");
		ResultSet results = UC.getUCinfoWithLogin(con, user.getLogin());
		if (results == null) {
			System.out.println("There is no car registerd.");
		} else {
			try {
				int i = 1;
				while (results.next()) {
					ResultSet carRs = UC.getCtypes(con, results.getString("vin"));

					System.out.println(i + ". Vin: " + results.getString("vin") + " Category: "
							+ results.getString("category") + " Comfort: " + results.getString("comfort") + " Make: "
							+ carRs.getString("make") + " Model: " + carRs.getString("model"));
					i++;
				}

				boolean doneEdit = false;
				while (!doneEdit) {
					System.out.println("Please enter the following information.");
					String vin = promptUserForString("Enter vin for car: ");
					String category = promptUserForString("Enter adjusted category: ");
					String comfort = promptUserForString("Enter adjusted comfort: ");
					String make = promptUserForString("Enter adjusted make year: ");
					String model = promptUserForString("Enter adjusted model: ");

					UC car = new UC(vin, category, user.getLogin(), comfort, make, model);
					UC.editUC(con, car);

					boolean validResponse = true;
					while (validResponse) {
						String toReserve = promptUserForString("Edit information for other car? y/n");
						if (toReserve.equals("y")) {
							doneEdit = false;
							validResponse = false;
						} else if (toReserve.equals("n")) {
							doneEdit = true;
							validResponse = false;
						} else {
							System.err.println("Invalid response please try again");
						}
					}
				}

			} catch (SQLException e) {
				System.err.println("Error while editing UC");
			}
		}

	}

	/**
	 * Used for registering a new user.
	 * 
	 * @param connector
	 */
	private static void getUserInfo(Connector connector) throws IOException {
		// create new user to pass into user.createUser. get login, address,
		// etc.

		String login = "";
		boolean duplicate = true;
		while (duplicate) {
			login = promptUserForString("Enter username:");
			if (!user.isLoginDuplicate(login, connector.stmt)) {
				duplicate = false;
			} else {
				System.err.println("ERROR: Login must unique. Enter another username.");
			}
		}

		boolean invalidResponseToDriver = true;
		boolean userIsDriver = false;

		while (invalidResponseToDriver) {
			String driver = promptUserForString("Are you a driver? y/n");
			if (driver.equals("y")) {
				userIsDriver = true;
				invalidResponseToDriver = false;
			} else if (driver.equals("n")) {
				// in the future if we add a isDriver field to the UU, set it to
				// false here. same with above for true.
				invalidResponseToDriver = false;
			} else {
				System.err.println("Invalid response please try again");
			}
		}
		String first = promptUserForString("Enter first name: ");
		String last = promptUserForString("Enter last name: ");
		System.out.println("Enter address:");
		String address = promptUserForString("Enter street: ");
		String city = promptUserForString("Enter city: ");
		String state = promptUserForString("Enter state abbreviation: ");
		String num = promptUserForString("Enter phone number: i.e. 8015555555 ");
		String pass = promptUserForString("Enter password: ");

		UU userToCreate = new UU(login, first, last, address, num, pass, city, state);
		boolean wasCreated = UU.createUser(userToCreate, connector.stmt);

		if (userIsDriver) {
			UD.addToUD(connector, userToCreate);

			// add to time period and Available time for UD
			boolean addAnotherTime = true;
			while (addAnotherTime) {
				System.out.println("Enter time available (0 to 24):");
				String from = promptUserForString("From: ");
				String to = promptUserForString("To: ");

				// check for duplicate time here
				String pid = Period.timeExist(from, to, connector.stmt);
				if (pid.equals("") || pid == null) {
					pid = Period.getNextPid(connector);
					if (pid.equals("")) {
						pid = "0";
					}
					Period timePeriod = new Period(pid, from, to);
					Period.createNewPeriod(timePeriod, connector.stmt);
					UD.addAvailableTime(connector, userToCreate.getLogin(), timePeriod.getPid());
				} else {
					UD.addAvailableTime(connector, userToCreate.getLogin(), pid);
				}

				String driver = promptUserForString("Add another time period? y/n");
				if (driver.equals("y")) {
					addAnotherTime = true;
				} else if (driver.equals("n")) {
					addAnotherTime = false;
				} else {
					System.err.println("Invalid response please try again");
				}
			}

		}

		if (wasCreated) {
			// set driver's user to the user that was created / registered
			user = userToCreate;
			login(connector);
		} else {
			// this error wil have already be caught in UU.java. probably not
			// needed.
			System.err.println("ERROR while registering user. Please try again.");
			// go(connector);
		}
	}

	private static boolean checkTimeFormat(String input) {

		if (input != null) {
			String[] split = input.split(":");
			if (split.length == 2) {
				int hour = Integer.parseInt(split[0]);
				if (hour >= 0 && hour <= 24) {
					int minute = Integer.parseInt(split[1]);
					if (minute >= 0 && minute <= 60) {
						return true;
					} else {
						return false;
					}
				} else {
					return false;
				}

			} else {
				return false;
			}

		}

		return false;
	}

	/******
	 * I/O METHODS
	 ******/

	/**
	 *
	 * @param message
	 * @return users response
	 */
	private static String promptUserForString(String message) {
		System.out.println(message);
		Scanner scanner = new Scanner(System.in);
		return scanner.nextLine();
	}
}
