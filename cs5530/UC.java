package cs5530;

import javax.swing.plaf.metal.MetalComboBoxButton;
import java.lang.*;
import java.sql.*;
import java.io.*;

public class UC {

	private String vin;
	private String login; // foreign key in UU table.
	private String category;
	private String comfort;
	private String make;
	private String model;

	public UC(String vin, String category, String login, String comfort, String make, String model) {
		this.vin = vin;
		this.login = login;
		this.category = category;
		this.comfort = comfort;
		this.make = make;
		this.model = model;
	}

	public static UC newUC(Connector con, String vin, String category, String login, String comfort, String make,
			String model) {

		// insert into UC (vin, category, login) values ('42242', 'SUV',
		// 'adam');

		boolean insertsSucessful = true;

		String query = "insert into UC (vin, category, login) values ('" + vin + "', '" + category + "', '" + login
				+ "');";

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
		String tid = checkIfCtypeExist(con, make, model);
		if (tid.equals("")) {

			tid = getNextId(con, "tid", "Period");
			if (tid.equals("")) {
				tid = "0";
			}

			// create new Ctypes
			if (insertsSucessful) {
				String CtypesQuery = "insert into Ctypes values ('" + tid + "', '" + make + "', '" + model + "');";
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
					return new UC(vin, category, login, comfort, make, model);
				}
			} catch (Exception e) {
				System.err.println("Error adding to IsCtypes" + e);
			}
		} else if (insertsSucessful && dup) {
			return new UC(vin, category, login, comfort, make, model);
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
		String tid = checkIfCtypeExist(con, car.getMake(), car.getModel());
		if (tid.equals("")) {

			tid = getNextId(con, "tid", "Period");
			if (tid.equals("")) {
				tid = "0";
			}

			// create new Ctypes
			if (insertsSucessful) {
				String CtypesQuery = "insert into Ctypes values ('" + tid + "', '" + car.getMake() + "', '"
						+ car.getModel() + "');";
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
		if (insertsSucessful && !dup) {
			String IsCtypesQuery = "insert into IsCtypes values ('" + car.getVin() + "', '" + tid + "');";
			try {
				int result = con.stmt.executeUpdate(IsCtypesQuery);
				if (result <= 0) {
					// insert not succesful
					System.out.println("insert into IsCtypes failed");
					insertsSucessful = false;
				} else {
					return new UC(car.getVin(), car.getCategory(), car.getLogin(), car.getComfort(), car.getMake(),
							car.getModel());
				}
			} catch (Exception e) {
				System.err.println("Error adding to IsCtypes" + e);
			}
		} else if (insertsSucessful && dup) {
			return new UC(car.getVin(), car.getCategory(), car.getLogin(), car.getComfort(), car.getMake(),
					car.getModel());
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
	public static ResultSet getUCinfoWithLogin(Connector con, String login) {

		ResultSet rs;

		String query = "select * from UC where login ='" + login + "';";

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
	public static ResultSet getCtypes(Connector con, String vin) {

		ResultSet rs;

		String query = "select * from Ctypes type where type.tid = (select isType.tid from IsCtypes where vin = '" + vin
				+ "';";

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
	 * @param user
	 * @return
	 */
	public static boolean Reserve(Connector con, String vin, String pid, String cost, String time, String login) {

		String query = "insert into Reserve values ('" + pid + "', '" + vin + "', '" + login + "', '" + cost + "', '"
				+ time + "');";

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
	 * record a ride to a UC
	 * 
	 * @param con
	 * @param vin
	 * @param pid
	 * @param cost
	 * @param time
	 * @param user
	 * @return
	 */
	public static boolean recordRides(Connector con, String rid, String vin, String cost, String date, String login,
			String from, String to) {

		String query = "insert into Reserve values ('" + rid + "', '" + vin + "', '" + login + "', '" + cost + "', '"
				+ date + "', '" + from + "', '" + to + "');";

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
	 * Add favorites with UC and Vin
	 * 
	 * @return
	 */
	public static boolean addFav(Connector con, String login, String vin, String date) {

		String query = "insert into Favorites values ('" + login + "', '" + vin + "', '" + date + "');";

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
	public static boolean addFeedback(Connector con, String vin, String login, String score, String text, String date) {

		String fid = getNextId(con, "fid", "Feedback");
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

	/**
	 * Using vin to get the login of driver
	 * 
	 * @param con
	 * @param vin
	 * @return
	 */
	public static String checkIfCtypeExist(Connector con, String make, String model) {

		ResultSet rs;
		String tid = "";
		String query = "select tid from Ctypes where make ='" + make + "' and model = '" + model + ";";

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
	 * Using vin to get the login of driver
	 * 
	 * @param con
	 * @param vin
	 * @return
	 */
	public static boolean checkIsCtypesDuplicate(Connector con, String vin, String tid) {

		ResultSet rs;
		String query = "select * from Ctypes where vin ='" + vin + "' and tid = '" + tid + ";";

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

	public static void printUC(UC car) {
		System.out.println("***CAR INFORMATION***\nvin: " + car.getVin() + "\ncategory: " + car.getCategory()
				+ "\nowner: " + car.getLogin());
	}
}
