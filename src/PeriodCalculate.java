import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

/**
 * 
 * Functional class used to calculate and keep track of the period cycles; its functions include calculating the frequency,
 * calculating the average length of period, predicting the ovulation dates, and tracking the progress of the current cycle.
 *
 */
public class PeriodCalculate {

	private int length;
	private int frequency;
	private boolean isNormal;
	private boolean isPaused;
	private boolean isChanged;
	private int changedFreq;
	private int changedLength;
	private boolean isNoData;
	private int progressDate;
	private int nextDate;
	private LocalDate localDate;
	private Date today;
	
	/**
	 * Sets up the cycles data, identifies the the current date
	 */
	public PeriodCalculate() {
		String query = "SELECT * FROM Dates"+Login.getUsername();
		Date date = new Date();
		localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		calculateFreq();
		calculateLength();
		setSettings();
		setNormal();
		try {
			PreparedStatement preparedStmt = Database.getConnection().prepareStatement(query);
			ResultSet result = preparedStmt.executeQuery();
			if(result.next()) {
				isNoData = false;
			} 
			else {
				isNoData = true;
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
		
		today = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
		if (!isNoData)
		{
			query = "SELECT * FROM Cycle"+Login.getUsername()
			+" WHERE startDate = (SELECT MAX(startDate) FROM Cycle"+Login.getUsername()+ ")";
			
			Date startDate = null;
			try {
			
			PreparedStatement preparedStmt = Database.getConnection().prepareStatement(query);
			ResultSet result = preparedStmt.executeQuery();
			
			if(result.next()) {
				
				startDate = new SimpleDateFormat("yyyy-MM-dd").parse(result.getString("startDate"));
				
				}
			}
			catch (SQLException e) {
			e.printStackTrace();} 
			catch (ParseException e) {
				e.printStackTrace();
			}
			progressDate = (int) ChronoUnit.DAYS.between(startDate.toInstant(), today.toInstant())+1;
			
			Calendar tempCal = Calendar.getInstance();
			tempCal.setTime(startDate);
			
			tempCal.add(Calendar.DATE, getFreq());
			startDate = tempCal.getTime();
			
			
			nextDate =(int) (ChronoUnit.DAYS.between(today.toInstant(), startDate.toInstant()));
		}
		
		
		
	}
	/**
	 * Getter method for current date
	 * @return Current date
	 */
	public Date getToday() {
		return today;
	}
	
	/**
	 * Getter method indicating whether there exists user data
	 * @return True if data exists, false if it doesn't
	 */
	public boolean getNoData() {
		return isNoData;
	}
	/**
	 * Getter method indicating whether the tracking of cycle is paused
	 * @return True if paused, false if not paused
	 */
	public boolean getPaused() {
		return isPaused;
	}
	
	/**
	 * Setter method indicating whether the tracking of the cycle should be paused
	 * @param isPaused True if paused, false if not paused
	 */
	public void setPaused(boolean isPaused) {
		String query = "SELECT * FROM Settings"+Login.getUsername()
		+" WHERE count = "+1;
		int pausedInt = 0;
		if(isPaused) {
			pausedInt = 1;
		}
		try {
		
		PreparedStatement preparedStmt = Database.getConnection().prepareStatement(query);
		ResultSet result = preparedStmt.executeQuery();
			if(result.next()) {
				query = "UPDATE Settings"+Login.getUsername() 
				+" SET preg = ? WHERE count = 1";
				preparedStmt = Database.getConnection().prepareStatement(query);
				preparedStmt.setInt(1, pausedInt);
				preparedStmt.executeUpdate();
			}
		
		}
		catch (SQLException e) {
		e.printStackTrace();}
		
		this.isPaused = isPaused;
	}
	
	/**
	 * Setter method indicating if the user has changed the calcualted length and frequency
	 * @param isChanged True if changed, false if not changed
	 */
	public void setChanged(boolean isChanged) {
		String query = "SELECT * FROM Settings"+Login.getUsername() +" WHERE count = "+1;
		int changedInt = 0;
		if(isChanged) {
			changedInt = 1;
		}
		try {
		
		PreparedStatement preparedStmt = Database.getConnection().prepareStatement(query);
		ResultSet result = preparedStmt.executeQuery();
			if(result.next()) {
				query = "UPDATE Settings"+Login.getUsername() 
				+" SET changed = ? WHERE count = 1";
				preparedStmt = Database.getConnection().prepareStatement(query);
				preparedStmt.setInt(1, changedInt);
				preparedStmt.executeUpdate();
			}
		
		}
		catch (SQLException e) {
		e.printStackTrace();}
		
		this.isChanged = isChanged;
		
	}
	
	/**
	 * Initializes the settings, determines whether data has been altered and whether tracking has been paused
	 */
	public void setSettings() {
		String query = "SELECT * FROM  Settings"+Login.getUsername() +" WHERE count = "+1;
		int changedInt = 0;
		int pausedInt = 0;
		
		try {
		
		PreparedStatement preparedStmt = Database.getConnection().prepareStatement(query);
		ResultSet result = preparedStmt.executeQuery();
		if(result.next()) {
			changedInt =result.getInt("changed");
			pausedInt = result.getInt("preg");
			if (changedInt ==1)
			{
				changedFreq = result.getInt("frequency");
				changedLength = result.getInt("length");
			}
			
		}
		
		}
		catch (SQLException e) {
		e.printStackTrace();}
		
		if(changedInt==0) {
			isChanged = false;
		}
		else {
			isChanged = true;
		}
		if (pausedInt ==0) {
			isPaused = false;
		}
		else {
			isPaused = true;
		}
	}
	
	/**
	 * Getter method indicating whether the data calculated has been altered
	 * @return True if altered, false if not altered
	 */
	public boolean getChanged() {
		return isChanged;
	}
	
	/**
	 * Determines whether the calculated frequency and length of period cycle is normal
	 */
	public void setNormal() {
		if ((frequency>=21 && frequency <=35)&&(length>=2&&length<=7)) {
			isNormal = true;
		}
		else {
			isNormal = false;
		}
	}
	
	/**
	 * Getter method indicating whether the cycle of user is normal
	 * @return True if normal, false if not normal
	 */
	public boolean getNormal() {
		return isNormal;
	}
	
	/**
	 * Setter method for when the calculated length is changed
	 * @param changedLength The new inputed length of cycle
	 */
	public void setChangedLength(int changedLength) {
		String query = "SELECT * FROM  Settings"+Login.getUsername() +" WHERE count = "+1;
		try {
		
		PreparedStatement preparedStmt = Database.getConnection().prepareStatement(query);
		ResultSet result = preparedStmt.executeQuery();
			if(result.next()) {
				query = "UPDATE  Settings"+Login.getUsername() 
				+" SET length = ? WHERE count = 1";
				preparedStmt = Database.getConnection().prepareStatement(query);
				preparedStmt.setInt(1, changedLength);
				preparedStmt.executeUpdate();
			}
		
		}
		catch (SQLException e) {
		e.printStackTrace();}
		this.changedLength = changedLength;
	}
	
	/**
	 * Setter method for when the calculated frequency is changed
	 * @param changedFreq The new inputed frequency of cycle
	 */
	public void setChangedFreq (int changedFreq) {
		String query = "SELECT * FROM  Settings"+Login.getUsername() +" WHERE count = 1";
		try {
		
		PreparedStatement preparedStmt = Database.getConnection().prepareStatement(query);
		ResultSet result = preparedStmt.executeQuery();
			if(result.next()) {
				query = "UPDATE  Settings"+Login.getUsername() 
				+" SET frequency = ? WHERE count = 1";
				preparedStmt = Database.getConnection().prepareStatement(query);
				preparedStmt.setInt(1, changedFreq);
				preparedStmt.executeUpdate();
			}
		
		}
		catch (SQLException e) {
		e.printStackTrace();}
		
		this.changedFreq = changedFreq;
	}
	
	/**
	 * Calculates the length of cycle from the database
	 */
	public void calculateLength() {
		String query = "SELECT * FROM  Cycle"+Login.getUsername()
						+" WHERE startDate != (SELECT MAX(startDate) FROM  Cycle"+Login.getUsername()+ ")";
		int total = 0;
		int count = 0;
		
		try {
			
			PreparedStatement preparedStmt = Database.getConnection().prepareStatement(query);
			ResultSet result = preparedStmt.executeQuery();
			while(result.next()) {
				total +=result.getInt("length");
				count++;
				
			}
			
		}
		catch (SQLException e) {
			e.printStackTrace();}
		
		if (count!=0) {
			length = (int) Math.round((double)total/count);
		}
		//Default length
		else {
			length = 5;
		}
	}
	
	/**
	 * Calculates the frequency of the cycle from the database
	 */
	public void calculateFreq() {
		String query = "SELECT * FROM  Cycle"+Login.getUsername()
						+" WHERE startDate != (SELECT MIN(startDate) FROM Cycle"+Login.getUsername()+ ")";
		int total = 0;
		int count = 0;
		
		try {
			
			PreparedStatement preparedStmt = Database.getConnection().prepareStatement(query);
			ResultSet result = preparedStmt.executeQuery();
			while(result.next()) {
				total +=result.getInt("frequency");
				count++;
			}
			
		}
		catch (SQLException e) {
			e.printStackTrace();}
		
		if (count!=0) {
			frequency = (int) Math.round((double)total/count);
		}
		else {
			frequency = 28;
		}
	}
	
	//Ovulation period starts around 14 days before the next period
	/**
	 * Calculates the ovulation date 
	 * @param day The next first day of expected period
	 * @return Date of ovulation
	 */
	public Date calculateOvulate(Date day) {
		if(day!=null) {
			Calendar tempCal = Calendar.getInstance();
			
			tempCal.setTime(day);
			tempCal.set(Calendar.HOUR_OF_DAY, 0);
			tempCal.set(Calendar.MINUTE, 0);
			tempCal.set(Calendar.SECOND, 0);
			tempCal.set(Calendar.MILLISECOND, 0);
			
			tempCal.add(Calendar.DATE, -14);
			day = tempCal.getTime();
			
		}
		return day;
	}

	
	//how many days in the period
	/**
	 * Getter method for the progression of the cycle
	 * @return The days into the cycle
	 */
	public int getProgressDate() {
		return progressDate;
	}
	
	//next period starts in this many days
	/**
	 * Getter method calculating how many days until the next period starts
	 * @return Days until next period starts
	 */
	public int getNextDate() {
		return nextDate;
	}

	
	/**
	 * Getter method for frequency
	 * @return Frequency of cycle
	 */
	public int getFreq() {
		if (!isChanged) {
			return frequency;
		}
		else {
			return changedFreq;
		}
	}
	
	/**
	 * Getter method for length
	 * @return Length of cycle
	 */
	public int getLength() {
		if (!isChanged) {
			return length;
		}
		else {
			return changedLength;
		}
	}
	
	
}
