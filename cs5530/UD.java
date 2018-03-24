package cs5530;

import java.lang.*;
import java.sql.*;
import java.util.Formatter;
import java.io.*;

public class UD {

	public static boolean addToUD(Connector con, UU user) {

		// TODO may need to add first / last name to UD table to handle names.
		// not sure if needed though.

		String query = "insert into UD values ('" + user.getLogin() + "', '" + user.getFirst() + "', '0x000');";
		try {
			int result = con.stmt.executeUpdate(query);

			if (result > 0) {
				// insert worked
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			System.err.println("Error adding to UD table. " + e);
		}

		return true;
	}

	public static boolean isUserADriver(Connector con, UU user) {

		ResultSet rs;
		String query = "select UU_login from UD where UU_login = '" + user.getLogin() + "';";

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
			System.err.println("Error checking if user is in UD" + e);
		}
		return false;
	}
	
	public static boolean addAvailableTime(Connector con, String login, String pid) {


		String query = "insert into Available values ('" + login + "', '" + pid + "');";
		try {
			int result = con.stmt.executeUpdate(query);

			if (result > 0) {
				// insert worked
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			System.err.println("Error adding to Available table. " + e);
		}

		return true;
	}

	public static String isDriverAvailable(Connector con, String vin, String time) {

		ResultSet rs;
		String pid = "";
		java.sql.Time myTime = java.sql.Time.valueOf(time);
		String query = "select a.pid from Available a where a.login = (select car.login from UC car where car.vin = '"
				+ vin + "') and a.pid in (select p.pid from Period p where p.from <= '" + myTime + "' and p.to >= '"
				+ myTime + "');";

		try {
			rs = con.stmt.executeQuery(query);

			if (!rs.next()) {
				// not found.
				rs.close();
				return pid;
			} else {
				// get the first available pid
				pid = rs.getString(1);
				rs.close();
				return pid;
			}
		} catch (Exception e) {
			System.err.println("Error checking if user is Available" + e);
		}
		return pid;
	}
	
	public static String isDriverAvailableRide(Connector con, String vin, String from, String to) {

		ResultSet rs;
		String pid = "";
		String query = "select a.pid from Available a where a.login = (select car.login from UC car where car.vin = '"
				+ vin + "') and a.pid in (select p.pid from Period p where p.from <= '" + from + "' and p.to >= '"
				+ to + "');";

		try {
			rs = con.stmt.executeQuery(query);

			if (!rs.next()) {
				// not found.
				rs.close();
				return pid;
			} else {
				// get the first available pid
				pid = rs.getString(1);
				rs.close();
				return pid;
			}
		} catch (Exception e) {
			System.err.println("Error checking if user is Available" + e);
		}
		return pid;
	}
}
