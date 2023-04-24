import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.*;
import org.sqlite.*;

/**
 * Formats the overall panel for the login function
 *
 */
public class Login extends JPanel implements ActionListener {

	private static JLabel welcome;
	private static JLabel userLabel;
	private static JTextField userLoginText;
	private static JLabel passwordLabel;
	private static JPasswordField passwordLoginText;

	private static JTextField userRegisterText;
	private static JPasswordField passwordRegisterText;

	private static JButton buttonLogin;
	private static JButton buttonAccount;
	private static JLabel loginSuccess;
	private static JLabel registerSuccess;
	private static JPanel registerPanel;
	private static JPanel loginPanel;
	private static JPanel welcomePanel;
	private static JButton buttonRegister;
	private static JButton buttonReturn;
	private static String username;
	private static String password;
	private static GridBagConstraints gbd;
	private JFrame frame;

	/**
	 * Defines the overall layout of the login panel, including the textfields, buttons, etc.
	 * @param frame Main JFrame which holds all of the panels
	 */
	public Login(JFrame frame) {

		Database db = new Database();
		this.frame = frame;

		this.setLayout(new GridBagLayout());
		welcomePanel = new JPanel();
		loginPanel = new JPanel();
		gbd = new GridBagConstraints();

		loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));
		welcomePanel.setLayout(new BoxLayout(welcomePanel, BoxLayout.Y_AXIS));

		welcomePanel.add(Box.createVerticalStrut(50));
		welcome = new JLabel("Welcome back!");
		welcome.setPreferredSize(new Dimension(400, 100));
		welcome.setAlignmentX(Component.CENTER_ALIGNMENT);
		welcome.setFont(new Font("Serif", Font.PLAIN, 30));
		welcome.setForeground(Color.white);
		welcomePanel.add(welcome);

		gbd.gridx = 0;
		gbd.gridy = 0;
		this.add(welcomePanel, gbd);
		welcomePanel.setBackground(new Color(252, 213, 206));

		loginPanel.add(Box.createVerticalGlue());
		loginPanel.add(Box.createVerticalStrut(20));

		userLabel = new JLabel("Username");
		userLabel.setPreferredSize(new Dimension(80, 25));
		userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

		loginPanel.add(userLabel);
		loginPanel.add(Box.createVerticalStrut(10));

		userLoginText = new JTextField(20);
		userLoginText.setMaximumSize(new Dimension(165, 25));
		userLoginText.setAlignmentX(Component.CENTER_ALIGNMENT);
		loginPanel.add(userLoginText);
		loginPanel.add(Box.createVerticalStrut(30));

		passwordLabel = new JLabel("Password");
		passwordLabel.setPreferredSize(new Dimension(80, 25));
		passwordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		loginPanel.add(passwordLabel);
		loginPanel.add(Box.createVerticalStrut(10));

		passwordLoginText = new JPasswordField();
		passwordLoginText.setMaximumSize(new Dimension(165, 25));
		passwordLoginText.setAlignmentX(Component.CENTER_ALIGNMENT);
		loginPanel.add(passwordLoginText);
		loginPanel.add(Box.createVerticalStrut(30));

		buttonLogin = new JButton("Login");
		buttonLogin.setBackground(Color.white);
		buttonLogin.addActionListener(this);
		buttonLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
		loginPanel.add(buttonLogin);
		loginPanel.add(Box.createVerticalStrut(10));

		buttonAccount = new JButton("Don't have an account?");
		buttonAccount.setBackground(Color.white);
		buttonAccount.addActionListener(this);
		buttonAccount.setAlignmentX(Component.CENTER_ALIGNMENT);
		loginPanel.add(buttonAccount);
		loginPanel.add(Box.createVerticalStrut(5));

		loginSuccess = new JLabel("");
		loginSuccess.setPreferredSize(new Dimension(400, 25));
		loginSuccess.setAlignmentX(Component.CENTER_ALIGNMENT);
		loginPanel.add(loginSuccess);

		loginPanel.add(Box.createVerticalStrut(10));

		loginPanel.setBackground(new Color(250, 225, 221));

		gbd.gridy = 1;
		this.add(loginPanel, gbd);
		this.setBackground(new Color(248, 237, 235));

	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == buttonRegister) {
			username = userRegisterText.getText();
			password = String.valueOf(passwordRegisterText.getPassword());
			try {

				String query = " INSERT INTO logininfo (username,password)" + " VALUES (?, ?)";

				PreparedStatement preparedStmt = Database.getConnection().prepareStatement(query);
				if (username.equals("") || password.equals("")) {
					registerSuccess.setText("Username or password cannot be empty");

				} else {
					preparedStmt.setString(1, username);
					preparedStmt.setString(2, password);
					preparedStmt.execute();
					initRegister();

					registerPanel.setVisible(false);
					loginPanel.setVisible(true);

					welcome.setText("Welcome back!");
					loginSuccess.setText("Registration successful");

				}
			}

			catch (SQLiteException dublicate) {
				registerSuccess.setText("Username taken");

			} catch (SQLException e1) {
				e1.printStackTrace();
			}

		} else if (e.getSource() == buttonReturn) {

			welcome.setText("Welcome back!");
			registerPanel.setVisible(false);
			loginPanel.setVisible(true);
		} else if (e.getSource() == buttonAccount) {
			createRegisterPanel();

		} else {
			{
				username = userLoginText.getText();
				password = String.valueOf(passwordLoginText.getPassword());

				if (!username.equals("") && !password.equals("")) {
					try {
						String query = "SELECT * FROM logininfo WHERE username =" + "'" + username + "'" + ";";

						Statement statement = Database.getConnection().createStatement();
						ResultSet result = statement.executeQuery(query);

						String savedPassword = "";
						if (result.next()) {
							savedPassword = result.getString("password");
						}

						if (password.equals(savedPassword)) {
							Menu menu = new Menu(frame);
							frame.getContentPane().remove(this);
							frame.getContentPane().add(menu);
							frame.invalidate();
							frame.validate();

						} else {
							loginSuccess.setText("Username or password incorrect");
						}
					}

					catch (SQLException e1) {
						JOptionPane.showMessageDialog(null, e1);
					}
				}
			}
		}
	}

	/**
	 * Create a new panel for registration once called
	 */
	public void createRegisterPanel() {
		loginPanel.setVisible(false);
		registerPanel = new JPanel();

		welcome.setText("We're glad you're here!");

		registerPanel.setLayout(new BoxLayout(registerPanel, BoxLayout.Y_AXIS));

		registerPanel.add(Box.createVerticalGlue());
		registerPanel.add(Box.createVerticalStrut(20));

		userLabel = new JLabel("Username");
		userLabel.setPreferredSize(new Dimension(80, 25));

		userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

		registerPanel.add(userLabel);
		registerPanel.add(Box.createVerticalStrut(10));

		userRegisterText = new JTextField(20);

		userRegisterText.setMaximumSize(new Dimension(165, 25));

		userRegisterText.setAlignmentX(Component.CENTER_ALIGNMENT);
		registerPanel.add(userRegisterText);
		registerPanel.add(Box.createVerticalStrut(30));

		passwordLabel = new JLabel("Password");
		passwordLabel.setPreferredSize(new Dimension(80, 25));
		passwordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		registerPanel.add(passwordLabel);
		registerPanel.add(Box.createVerticalStrut(10));

		passwordRegisterText = new JPasswordField();
		passwordRegisterText.setMaximumSize(new Dimension(165, 25));
		passwordRegisterText.setAlignmentX(Component.CENTER_ALIGNMENT);
		registerPanel.add(passwordRegisterText);
		registerPanel.add(Box.createVerticalStrut(30));

		// --------

		buttonRegister = new JButton("Register");
		buttonRegister.setAlignmentX(Component.CENTER_ALIGNMENT);
		buttonRegister.setBackground(Color.white);
		buttonRegister.addActionListener(this);
		registerPanel.add(buttonRegister);
		registerPanel.add(Box.createVerticalStrut(10));

		buttonReturn = new JButton("Back");
		buttonReturn.setBackground(Color.white);
		buttonReturn.setAlignmentX(Component.CENTER_ALIGNMENT);
		buttonReturn.addActionListener(this);
		registerPanel.add(buttonReturn);
		registerPanel.add(Box.createVerticalStrut(5));

		registerSuccess = new JLabel("");
		registerSuccess.setPreferredSize(new Dimension(400, 25));
		registerSuccess.setAlignmentX(Component.CENTER_ALIGNMENT);
		registerPanel.add(registerSuccess);

		registerPanel.add(Box.createVerticalStrut(10));

		registerPanel.setBackground(new Color(250, 225, 221));

		gbd.gridy = 1;
		this.add(registerPanel, gbd);

	}

	/**
	 * Initializes the user tables in the database once it is registered
	 */
	public static void initRegister() {

		// table with period dates
		String query = "CREATE TABLE " + "Dates" + username + " (" + "pdate TEXT NOT NULL," + "PRIMARY KEY(pdate))";

		try {
			Statement statement = Database.getConnection().createStatement();
			statement.executeUpdate(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// table with notes
		query = "CREATE TABLE " + "Notes" + username + " (" + "date TEXT NOT NULL," + "notes TEXT," + "light INT, "
				+ "median INT, " + "heavy INT, " + "PRIMARY KEY(date))";
		try {
			Statement statement = Database.getConnection().createStatement();
			statement.executeUpdate(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// table with period cycles
		query = "CREATE TABLE " + "Cycle" + username + " (" + "startDate TEXT NOT NULL," + "endDate TEXT,"
				+ "length INT, " + "frequency INT, " + "PRIMARY KEY(startDate))";
		try {
			Statement statement = Database.getConnection().createStatement();
			statement.executeUpdate(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// table with symptoms
		query = "CREATE TABLE " + "symptoms" + username + " (" + "date TEXT," + "acne INT, " + "bloated INT, "
				+ "cramps INT, " + "headache INT, " + "insomnia INT, " + "tiredness INT, " + "\"weight gain\" INT, "
				+ "\"weight loss\" INT, " + "PRIMARY KEY(date))";
		try {
			Statement statement = Database.getConnection().createStatement();
			statement.executeUpdate(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// table with emotions
		query = "CREATE TABLE " + "emotions" + username + " (" + "date TEXT," + "angry INT, " + "anxious INT, "
				+ "calm INT, " + "craving INT, " + "depressed INT, " + "excited INT, " + "happy INT, " + "lazy INT, "
				+ "motivated INT, " + "PRIMARY KEY(date))";
		try {
			Statement statement = Database.getConnection().createStatement();
			statement.executeUpdate(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// table with discharges
		query = "CREATE TABLE " + "discharges" + username + " (" + "date TEXT," + "brown INT, "
				+ "\"clear and stretchy\" INT, " + "\"clear and watery\" INT, " + "\"spotting blood\" INT, "
				+ "white INT, " + "\"yellow/green\" INT, " + "PRIMARY KEY(date))";
		try {
			Statement statement = Database.getConnection().createStatement();
			statement.executeUpdate(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// table with settings
		query = "CREATE TABLE " + "Settings" + username + " (" + "count INT," + "changed INT, " + "preg INT, "
				+ "length INT, " + "frequency INT," + "PRIMARY KEY(count))";
		try {
			Statement statement = Database.getConnection().createStatement();
			statement.executeUpdate(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		query = "INSERT INTO Settings" + username + " VALUES(?,?,?,?,?)";

		try {
			PreparedStatement preparedStmt = Database.getConnection().prepareStatement(query);
			preparedStmt.setInt(1, 1);
			// boolean values
			preparedStmt.setInt(2, 0);
			preparedStmt.setInt(3, 0);
			preparedStmt.setInt(4, 0);
			preparedStmt.setInt(5, 0);

			preparedStmt.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Getter method for user's username
	 * @return username
	 */
	public static String getUsername() {
		return username;
	}

}
