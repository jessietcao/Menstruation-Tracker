import java.sql.Connection;
import java.sql.DriverManager;

import javax.swing.JOptionPane;

/**
 * Builds a connection to the sqlite databsae
 *
 */
public class Database {
	public static Connection con;

	/**
	 * Delcaring the databse connection
	 */
	public Database() {
		try
		{
            String url = "jdbc:sqlite:db.db";
			Class.forName("org.sqlite.JDBC");

			con = DriverManager.getConnection(url);
		}
		catch(Exception e)
		{
			JOptionPane.showMessageDialog(null,  e);
		}
		
	}
	
	/**
	 * Getter method for sqlite database connection
	 * @return Connection link to the sqlite database
	 */
	public static Connection getConnection() {
		return con;
	}

}
