package cs5530;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

public class Period {
	/**
	 * Member fields on the Period class
	 */
	private String pid;
	private String start;
	private String end;

	public Period(String pid, String timeStart, String timeEnd) {
		this.pid = pid;
		this.start = timeStart;
		this.end = timeEnd;
	}

	public static boolean createNewPeriod(Period time, Statement stmt) {
		String query = "insert into Period values ('" + time.getPid() + "', '" + time.getTimeStart() + "', '"
				+ time.getTimeEnd() + "');";

		try {
			int result = stmt.executeUpdate(query);

			if (result > 0) {
				// insert worked
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			System.err.println("There was an error creating the time Period" + e);
			return false;
		}
	}

	public static String timeExist(String timeStart, String timeEnd, Statement stmt) {
		ResultSet rs;
		String pid = "";
		String query = "select p.pid from Period p where p.from = '" + timeStart + "' and p.to = '" + timeEnd + "';";

		try {
			rs = stmt.executeQuery(query);
			if (!rs.next()) {
				// no duplicate
				rs.close();
				return pid;
			} else {
				// duplicate found
				pid = rs.getString(1);
				rs.close();
				return pid;
			}

		} catch (Exception e) {
			System.err.println("Error while checking for duplicate time Period field. " + e);
		}
		return pid;
	}

	public String getPid() {
		return this.pid;
	}

	public String getTimeStart() {
		return this.start;
	}

	public String getTimeEnd() {
		return this.end;
	}

}
