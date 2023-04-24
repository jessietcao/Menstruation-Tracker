import java.awt.Color;
import java.awt.Font;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.border.EtchedBorder;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;


/**
 * 
 * Defines each individual cell inside the calendar
 *
 */
public class Cell extends JButton {
	private Date date;

	/**
	 * Indicates the flow is light
	 */
	public static final int LIGHT = 1;
	/**
	 * Indicates the flow is median
	 */
	public static final int MID = 2;
	/**
	 * Indicates the flow is heavy
	 */
	public static final int HEAVY = 3;
	private String notes;

	private boolean isFuture;
	private boolean isPeriod;
	private boolean isOvulate;
	private boolean isExpected;
	private PeriodCalculate pCal = Menu.getpCal();

	/**
	 * Defines the background colour and font of the button
	 * @param calendar Calendar that defines the year and month of the current page in the calendar panel 
	 */
	public Cell(Calendar calendar) {
		this.date = calendar.getTime();

		this.setBackground(Color.white);
		this.setFont(new Font("Kartika", Font.PLAIN, 20));
		setPeriod();

	}

	/**
	 * Updates frequency when called
	 */
	public void setCycle() {
		String query = "SELECT * FROM Cycle" + Login.getUsername();
		try {

			Statement statement = Database.getConnection().createStatement();
			ResultSet result = statement.executeQuery(query);
			while (result.next()) {

				Date startDate = new SimpleDateFormat("yyyy-MM-dd").parse(result.getString("startDate"));
				query = "UPDATE Cycle" + Login.getUsername() + " SET frequency =?" + " WHERE startDate=" + "'"
						+ convertToDate(startDate) + "'" + ";";
				PreparedStatement preparedStmt = Database.getConnection().prepareStatement(query);
				preparedStmt.setInt(1, calculateFrequency(startDate));
				preparedStmt.executeUpdate();
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns whether a cycle has been inputed by the user
	 * @return Boolean for whether user has inputed their cycle dates
	 */
	public boolean isStart() {
		String query = "SELECT * FROM Cycle" + Login.getUsername() + " WHERE startDate=" + "'" + convertToDate()
				+ "'" + ";";
		boolean isStart = false;
		try {
			Statement statement = Database.getConnection().createStatement();
			ResultSet result = statement.executeQuery(query);
			if (result.next()) {
				isStart = true;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return isStart;
	}

	/**
	 * Add or delete new period date and update the cycles
	 * 
	 * @param isCycle Boolean for whether the period is deleted or added 
	 */
	
	public void setCycle(boolean isCycle) {
		Calendar tempCal = Calendar.getInstance();
		tempCal.setTime(date);
		tempCal.add(Calendar.DATE, -1);
		Date prev = tempCal.getTime();
		tempCal.add(Calendar.DATE, 2);
		Date post = tempCal.getTime();
		boolean prevExist = false;
		boolean postExist = false;

		// if the previous date is on period
		String query = "SELECT * FROM Dates" + Login.getUsername() + " WHERE pdate=" + "'" + convertToDate(prev) + "'"
				+ ";";

		try {
			Statement statement = Database.getConnection().createStatement();
			ResultSet result = statement.executeQuery(query);
			if (result.next()) {
				prevExist = true;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		// if the next date is on period
		query = "SELECT * FROM Dates" + Login.getUsername() + " WHERE pdate=" + "'" + convertToDate(post) + "'" + ";";
		try {
			Statement statement = Database.getConnection().createStatement();
			ResultSet result = statement.executeQuery(query);
			if (result.next()) {
				postExist = true;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		// Adding new period dates
		if (isCycle) {
			if (!prevExist && !postExist) {
				try {
					query = "INSERT INTO Cycle" + Login.getUsername() + " (startDate, endDate, length, frequency)"
							+ " VALUES (?,?,?,?)";

					PreparedStatement preparedStmt = Database.getConnection().prepareStatement(query);
					preparedStmt.setString(1, convertToDate());
					preparedStmt.setString(2, convertToDate());
					preparedStmt.setInt(3, 1);
					preparedStmt.setInt(4, getFrequency(findPrevStartDate(prev)));
					preparedStmt.executeUpdate();
				} catch (SQLException e) {
					e.printStackTrace();
				}

			} else if (prevExist && !postExist) {
				try {
					query = "UPDATE Cycle" + Login.getUsername() + " SET endDate = ?, length = ?, frequency =?"
							+ " WHERE startDate=" + "'" + convertToDate(findPrevStartDate(prev)) + "'" + ";";
					PreparedStatement preparedStmt = Database.getConnection().prepareStatement(query);
					preparedStmt.setString(1, convertToDate());
					preparedStmt.setInt(2, getLength(findPrevStartDate(prev)) + 1);
					preparedStmt.setInt(3, getFrequency(findPrevStartDate(prev)));
					preparedStmt.executeUpdate();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} else if (!prevExist && postExist) {
				try {
					query = "UPDATE Cycle" + Login.getUsername() + " SET startDate = ?, length = ?, frequency =?"
							+ " WHERE startDate=" + "'" + convertToDate(post) + "'" + ";";
					PreparedStatement preparedStmt = Database.getConnection().prepareStatement(query);
					preparedStmt.setString(1, convertToDate());
					preparedStmt.setInt(2, getLength(post) + 1);
					preparedStmt.setInt(3, calculateFrequency(date));
					preparedStmt.executeUpdate();

				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			// when prevExist && postExist
			else {
				try {
					Date start = findPrevStartDate(prev);
					Date end = findPostEndDate(post);
					int length = getLength(start) + getLength(post) + 1;
					int frequency = calculateFrequency(start);

					query = "DELETE FROM Cycle" + Login.getUsername() + " WHERE startDate = ? ";
					PreparedStatement preparedStmt = Database.getConnection().prepareStatement(query);
					preparedStmt.setString(1, convertToDate(start));
					preparedStmt.executeUpdate();

					preparedStmt = Database.getConnection().prepareStatement(query);
					preparedStmt.setString(1, convertToDate(post));
					preparedStmt.executeUpdate();

					query = "INSERT INTO Cycle" + Login.getUsername() + " (startDate, endDate, length, frequency)"
							+ " VALUES (?,?,?,?)";
					preparedStmt = Database.getConnection().prepareStatement(query);
					preparedStmt.setString(1, convertToDate(start));
					preparedStmt.setString(2, convertToDate(end));
					preparedStmt.setInt(3, length);
					preparedStmt.setInt(4, frequency);
					preparedStmt.executeUpdate();

				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		// Deleting period
		else {

			if (!prevExist && !postExist) {
				try {
					query = "DELETE FROM Cycle" + Login.getUsername() + " WHERE startDate = ? ";
					PreparedStatement preparedStmt = Database.getConnection().prepareStatement(query);
					preparedStmt.setString(1, convertToDate());
					preparedStmt.executeUpdate();

				} catch (SQLException e) {
					e.printStackTrace();
				}

			} else if (prevExist && !postExist) {
				try {
					query = "UPDATE Cycle" + Login.getUsername() + " SET endDate = ?, length = ?, frequency =?"
							+ " WHERE startDate=" + "'" + convertToDate(findPrevStartDate(date)) + "'" + ";";
					PreparedStatement preparedStmt = Database.getConnection().prepareStatement(query);
					preparedStmt.setString(1, convertToDate(prev));
					preparedStmt.setInt(2, getLength(findPrevStartDate(date)) - 1);
					preparedStmt.setInt(3, getFrequency(findPrevStartDate(date)));
					preparedStmt.executeUpdate();

				} catch (SQLException e) {
					e.printStackTrace();
				}
			} else if (!prevExist && postExist) {
				try {
					query = "UPDATE Cycle" + Login.getUsername() + " SET startDate = ?, length = ?, frequency =?"
							+ " WHERE startDate=" + "'" + convertToDate() + "'" + ";";
					PreparedStatement preparedStmt = Database.getConnection().prepareStatement(query);
					preparedStmt.setString(1, convertToDate(post));
					preparedStmt.setInt(2, getLength(date) - 1);
					preparedStmt.setInt(3, calculateFrequency(post));
					preparedStmt.executeUpdate();

				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
			// when prevExist && postExist
			else {
				try {
					// find startDate of first period group
					query = "SELECT * FROM Cycle" + Login.getUsername() + " WHERE StartDate < " + "'"
							+ convertToDate() + "'" + " ORDER BY startDate DESC LIMIT 1;";
					Date firstStart = null;

					try {
						Statement statement = Database.getConnection().createStatement();
						ResultSet result = statement.executeQuery(query);
						if (result.next()) {
							firstStart = new SimpleDateFormat("yyyy-MM-dd").parse(result.getString("startDate"));
						}

					} catch (SQLException e) {
						e.printStackTrace();
					} catch (ParseException e) {
						e.printStackTrace();
					}

					// find endDate of second period group
					query = "SELECT * FROM Cycle" + Login.getUsername() + " WHERE StartDate =" + "'"
							+ convertToDate(firstStart) + "'" + ";";
					Date secondEnd = null;

					try {
						Statement statement = Database.getConnection().createStatement();
						ResultSet result = statement.executeQuery(query);
						if (result.next()) {
							secondEnd = new SimpleDateFormat("yyyy-MM-dd").parse(result.getString("endDate"));
						}

					} catch (SQLException e) {
						e.printStackTrace();
					} catch (ParseException e) {
						e.printStackTrace();
					}

					// storing data
					Calendar tempCal2 = Calendar.getInstance();
					tempCal2.setTime(prev);
					tempCal2.set(Calendar.HOUR, 0);
					tempCal2.set(Calendar.MINUTE, 0);
					tempCal2.set(Calendar.SECOND, 0);
					tempCal2.set(Calendar.HOUR_OF_DAY, 0);
					Date firstEnd = tempCal2.getTime();

					tempCal2.setTime(post);
					tempCal2.set(Calendar.HOUR, 0);
					tempCal2.set(Calendar.MINUTE, 0);
					tempCal2.set(Calendar.SECOND, 0);
					tempCal2.set(Calendar.HOUR_OF_DAY, 0);
					Date secondStart = tempCal2.getTime();

					int firstLength = (int) ChronoUnit.DAYS.between(firstStart.toInstant(), firstEnd.toInstant()) + 1;
					int secondLength = (int) ChronoUnit.DAYS.between(secondStart.toInstant(), secondEnd.toInstant())
							+ 2;

					// Deleting stored big period group
					query = "DELETE FROM Cycle" + Login.getUsername() + " WHERE startDate = ? ";
					PreparedStatement preparedStmt = Database.getConnection().prepareStatement(query);
					preparedStmt.setString(1, convertToDate(firstStart));
					preparedStmt.executeUpdate();

					// Add first period group
					query = "INSERT INTO Cycle" + Login.getUsername() + " (startDate, endDate, length, frequency)"
							+ " VALUES (?,?,?,?)";
					preparedStmt = Database.getConnection().prepareStatement(query);
					preparedStmt.setString(1, convertToDate(firstStart));
					preparedStmt.setString(2, convertToDate(firstEnd));
					preparedStmt.setInt(3, firstLength);
					preparedStmt.setInt(4, calculateFrequency(firstStart));
					preparedStmt.executeUpdate();

					// Add second period group
					preparedStmt = Database.getConnection().prepareStatement(query);
					preparedStmt.setString(1, convertToDate(secondStart));
					preparedStmt.setString(2, convertToDate(secondEnd));
					preparedStmt.setInt(3, secondLength);
					preparedStmt.setInt(4, calculateFrequency(secondStart));
					preparedStmt.executeUpdate();

				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

	}

	/**
	 * Calculates the average frequency of all inputed cycles
	 * 
	 * @param startDate The starting date of the cycle 
	 * @return Average frequency
	 */
	public int calculateFrequency(Date startDate) {
		String query = "SELECT * FROM Cycle" + Login.getUsername() + " WHERE StartDate < " + "'"
				+ convertToDate(startDate) + "'" + " ORDER BY startDate DESC LIMIT 1;";
		Date lastStartDate = null;

		int frequency = 0;

		try {
			PreparedStatement preparedStmt = Database.getConnection().prepareStatement(query);
			ResultSet result = preparedStmt.executeQuery();
			if (result.next()) {
				lastStartDate = new SimpleDateFormat("yyyy-MM-dd").parse(result.getString("startDate"));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		if (lastStartDate != null) {
			frequency = (int) ChronoUnit.DAYS.between(lastStartDate.toInstant(), startDate.toInstant());
		}

		return frequency;

	}

	/**
	 * Getter method for frequency
	 * @param startDate Starting date of the cycle
	 * @return Frequency of the cycle with the inputed starting date
	 */
	public int getFrequency(Date startDate) {
		String query = "SELECT * FROM Cycle" + Login.getUsername() + " WHERE StartDate=" + "'"
				+ convertToDate(startDate) + "'" + ";";
		int frequency = 0;
		try {
			PreparedStatement preparedStmt = Database.getConnection().prepareStatement(query);
			ResultSet result = preparedStmt.executeQuery();
			if (result.next()) {
				frequency = result.getInt("frequency");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return frequency;
	}

	/**
	 * Getter method for length
	 * @param startDate Starting date of the cycle 
	 * @return Length of the cycle with the inputed starting date
	 */
	public int getLength(Date startDate) {
		String query = "SELECT * FROM Cycle" + Login.getUsername() + " WHERE StartDate=" + "'"
				+ convertToDate(startDate) + "'" + ";";
		int length = 0;
		try {
			PreparedStatement preparedStmt = Database.getConnection().prepareStatement(query);
			ResultSet result = preparedStmt.executeQuery();
			if (result.next()) {
				length = result.getInt("length");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return length;
	}

	// use endDate to find startDate
	/**
	 * Finds the starting date of the previous cycle with its ending date
	 * @param prev Ending date of the cycle 
	 * @return Starting date that corresponds with the ending date
	 */
	public Date findPrevStartDate(Date prev) {
		String query = "SELECT * FROM Cycle" + Login.getUsername() + " WHERE endDate=" + "'" + convertToDate(prev) + "'"
				+ ";";
		java.util.Date startDate = new java.util.Date();
		try {
			PreparedStatement preparedStmt = Database.getConnection().prepareStatement(query);
			ResultSet result = preparedStmt.executeQuery();
			if (result.next()) {
				startDate = new SimpleDateFormat("yyyy-MM-dd").parse(result.getString("startDate"));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return startDate;
	}

	// use startDate to find endDate
	/**
	 * Finds the ending date of the next cycle with its starting date
	 * @param post Starting date of the cycle 
	 * @return Ending date that corresponds with the starting date
	 */
	
	public Date findPostEndDate(Date post) {
		String query = "SELECT * FROM Cycle" + Login.getUsername() + " WHERE startDate=" + "'" + convertToDate(post)
				+ "'" + ";";
		Date endDate = new Date();
		try {
			PreparedStatement preparedStmt = Database.getConnection().prepareStatement(query);
			ResultSet result = preparedStmt.executeQuery();
			if (result.next()) {
				endDate = new SimpleDateFormat("yyyy-MM-dd").parse(result.getString("endDate"));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return endDate;
	}

	/**
	 * Indicates if the symptom selected is during PMS
	 * @param type Indicates the table in the database
	 * @param primaryKey Indicates the column name in the table
	 * @return 1 for true and during PMS, 0 for false
	 */
	public boolean getSymptomPMS(String type, String primaryKey) {
		String query = "SELECT * FROM " + type + "s" + " WHERE " + type + " = " + "'" + primaryKey + "'" + ";";
		int symptomPMSValue = 0;
		try {
			PreparedStatement preparedStmt = Database.getConnection().prepareStatement(query);
			ResultSet result = preparedStmt.executeQuery();
			if (result.next()) {
				if (result.getInt("pms") == 1) {
					symptomPMSValue = 1;
				}

			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return symptomPMSValue == 1;
	}

	/**
	 * Indicates if the symptom selected is during ovulation
	 * @param type Indicates the table in the database
	 * @param primaryKey Indicates the column name in the table
	 * @return 1 for true and during ovulation, 0 for false
	 */
	public boolean getSymptomOvulate(String type, String primaryKey) {
		String query = "SELECT * FROM " + type + "s" + " WHERE " + type + "=" + "'" + primaryKey + "'" + ";";
		int symptomOvulateValue = 0;
		try {
			PreparedStatement preparedStmt = Database.getConnection().prepareStatement(query);
			ResultSet result = preparedStmt.executeQuery();
			if (result.next()) {
				if (result.getInt("ovulate") == 1) {
					symptomOvulateValue = 1;
				}

			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return symptomOvulateValue == 1;
	}

	/**
	 * Finds out if the type of flow given is selected
	 * @param flow Indicates if the flow is light, median, or heavy
	 * @return Indicates if the flow given is selected
	 */
	public int getFlow(int flow) {
		String query = "SELECT * FROM Notes" + Login.getUsername() + " WHERE date=" + "'" + convertToDate() + "'" + ";";
		int flowValue = 0;
		try {
			PreparedStatement preparedStmt = Database.getConnection().prepareStatement(query);
			ResultSet result = preparedStmt.executeQuery();
			if (result.next()) {
				if (flow == Cell.LIGHT) {
					flowValue = result.getInt("light");
				} else if (flow == Cell.MID) {
					flowValue = result.getInt("median");
				} else {
					flowValue = result.getInt("heavy");
				}

			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return flowValue;
	}

	/**
	 * Sets the flow as selected or not selected
	 * @param flow Indicates the if the flow is light, median, or heavy
	 */
	public void setFlow(int flow) {
		String query = "SELECT * FROM Notes" + Login.getUsername() + " WHERE date=" + "'" + convertToDate() + "'" + ";";

		try {
			Statement statement = Database.getConnection().createStatement();
			ResultSet result = statement.executeQuery(query);
			if (result.next()) {
				if (flow == Cell.LIGHT) {
					query = "UPDATE Notes" + Login.getUsername() + " SET light = ?, median = ?, heavy =?"
							+ " WHERE date=" + "'" + convertToDate() + "'" + ";";
					PreparedStatement preparedStmt = Database.getConnection().prepareStatement(query);
					preparedStmt.setInt(1, 1);
					preparedStmt.setInt(2, 0);
					preparedStmt.setInt(3, 0);
					preparedStmt.executeUpdate();
				} else if (flow == Cell.MID) {
					query = "UPDATE Notes" + Login.getUsername() + " SET light = ?, median = ?, heavy =?"
							+ " WHERE date=" + "'" + convertToDate() + "'" + ";";
					PreparedStatement preparedStmt = Database.getConnection().prepareStatement(query);
					preparedStmt.setInt(1, 0);
					preparedStmt.setInt(2, 1);
					preparedStmt.setInt(3, 0);
					preparedStmt.executeUpdate();
				} else {
					query = "UPDATE Notes" + Login.getUsername() + " SET light = ?, median = ?, heavy =?"
							+ " WHERE date=" + "'" + convertToDate() + "'" + ";";
					PreparedStatement preparedStmt = Database.getConnection().prepareStatement(query);
					preparedStmt.setInt(1, 0);
					preparedStmt.setInt(2, 0);
					preparedStmt.setInt(3, 1);
					preparedStmt.executeUpdate();
				}

			} else {
				query = setFirstNotes();

				try {
					PreparedStatement preparedStmt = Database.getConnection().prepareStatement(query);
					String sqlDate = convertToDate();
					preparedStmt.setString(1, sqlDate);
					preparedStmt.setString(2, getNotes());
					if (flow == Cell.LIGHT) {
						preparedStmt.setInt(3, 1);
						preparedStmt.setInt(4, 0);
						preparedStmt.setInt(5, 0);
					} else if (flow == Cell.MID) {
						preparedStmt.setInt(3, 0);
						preparedStmt.setInt(4, 1);
						preparedStmt.setInt(5, 0);
					} else {
						preparedStmt.setInt(3, 0);
						preparedStmt.setInt(4, 0);
						preparedStmt.setInt(5, 1);
					}

					preparedStmt.executeUpdate();

				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Set notes for each date
	 * @param notes Inserted for each date
	 */
	public void setNotes(String notes) {
		String query = "SELECT * FROM Notes" + Login.getUsername() + " WHERE date=" + "'" + convertToDate() + "'" + ";";

		try {
			Statement statement = Database.getConnection().createStatement();
			ResultSet result = statement.executeQuery(query);
			// if notes already existed
			if (result.next()) {
				query = "UPDATE Notes" + Login.getUsername() + " SET notes = ?" + " WHERE date=" + "'" + convertToDate()
						+ "'" + ";";
				PreparedStatement preparedStmt = Database.getConnection().prepareStatement(query);
				preparedStmt.setString(1, notes);

				preparedStmt.executeUpdate();
			} else {
				query = setFirstNotes();

				try {
					PreparedStatement preparedStmt = Database.getConnection().prepareStatement(query);
					String sqlDate = convertToDate();
					preparedStmt.setString(1, sqlDate);
					preparedStmt.setString(2, notes);

					preparedStmt.setInt(3, getFlow(Cell.LIGHT));
					preparedStmt.setInt(4, getFlow(Cell.MID));
					preparedStmt.setInt(5, getFlow(Cell.HEAVY));

					preparedStmt.executeUpdate();

				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

		}
		catch (SQLException e) {
			e.printStackTrace();
		}

	}

	// setting notes for the first time
	/**
	 * Define the query to set notes for the first time
	 * @return Query for inserting new notes in the table
	 */
	public static String setFirstNotes() {
		String query = "INSERT INTO Notes" + Login.getUsername() + "(date,notes,light,median,heavy)"
				+ " VALUES(?,?,?,?,?)";

		return query;
	}

	/**
	 * Retrieve notes from table in the database
	 * @return Notes from database
	 */
	public String getNotes() {
		String query = "SELECT * FROM Notes" + Login.getUsername() + " WHERE date=" + "'" + convertToDate() + "'" + ";";
		notes = "";
		try {
			Statement statement = Database.getConnection().createStatement();
			ResultSet result = statement.executeQuery(query);
			if (result.next()) {
				notes = result.getString("notes");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return notes;
	}

	/**
	 * Set the date as future
	 * @param isFuture True for in future, False for not in future
	 */
	public void setFuture(boolean isFuture) {
		this.isFuture = isFuture;
	}

	/**
	 * Getter method to determine if the date is in future
	 * @return True for in future, false for not in future
	 */
	public boolean getFuture() {
		return isFuture;
	}

	/**
	 * Set the date of the cell as today 
	 * @param isToday True if the cell is today, false if it is not today
	 */
	public void setToday(boolean isToday) {
		if (isToday) {
			this.setForeground(new Color(200, 100, 100));
			EtchedBorder highlightBorder = new EtchedBorder(EtchedBorder.LOWERED, new Color(255, 215, 186),
					new Color(254, 200, 154));
			this.setBorder(highlightBorder);
			this.setFont(new Font("Kartika", Font.BOLD, 20));

		}
	}

	/**
	 * Set expected period and ovulation days
	 */
	public void setExpected() {

		if (Dates.getExpectedDaysList().getHeader() != null && Dates.getExpectedDaysList().getHeader().getNext() != null
				&& Dates.getExpectedDaysList().getHeader().getNext().getStartDate() != null && !pCal.getPaused()) {
			// System.out.println("in");
			// calculate expected
			Date firstStart = Dates.getExpectedDaysList().getHeader().getNext().getStartDate();

			// calculate ovulation
			if (pCal.getNormal()) {

				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				Date ovulateDay = pCal.calculateOvulate(firstStart);
				String date1 = sdf.format(ovulateDay);
				Calendar tempCal = Calendar.getInstance();

				tempCal.setTime(ovulateDay);

				tempCal.add(Calendar.DATE, -1);
				Date yesterday = tempCal.getTime();
				String date2 = sdf.format(yesterday);
				tempCal.add(Calendar.DATE, 2);
				Date tmr = tempCal.getTime();
				String date3 = sdf.format(tmr);

				String d = sdf.format(date);

				if (d.equals(date1) || d.equals(date2) || d.equals(date3)) {
					isOvulate = true;

				} else {
					isOvulate = false;
				}
			} else {
				isOvulate = false;
			}

			// calculate expected
			if (Dates.getExpectedDaysList().traverse(date)) {
				isExpected = true;
			} else {
				isExpected = false;
			}
		} else if (Dates.getExpectedDaysList().getHeader() != null
				&& Dates.getExpectedDaysList().getHeader().getStartDate() != null && !pCal.getPaused()) {
			if (Dates.getExpectedDaysList().traverse(date)) {
				isExpected = true;
			} else {
				isExpected = false;
			}
		}

		else {
			isExpected = false;
			isOvulate = false;
		}

		if (isExpected && !isPeriod) {
			this.setBackground(new Color(232, 232, 228));
		} else if (isOvulate && !isPeriod) {
			this.setBackground(new Color(255, 229, 217));
		}

		else if (!isPeriod) {
			this.setBackground(Color.white);
		}

	}

	/**
	 * Set days where the client menstrated
	 */
	public void setPeriod() {

		String query = "SELECT * FROM Dates" + Login.getUsername() + " WHERE pdate=" + "'" + convertToDate() + "'"
				+ ";";

		try {
			PreparedStatement preparedStmt = Database.getConnection().prepareStatement(query);
			ResultSet result = preparedStmt.executeQuery();
			if (result.next()) {
				setPeriod(true);
			} else {
				setPeriod(false);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	// formatting background colours
	/**
	 * Set the background colours of the cells
	 * @param isPeriod Indicates if the user is on period
	 */
	public void setPeriod(boolean isPeriod) {
		this.isPeriod = isPeriod;
		if (isPeriod) {
			this.setBackground(new Color(254, 197, 187));
		} else {
			setExpected();
			if (!isExpected && !isOvulate) {
				this.setBackground(Color.white);
			}
		}
	}

	// inserting new periods
	/**
	 * Inser new period dates into the database
	 * @param b Indicates if the user adds a menstruating date (true), or deletes a menstruating date (false)
	 */
	public void setNewPeriod(boolean b) {
		if (b) {
			String query = "INSERT INTO Dates" + Login.getUsername() + "(pdate)" + " VALUES(?)";

			try {
				PreparedStatement preparedStmt = Database.getConnection().prepareStatement(query);
				String sqlDate = convertToDate();
				preparedStmt.setString(1, sqlDate);

				preparedStmt.executeUpdate();

			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			String query = "DELETE FROM Dates" + Login.getUsername() + " WHERE pdate = ? ";
			try {
				PreparedStatement preparedStmt = Database.getConnection().prepareStatement(query);
				preparedStmt.setString(1, convertToDate());
				preparedStmt.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		setPeriod(b);

	}
	

	/**
	 * Getter method to check if the date is during menstruation
	 * @return True if on period, false if not on period
	 */
	public boolean getPeriod() {
		return isPeriod;
	}

	/**
	 * Getter method for the date of the cell
	 * @return Date of the cell
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * Getter method to check if the date is during ovulation
	 * @return True if during ovulation, false if not during ovulation
	 */
	public boolean getOvulate() {
		return isOvulate;
	}

	
	/**
	 * Convert Date objects to String objects
	 * @return The current date in String
	 */
	public String convertToDate() {

		SimpleDateFormat dateFormatted = new SimpleDateFormat("yyyy-MM-dd");
		return dateFormatted.format(date);
	}

	/**
	 * Convert Date objects (that is inputed) to String objects
	 * @param date The date to be converted
	 * @return The String of the date inputed
	 */
	public static String convertToDate(Date date) {

		SimpleDateFormat dateFormatted = new SimpleDateFormat("yyyy-MM-dd");
		return dateFormatted.format(date);
	}

}
