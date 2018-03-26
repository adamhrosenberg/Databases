package cs5530;

import javax.swing.plaf.metal.MetalComboBoxButton;
import java.lang.*;
import java.sql.*;
import java.util.ArrayList;
import java.io.*;

public class UC {

	private String vin;
	private String login; // foreign key in UU table.
	private String category;
	private String comfort;
	private String make;
	private String model;
	private String year;

	public UC(String vin, String category, String login, String comfort, String make, String model, String year) {
		this.vin = vin;
		this.login = login;
		this.category = category;
		this.comfort = comfort;
		this.make = make;
		this.model = model;
		this.year = year;
	}

	public static UC newUC(Connector con, String vin, String category, String login, String comfort, String make,
			String model, String year) {

		// insert into UC (vin, category, login) values ('42242', 'SUV',
		// 'adam');

		boolean insertsSucessful = true;

		String query = "insert into UC values ('" + vin + "', '" + category + "', '" + login + "', '" + comfort + "');";

		try {
			int result = con.stmt.executeUpdate(query);
			if (result <= 0) {
				// insert not succesful
				System.out.println("insert into UC failed");
				insertsSucessful = false;
			}
		} catch (Exception e) {
			System.err.println("Error adding UC" + e);
		}

		// insert into ctypes and isctypes
		// (could possibly put in a different method same with editUC)

		// Check if the make, model is already in the database
		String tid = checkIfCtypeExist(con, make, model, year);
		if (tid.equals("")) {

			tid = getNextId(con, "tid", "Ctypes");
			if (tid.equals("")) {
				tid = "0";
			}

			// create new Ctypes
			if (insertsSucessful) {
				String CtypesQuery = "insert into Ctypes values ('" + tid + "', '" + make + "', '" + model + "', '"
						+ year + "');";
				try {
					int result = con.stmt.executeUpdate(CtypesQuery);
					if (result <= 0) {
						// insert not succesful
						System.out.println("insert into Ctypes failed");
						insertsSucessful = false;
					}
				} catch (Exception e) {
					System.err.println("Error adding to Ctypes" + e);
				}
			}
		}

		// insert into IsCtypes

		boolean dup = checkIsCtypesDuplicate(con, vin, tid);
		if (insertsSucessful && !dup) {
			String IsCtypesQuery = "insert into IsCtypes values ('" + vin + "', '" + tid + "');";
			try {
				int result = con.stmt.executeUpdate(IsCtypesQuery);
				if (result <= 0) {
					// insert not succesful
					System.out.println("insert into IsCtypes failed");
					insertsSucessful = false;
				} else {
					return new UC(vin, category, login, comfort, make, model, year);
				}
			} catch (Exception e) {
				System.err.println("Error adding to IsCtypes" + e);
			}
		} else if (insertsSucessful && dup) {
			return new UC(vin, category, login, comfort, make, model, year);
		}

		// default return.
		return null;
	}

	/**
	 * Check if the vin already exist
	 * 
	 * @param con
	 * @param vin
	 * @return
	 */
	public static boolean doesThisIdExist(Connector con, String vin, String id, String table) {

		ResultSet rs;

		String query = "select " + id + " from " + table + " where " + id + " ='" + vin + "';";

		try {
			rs = con.stmt.executeQuery(query);
			if (!rs.next()) {
				// not found.
				rs.close();
				return false;
			} else {
				rs.close();
				return true;
			}
		} catch (Exception e) {
			System.err.println("Error checking for vin" + e);
		}
		return false;
	}

	public static UC editUC(Connector con, UC car) {

		boolean insertsSucessful = true;
		String query = "UPDATE UC SET category = '" + car.getCategory() + "', comfort = '" + car.getComfort()
				+ "' WHERE vin = '" + car.getVin() + "';";

		try {
			int result = con.stmt.executeUpdate(query);
			if (result <= 0) {
				// insert not succesful
				System.out.println("editing UC failed");
				insertsSucessful = false;
			}
		} catch (Exception e) {
			System.err.println("Error editing UC" + e);
		}

		// insert into ctypes and isctypes (could possibly put in a different
		// method)

		// Check if the make, model is already in the database
		String tid = checkIfCtypeExist(con, car.getMake(), car.getModel(), car.getYear());
		if (tid.equals("") || tid == null) {

			tid = getNextId(con, "tid", "Ctypes");
			if (tid.equals("")) {
				tid = "0";
			}

			// create new Ctypes
			if (insertsSucessful) {
				String CtypesQuery = "insert into Ctypes values ('" + tid + "', '" + car.getMake() + "', '"
						+ car.getModel() + "', '" + car.getYear() + "');";
				try {
					int result = con.stmt.executeUpdate(CtypesQuery);
					if (result <= 0) {
						// insert not succesful
						System.out.println("insert into Ctypes failed");
						insertsSucessful = false;
					}
				} catch (Exception e) {
					System.err.println("Error adding to Ctypes" + e);
				}
			}
		}

		// insert into IsCtypes
		boolean dup = checkIsCtypesDuplicate(con, car.getVin(), tid);
		boolean dupVin = checkIsCtypesVinExists(con, car.getVin());
		if (insertsSucessful && !dup && !dupVin) {
			String IsCtypesQuery = "insert into IsCtypes values ('" + car.getVin() + "', '" + tid + "');";
			try {
				int result = con.stmt.executeUpdate(IsCtypesQuery);
				if (result <= 0) {
					// insert not succesful
					System.out.println("insert into IsCtypes failed");
					insertsSucessful = false;
				} else {
					return new UC(car.getVin(), car.getCategory(), car.getLogin(), car.getComfort(), car.getMake(),
							car.getModel(), car.getYear());
				}
			} catch (Exception e) {
				System.err.println("Error adding to IsCtypes" + e);
			}
		} else if (insertsSucessful && !dup && dupVin) {
			String IsCtypesQuery = "UPDATE IsCtypes SET tid = '" + tid + "' WHERE vin = '" + car.getVin() + "';";
			try {
				int result = con.stmt.executeUpdate(IsCtypesQuery);
				if (result <= 0) {
					// insert not succesful
					System.out.println("insert into IsCtypes failed");
					insertsSucessful = false;
				} else {
					return new UC(car.getVin(), car.getCategory(), car.getLogin(), car.getComfort(), car.getMake(),
							car.getModel(), car.getYear());
				}
			} catch (Exception e) {
				System.err.println("Error adding to IsCtypes" + e);
			}
		} else if (insertsSucessful && dup && dupVin) {
			return new UC(car.getVin(), car.getCategory(), car.getLogin(), car.getComfort(), car.getMake(),
					car.getModel(), car.getYear());
		}

		return null;
	}

	/**
	 * Using vin to get the login of driver
	 * 
	 * @param con
	 * @param vin
	 * @return
	 */
	public static String getLoginWithVin(Connector con, String vin) {

		ResultSet rs;
		String login = "";

		String query = "select login from UC where vin ='" + vin + "';";

		try {
			rs = con.stmt.executeQuery(query);
			if (!rs.next()) {
				// not found.
				rs.close();
				return login;
			} else {
				login = rs.getString(0);
				rs.close();
				return login;
			}
		} catch (Exception e) {
			System.err.println("Error checking for vin" + e);
		}
		return login;
	}

	/**
	 * Using login of user to get the list of UC with all the information
	 * 
	 * @param con
	 * @param vin
	 * @return
	 */
	public static ArrayList<String> getUCinfoWithLogin(Connector con, String login) {

		ResultSet results;
		ArrayList<String> result = new ArrayList<String>();

		String query = "select * from UC where login ='" + login + "';";

		try {
			results = con.stmt.executeQuery(query);
			// ResultSetMetaData metaData = rs.getMetaData();
			while (results.next()) {
				result.add(results.getString("vin"));
				result.add(results.getString("category"));
				result.add(results.getString("comfort"));
			}
			return result;
		} catch (Exception e) {
			System.err.println("Error checking for vin" + e);
		}
		return null;
	}

	/**
	 * Using vin to get make and model of certain car
	 * 
	 * @param con
	 * @param vin
	 * @return
	 */
	public static ArrayList<String> getCtypes(Connector con, String vin) {

		ResultSet results;

		String query = "select * from Ctypes type where type.tid = (select isType.tid from IsCtypes isType where vin = '"
				+ vin + "');";
		ArrayList<String> result = new ArrayList<String>();
		try {
			results = con.stmt.executeQuery(query);
			// ResultSetMetaData metaData = rs.getMetaData();
			while (results.next()) {
				result.add(results.getString("make"));
				result.add(results.getString("model"));
				result.add(results.getString("year"));
			}
			return result;
		} catch (Exception e) {
			System.err.println("Error checking for vin" + e);
		}
		return null;
	}

	/**
	 * record a reservation to a UC
	 * 
	 * @param con
	 * @param vin
	 * @param pid
	 * @param cost
	 * @param time
	 * @param numOfPpl
	 * @param destination
	 * @param user
	 * @return
	 */
	public static boolean Reserve(Connector con, String vin, String pid, String cost, String time, String login,
			String destination, String numOfPpl) {

		String query = "insert into Reserve values ('" + pid + "', '" + vin + "', '" + login + "', '" + cost + "', '"
				+ time + "', '" + destination + "', '" + numOfPpl + "');";

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

	public static ResultSet getSuggestions(Connector con, String vin) {
		ResultSet rs;

		String query = "select * from UC car where car.vin in (select r1.vin from Reserve r1, Reserve r2 "
				+ "where r1.login = r2.login and r1.vin = '" + vin + "' and r2.vin != '" + vin + "') "
				+ "and car.vin != '" + vin + "' group by car.vin ORDER BY count(*) desc;";

		try {
			rs = con.stmt.executeQuery(query);
			// ResultSetMetaData metaData = rs.getMetaData();
			if (!rs.next()) {
				// not found.
				rs.close();
				return null;
			} else {
				rs.close();
				return rs;
			}
		} catch (Exception e) {
			System.err.println("Error getting useful feedbacks" + e);
		}
		return null;
	}

	/**
	 * record a ride to a UC
	 * 
	 * @param con
	 * @param vin
	 * @param pid
	 * @param cost
	 * @param numOfPpl
	 * @param destination
	 * @param time
	 * @param user
	 * @return
	 */
	public static boolean recordRides(Connector con, String rid, String vin, String cost, String date, String login,
			String from, String to, String destination, String numOfPpl) {

		String query = "insert into Ride values ('" + rid + "', '" + vin + "', '" + cost + "', '" + date + "', '"
				+ login + "', '" + from + "', '" + to + "', '" + numOfPpl + "', '" + destination + "');";

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

	public static ResultSet UCBrosing(Connector con, ArrayList<String> category, ArrayList<String> address,
			ArrayList<String> make, String sort) {
		ResultSet rs;

		boolean first = true;

		String query = "";
		if (sort != null && sort.equals("1")) {
			query = "select car.vin, car.login, car.category, car.comfort, avg(feed.score) as score "
					+ "from UC car, Feedback feed where car.vin = feed.vin";
		} else if (sort != null && sort.equals("2")) {
			query = "select car.vin, car.login, car.category, car.comfort, avg(feed.score) as score "
					+ "from UC car, Feedback feed Trust trust where car.vin = feed.vin and trust.isTrusted = '1'";
		}

		for (int i = 0; i < category.size(); i += 2) {
			if (category.get(i).equals("or") && !first) {
				query = query + " or car.category = '" + category.get(i + 1) + "'";
			} else {
				query = query + " and car.category = '" + category.get(i + 1) + "'";
				first = false;
			}
		}

		if (address.size() >= 3) {
			for (int i = 0; i < address.size(); i += 3) {
				if (address.get(i).equals("or") && i == 0 && !first) {
					query = query + " or car.login in (select u.login from UU u where u." + address.get(i + 1) + " = '"
							+ address.get(i + 2) + "'";
				} else if (first && i == 0) {
					query = query + " and car.login in (select u.login from UU u where u." + address.get(i + 1) + " = '"
							+ address.get(i + 2) + "'";
					first = false;
				} else if (address.get(i).equals("or") && i != 0) {
					query = query + " or u." + address.get(i + 1) + " = '" + address.get(i + 2) + "'";
				} else if (address.get(i).equals("and") && i != 0) {
					query = query + " and u." + address.get(i + 1) + " = '" + address.get(i + 2) + "'";
				}
			}

			query = query + ")";
		}
		if (make.size() >= 3) {
			for (int i = 0; i < make.size(); i += 3) {
				if (make.get(i).equals("or") && i == 0 && !first) {
					query = query + " or car.vin in (select vin from IsCtypes it, Ctypes T where it.tid = T.tid and T."
							+ make.get(i + 1) + " = '" + make.get(i + 2) + "'";
				} else if (first && i == 0) {
					query = query + " and car.vin in (select vin from IsCtypes it, Ctypes T where it.tid = T.tid and T."
							+ make.get(i + 1) + " = '" + make.get(i + 2) + "'";
				} else if (make.get(i).equals("or") && i != 0) {
					query = query + " or T." + make.get(i + 1) + " = '" + make.get(i + 2) + "'";
				} else if (make.get(i).equals("and") && i != 0) {
					query = query + " and T." + make.get(i + 1) + " = '" + make.get(i + 2) + "'";
				}
			}

			query = query + ")";
		}

		query = query + " group by car.vin ORDER BY score desc;";

		try {
			rs = con.stmt.executeQuery(query);
			// ResultSetMetaData metaData = rs.getMetaData();
			if (!rs.next()) {
				// not found.
				rs.close();
				return null;
			} else {
				rs.close();
				return rs;
			}
		} catch (Exception e) {
			System.err.println("Error getting useful feedbacks" + e);
		}
		return null;
	}

	/**
	 * Add favorites with UC and Vin
	 * 
	 * @return
	 */
	public static boolean addFav(Connector con, String login, String vin, String date) {

		String query = "insert into Favorites values ('" + vin + "', '" + login + "', '" + date + "');";

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
	 * Add new feedback into Feedback table
	 * 
	 * @param con
	 * @param fid
	 * @param vin
	 * @param login
	 * @param score
	 * @param text
	 * @param date
	 * @return
	 */
	public static boolean addFeedback(Connector con, String fid, String vin, String login, String score, String text,
			String date) {

		String query = "insert into Feedback values ('" + fid + "', '" + vin + "', '" + login + "', '" + score + "', '"
				+ text + "', '" + date + "');";

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
	 * @param con
	 * @param fid
	 * @param login
	 * @param score
	 * @return
	 */
	public static boolean addRating(Connector con, String fid, String login, String score) {

		String query = "insert into Rates values ('" + fid + "', '" + login + "', '" + score + "');";

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

	public static ArrayList<String> getUsefulFeedbacks(Connector con, String login, String count) {

		ResultSet results;
		ArrayList<String> result = new ArrayList<String>();

		String query = "select f.fid, f.vin, f.login, f.score, f.text, f.date, AVG (r.rating) "
				+ "as avgRates from Feedback f, Rates r where f.vin in "
				+ "(select vin from UC c, UD d where c.login=d.UU_login and d.UU_login = '" + login + "') "
				+ "and f.fid=r.fid group by f.fid ORDER BY avgRates DESC limit " + count + ";";

		try {
			results = con.stmt.executeQuery(query);
			while (results.next()) {
				result.add(results.getString("fid"));
				result.add(results.getString("vin"));
				result.add(results.getString("login"));
				result.add(results.getString("date"));
				result.add(results.getString("score"));
				result.add(results.getString("text"));
				result.add(results.getString("avgRates"));
			}
			return result;
		} catch (Exception e) {
			System.err.println("Error getting useful feedbacks" + e);
		}
		return null;
		
	}

	/**
	 * Using vin to get the login of driver
	 * 
	 * @param con
	 * @param vin
	 * @return
	 */
	public static String checkIfCtypeExist(Connector con, String make, String model, String year) {

		ResultSet rs;
		String tid = "";
		String query = "select tid from Ctypes where make = '" + make + "' and model = '" + model + "' and year = '"
				+ year + "';";

		try {
			rs = con.stmt.executeQuery(query);
			if (!rs.next()) {
				// not found.
				rs.close();
				return tid;
			} else {
				tid = rs.getString(1);
				rs.close();
				return tid;
			}
		} catch (Exception e) {
			System.err.println("Error checking for vin" + e);
		}
		return tid;
	}

	/**
	 * Checks if this vin is already connected with this tid
	 * 
	 * @param con
	 * @param vin
	 * @return
	 */
	public static boolean checkIsCtypesDuplicate(Connector con, String vin, String tid) {

		ResultSet rs;
		String query = "select * from IsCtypes where vin ='" + vin + "' and tid = '" + tid + "';";

		try {
			rs = con.stmt.executeQuery(query);
			if (!rs.next()) {
				// not found.
				rs.close();
				return false;
			} else {
				rs.close();
				return true;
			}
		} catch (Exception e) {
			System.err.println("Error checking for vin" + e);
		}
		return false;
	}

	/**
	 * Checks if this vin is already connected with a tid
	 * 
	 * @param con
	 * @param vin
	 * @return
	 */
	public static boolean checkIsCtypesVinExists(Connector con, String vin) {

		ResultSet rs;
		String query = "select * from IsCtypes where vin ='" + vin + "';";

		try {
			rs = con.stmt.executeQuery(query);
			if (!rs.next()) {
				// not found.
				rs.close();
				return false;
			} else {
				rs.close();
				return true;
			}
		} catch (Exception e) {
			System.err.println("Error checking for vin" + e);
		}
		return false;
	}

	/**
	 * Generates new ids for different tables
	 * 
	 * @param con
	 * @return
	 */
	public static String getNextId(Connector con, String id, String table) {
		ResultSet rs;
		String fid = "";
		String query = "select max(" + id + ") from " + table + ";";

		try {
			rs = con.stmt.executeQuery(query);
			if (!rs.next()) {
				// no pid
				rs.close();
				return fid;
			} else {
				fid = rs.getString(1);
				if (fid == null) {
					rs.close();
					return "0";
				} else {
					int ret = Integer.parseInt(fid) + 1;
					rs.close();
					return (ret + "");
				}

			}

		} catch (Exception e) {
			System.err.println("Error while getting next Rid. " + e);
		}
		return fid;
	}

	/**
	 * GETTER FUNCTIONS.
	 */

	public String getVin() {
		return this.vin;
	}

	public String getLogin() {
		return this.login;
	}

	public String getCategory() {
		return this.category;
	}

	public String getComfort() {
		return this.comfort;
	}

	public String getMake() {
		return this.make;
	}

	public String getModel() {
		return this.model;
	}

	public String getYear() {
		return this.year;
	}

	public static void printUC(UC car) {
		System.out.println("***CAR INFORMATION***\nvin: " + car.getVin() + "\ncategory: " + car.getCategory()
				+ "\nowner: " + car.getLogin() + "\nComfort: " + car.getComfort() + "\nYear: " + car.getYear()
				+ "\nMake: " + car.getMake() + "\nModel: " + car.getModel() + "\n");
	}

}
