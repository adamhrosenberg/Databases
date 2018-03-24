package cs5530;

import java.lang.*;
import java.sql.*;
import java.text.ParseException;
import java.io.*;
import java.util.Scanner;

/**
 * ENUM for reading in the resposnes from the user for the login/initial menu.
 */
enum LOGIN_RESPONSES {
	LOGIN(1), REGISTER(2), EXIT(3);

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
	RESERVE(1), NEWUC(2), RECORD(3), FAV(4), FEEDBACK(5), RATE(6), TRUST(7), SEARCH(8), SUGGESTIONS(9), TWODEGREES(
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
		System.out.println("        Welcome to U-Uber System     ");
		System.out.println("1. Login");
		System.out.println("2. Register");
		System.out.println("3. Exit");
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
		System.out.println("9. Get suggestions");
		System.out.println("10. Two Degrees");
		System.out.println("11. Get user stats");
		System.out.println("12. Give user award");
		System.out.println("13. Exit");
		System.out.println("Enter your choice:");
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

			} else if (c == MENU_RESPONSES.SEARCH.getValue()) {

			} else if (c == MENU_RESPONSES.SUGGESTIONS.getValue()) {

			} else if (c == MENU_RESPONSES.TWODEGREES.getValue()) {

			} else if (c == MENU_RESPONSES.STATS.getValue()) {

			} else if (c == MENU_RESPONSES.AWARD.getValue()) {

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

		String time = "";

	}

	/**
	 * Reserve a UC and display the information
	 * 
	 * @param con
	 */
	private static void Reserve(Connector con) {

		String vin = "";
		String pid = "";
		String time = "";
		String cost = "";

		boolean correctVin = false;
		while (!correctVin) {
			vin = promptUserForString("Enter vin of UC that you want to reserve");
			if (!UC.doesThisIdExist(con, vin, "vin", "UC")) {
				System.err.println("There are no car with this Vin. Please enter a new one.");
			} else {
				correctVin = true;
			}
		}
		boolean isAvailable = false;
		while (!isAvailable) {
			time = promptUserForString("Enter time that you want to reserve in the form of HH:MM");
			if (checkTimeFormat(time)) {
				time = time + ":00";
				pid = UD.isDriverAvailable(con, vin, time);
				System.out.println("pid = " + pid);
				if (pid == null) {
					System.err.println(
							"The driver of this car is not available within this time. Please enter a new one.");
				} else {
					isAvailable = true;
				}
			} else {
				System.err.println("Please enter the time in this format \"HH:MM\" \n");
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
			System.out.println("Time: " + time);
			String toReserve = promptUserForString("Are the information correct? y/n");
			if (toReserve.equals("y")) {
				if (UC.Reserve(con, vin, pid, cost, time, user.getLogin())) {
					isReserved = true;
					System.out.println("RESERVED");
				}
			} else if (toReserve.equals("n")) {
				Reserve(con);
				break;
			} else {
				System.err.println("Invalid response please try again");
			}
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
			vin = promptUserForString("Enter vin of UC that you want to reserve");
			if (!UC.doesThisIdExist(con, vin, "vin", "UC")) {
				System.err.println("There are no car with this Vin. Please enter a new one.");
			} else {
				correctVin = true;
			}
		}

		// get date from user
		date = promptUserForString("Enter the date that you want to record the ride (with the form YYYY/MM/DD) :");

		// get from, to time and check if the driver is available using Vin
		boolean isAvailable = false;
		while (!isAvailable) {
			System.out.println("Enter time that you want to record the ride with,");
			from = promptUserForString("From: ");
			to = promptUserForString("To: ");
			pid = UD.isDriverAvailableRide(con, vin, from, to);
			System.out.println("pid = " + pid);
			if (pid == null) {
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
		;

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
				vin = promptUserForString("Enter vin of UC that you want to reserve");
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
				vin = promptUserForString("Enter vin of UC that you want to reserve");
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

			if (UC.addFeedback(con, vin, user.getLogin(), score, text, date)) {
				FeedbackAdded = true;
			}
		}
	}

	/**
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
				trusted = promptUserForString("Is this user trusted? (Enter ture/false):");
				if (!trusted.equals("true") || !trusted.equals("false")) {
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
	 * Prompt user for new UC information. call new UC method in the UC class.
	 * 
	 * @param con
	 */
	private static void manageUC(Connector con) {

		// only drivers can have cars.
		if (!UD.isUserADriver(con, user)) {
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

				// String edit = "";
				// boolean invalidResponse = true;
				// while (invalidResponse) {
				// String categoryResponse = promptUserForString("Select a
				// section to edit: \n1. Category\n2. Comfort\n3. Make\n4.
				// Model");
				//
				// if (categoryResponse.equals("1")) {
				// edit = categorySelect();
				// invalidResponse = false;
				// } else if (categoryResponse.equals("2")) {
				// edit = comfortSelect();
				// invalidResponse = false;
				// } else if (categoryResponse.equals("3")) {
				// boolean CtypeInfo = true;
				// while (CtypeInfo) {
				// edit = promptUserForString("Please enter the make year for
				// your car");
				// if (edit.matches("[0-9]+") && edit.length() == 4) {
				// CtypeInfo = false;
				// } else {
				// System.out.println("Please enter the make year in xxxx
				// format. Please enter a new one.");
				// }
				// }
				//
				// invalidResponse = false;
				// } else if (categoryResponse.equals("4")) {
				// edit = promptUserForString("Please enter the model for your
				// car");
				// invalidResponse = false;
				// } else {
				// System.err.println("Please enter a valid option for the
				// category of car. 1, 2, 3 or 4.");
				// }
				// }

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
				// TODO Auto-generated catch block
				e.printStackTrace();
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
		String address = promptUserForString("Enter address: ");
		String num = promptUserForString("Enter phone number: i.e. 8015555555 ");
		String pass = promptUserForString("Enter password: ");

		UU userToCreate = new UU(login, first, last, address, num, pass);
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
				if (pid.equals("")) {
					pid = Period.getNextPid(connector);
					if (pid.equals("")) {
						pid = "0";
					}
					Period timePeriod = new Period(pid, from, to);
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
