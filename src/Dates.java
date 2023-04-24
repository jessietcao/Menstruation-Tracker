import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.ZoneId;

import java.util.Calendar;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

/**
 * 
 * Initializes the cells in the calendar and formats the overall grid in the calendar
 *
 */
public class Dates extends JPanel implements ActionListener {

	private static PeriodCalculate pCal;
	private static LocalDate localDate;
	private static int dayOfMonth;
	private static Calendar calendar;
	private int year;
	private int month;

	private JPanel datesPanel;
	private GridBagConstraints gbd;
	private JPanel monthPanel;
	private JLabel monthName;

	private static JLabel sun;
	private static JLabel mon;
	private static JLabel tue;
	private static JLabel wed;
	private static JLabel thu;
	private static JLabel fri;
	private static JLabel sat;

	private static Cell[] cells;
	private static LinkedList expectedDaysList = new LinkedList();

	/**
	 * Defines the layout of the grid in calendar
	 * @param year Year of the calendar
	 * @param month Month of the calendar 
	 */
	public Dates(int year, int month) {

		this.year = year;
		this.month = month;

		pCal = Menu.getpCal();

		localDate = CalendarPanel.getLocalDate();
		this.setLayout(new GridBagLayout());
		gbd = new GridBagConstraints();

		monthPanel = new JPanel();
		monthPanel.setLayout(new GridBagLayout());
		monthName = new JLabel(Month.of(month).name() + " " + year);
		monthName.setFont(new Font("Franklin Gothic Medium", Font.PLAIN, 25));

		monthPanel.add(monthName);
		monthPanel.setBackground(new Color(250, 225, 221));
		gbd.gridx = 0;
		gbd.gridy = 0;
		this.add(monthPanel, gbd);

		datesPanel = new JPanel();
		datesPanel.setLayout(new GridLayout(0, 7, 5, 5));
		datesPanel.setBackground(new Color(250, 225, 221));

		setExpectedDaysList();

		initialize();

		gbd.gridy = 1;
		this.add(datesPanel, gbd);
		this.setBorder(new EmptyBorder(20, 20, 20, 20));
		this.setBackground(new Color(250, 225, 221));

	}

	/**
	 * Initializes the labels of the calendar
	 */
	public void initialize() {
		sun = new JLabel("SUN");
		sun.setFont(new Font("Microsoft Yi Baiti", Font.PLAIN, 20));
		mon = new JLabel("MON");
		mon.setFont(new Font("Microsoft Yi Baiti", Font.PLAIN, 20));
		tue = new JLabel("TUE");
		tue.setFont(new Font("Microsoft Yi Baiti", Font.PLAIN, 20));
		wed = new JLabel("WED");
		wed.setFont(new Font("Microsoft Yi Baiti", Font.PLAIN, 20));
		thu = new JLabel("THU");
		thu.setFont(new Font("Microsoft Yi Baiti", Font.PLAIN, 20));
		fri = new JLabel("FRI");
		fri.setFont(new Font("Microsoft Yi Baiti", Font.PLAIN, 20));
		sat = new JLabel("SAT");
		sat.setFont(new Font("Microsoft Yi Baiti", Font.PLAIN, 20));

		datesPanel.add(sun);
		datesPanel.add(mon);
		datesPanel.add(tue);
		datesPanel.add(wed);
		datesPanel.add(thu);
		datesPanel.add(fri);
		datesPanel.add(sat);

		calendar = Calendar.getInstance();

		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month - 1); // month jan as 0 so start from 0
		calendar.set(Calendar.DATE, 1);
		int startDay = calendar.get(Calendar.DAY_OF_WEEK) - 1; // get day of week -1 to index

		calendar.add(Calendar.DATE, -startDay);

		cells = new Cell[42];
		for (int i = 0; i < 42; i++) {
			cells[i] = new Cell(calendar);
			initCells(cells[i]);
			datesPanel.add(cells[i]);
		}
	}

	/**
	 * Initializes the individual cells in the grid of the calendar
	 * @param cell Cell object that defines each cell in the grid of the calendar
	 */
	public void initCells(Cell cell) {
		dayOfMonth = calendar.get(Calendar.DATE);
		cell.setText(dayOfMonth + "");

		// If date is in future
		// Note: month of calendar is from 0 to 11
		if ((localDate.getYear() < calendar.get(Calendar.YEAR))
				|| (localDate.getYear() == calendar.get(Calendar.YEAR)
						&& localDate.getMonthValue() < calendar.get(Calendar.MONTH) + 1)
				|| (localDate.getYear() == calendar.get(Calendar.YEAR)
						&& localDate.getMonthValue() == calendar.get(Calendar.MONTH) + 1
						&& localDate.getDayOfMonth() < dayOfMonth)) {
			cell.setFuture(true);

		} else {
			cell.setFuture(false);
		}

		// Check if date is today
		if (localDate.getYear() == calendar.get(Calendar.YEAR)
				&& localDate.getMonthValue() == calendar.get(Calendar.MONTH) + 1
				&& localDate.getDayOfMonth() == dayOfMonth) {
			cell.setToday(true);
		} else {
			cell.setToday(false);
		}

		// Check if the cells do not belong to the current month
		if (month - 1 != calendar.get(Calendar.MONTH)) {
			cell.setForeground(Color.gray);
		}
		// Increase 1 day
		calendar.add(Calendar.DATE, 1); 

		cell.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String[] options;
		if (e.getSource() instanceof Cell) {
			Cell cell = (Cell) e.getSource();

			if (cell.getFuture() == false && cell.getPeriod() == false) {
				options = new String[] { "Add notes", "Symptoms", "Add period" };
			} else if (cell.getFuture() == false && cell.getPeriod() == true) {
				options = new String[] { "Add notes", "Symptoms", "Delete period" };
			} else {
				options = new String[] { "Add notes", "Symptoms" };
			}

			int x = JOptionPane.showOptionDialog(null, "Select an option", "Menstruation Tracker",
					JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

			if (x == 0) { // Notes
				createNotesPane(cell.getPeriod(), cell);
			} else if (x == 1) { // Symptoms
				createSymptomsPane(cell);

			} else if (x == 2 && cell.getPeriod() == true) { // Delete period
				cell.setNewPeriod(false);
				cell.setCycle(false);
				cell.setCycle();
				pCal.calculateFreq();
				pCal.calculateLength();
				pCal.setNormal();

				expectedDaysList = new LinkedList();
				setExpectedDaysList();

				for (Cell c : cells) {
					c.setExpected();
				}
			} else if (x == 2 && cell.getPeriod() == false) { // Add period
				cell.setNewPeriod(true);
				cell.setCycle(true);
				cell.setCycle();
				pCal.calculateFreq();
				pCal.calculateLength();
				pCal.setNormal();

				expectedDaysList = new LinkedList();
				setExpectedDaysList();

				for (Cell c : cells) {
					c.setExpected();
				}
			}
		}
	}

	/**
	 * Create the pop-up symptoms panel when cell is clicked
	 * @param cell Cell object contained within the grid of the calendar 
	 */
	public static void createSymptomsPane(Cell cell) {
		JPanel symptomsPanel = new JPanel();
		symptomsPanel.setLayout(new GridBagLayout());

		GridBagConstraints gbd = new GridBagConstraints();

		gbd.gridx = 0;
		gbd.gridy = 0;
		gbd.anchor = GridBagConstraints.WEST;

		JLabel emotionsTitle = new JLabel("Emotions");
		JLabel symptomsTitle = new JLabel("Symptoms");
		JLabel dischargesTitle = new JLabel("Discharges");

		JRadioButton[] emotionsButton = new JRadioButton[9];
		JRadioButton[] symptomsButton = new JRadioButton[8];
		JRadioButton[] dischargesButton = new JRadioButton[6];

		String[] emotionsText = { "angry", "anxious", "calm", "craving", "depressed", "excited", "happy", "lazy",
				"motivated" };
		String[] symptomsText = { "acne", "bloated", "cramps", "headache", "insomnia", "tiredness", "weight loss",
				"weight gain" };
		String[] dischargesText = { "white", "clear and stretchy", "clear and watery", "yellow/green", "brown",
				"spotting blood" };

		String[] options = { "Save", "Cancel" };

		boolean duringPMS = expectedDaysList.traversePMS(cell.getDate());
		boolean duringOvulate = cell.getOvulate();

		symptomsPanel.add(emotionsTitle, gbd);
		gbd.gridy++;
		for (int i = 0; i < emotionsText.length; i++) {

			try {
				emotionsButton[i] = new JRadioButton();
				emotionsButton[i].setText(emotionsText[i]);
				String query = "SELECT * FROM emotions" + Login.getUsername() + " WHERE date = '"
						+ Cell.convertToDate(cell.getDate()) + "'" + ";";

				Statement statement = Database.getConnection().createStatement();
				ResultSet result = statement.executeQuery(query);

				int selected = 0;
				if (result.next()) {
					if (result.getInt(emotionsText[i]) == 1) {
						selected = 1;
					}
				}
				if (selected == 1) {
					emotionsButton[i].setSelected(true);
				}

				if (pCal.getToday().compareTo(cell.getDate()) <= 0) {

					if (pCal.getNormal() && duringOvulate) {
						if (cell.getSymptomOvulate("emotion", emotionsText[i])) {
							emotionsButton[i].setBackground(new Color(255, 229, 217));
						}

					} else if (pCal.getNormal() && duringPMS) {
						if (cell.getSymptomPMS("emotion", emotionsText[i])) {
							emotionsButton[i].setBackground(new Color(250, 225, 221));
						}
					}
				}
				symptomsPanel.add(emotionsButton[i], gbd);
				gbd.gridy++;
			}

			catch (SQLException e1) {
				JOptionPane.showMessageDialog(null, e1);
			}
		}

		gbd.gridy = 0;
		gbd.gridx++;
		symptomsPanel.add(symptomsTitle, gbd);
		gbd.gridy++;
		for (int i = 0; i < symptomsText.length; i++) {

			try {
				symptomsButton[i] = new JRadioButton();
				symptomsButton[i].setText(symptomsText[i]);

				String query = "SELECT * FROM symptoms" + Login.getUsername() + " WHERE date = '"
						+ Cell.convertToDate(cell.getDate()) + "'" + ";";

				Statement statement = Database.getConnection().createStatement();
				ResultSet result = statement.executeQuery(query);

				int selected = 0;
				if (result.next()) {
					if (result.getInt(symptomsText[i]) == 1) {
						selected = 1;
					}
				}
				if (selected == 1) {
					symptomsButton[i].setSelected(true);
				}

				if (pCal.getToday().compareTo(cell.getDate()) <= 0) {
					if (pCal.getNormal() && duringOvulate) {
						if (cell.getSymptomOvulate("symptom", symptomsText[i])) {
							symptomsButton[i].setBackground(new Color(255, 229, 217));
						}

					} else if (pCal.getNormal() && duringPMS) {
						if (cell.getSymptomPMS("symptom", symptomsText[i])) {
							symptomsButton[i].setBackground(new Color(250, 225, 221));
						}
					}
				}

				symptomsPanel.add(symptomsButton[i], gbd);
				gbd.gridy++;
			}

			catch (SQLException e1) {
				JOptionPane.showMessageDialog(null, e1);
			}
		}

		gbd.gridy = 0;
		gbd.gridx++;
		symptomsPanel.add(dischargesTitle, gbd);
		gbd.gridy++;
		for (int i = 0; i < dischargesText.length; i++) {

			// CHANGE LATER RRRR
			try {
				dischargesButton[i] = new JRadioButton();
				dischargesButton[i].setText(dischargesText[i]);
				String query = "SELECT * FROM discharges" + Login.getUsername() + " WHERE date = '"
						+ Cell.convertToDate(cell.getDate()) + "'" + ";";

				Statement statement = Database.getConnection().createStatement();
				ResultSet result = statement.executeQuery(query);

				int selected = 0;
				if (result.next()) {
					if (result.getInt(dischargesText[i]) == 1) {
						selected = 1;
					}
				}
				if (selected == 1) {
					dischargesButton[i].setSelected(true);
				}

				if (pCal.getToday().compareTo(cell.getDate()) <= 0) {
					if (pCal.getNormal() && duringOvulate) {
						if (cell.getSymptomOvulate("discharge", dischargesText[i])) {
							dischargesButton[i].setBackground(new Color(255, 229, 217));
						}

					} else if (pCal.getNormal() && duringPMS) {
						if (cell.getSymptomPMS("discharge", dischargesText[i])) {
							dischargesButton[i].setBackground(new Color(250, 225, 221));
						}
					}
				}

				symptomsPanel.add(dischargesButton[i], gbd);
				gbd.gridy++;
			}

			catch (SQLException e1) {
				JOptionPane.showMessageDialog(null, e1);
			}
		}
		gbd.gridx = 0;
		gbd.gridy = 10;
		gbd.gridwidth = 3;
		JLabel highlightLabel = new JLabel("*Highlighted texts indicate predicting symptoms");
		symptomsPanel.add(highlightLabel, gbd);

		// user input
		int x = JOptionPane.showOptionDialog(null, symptomsPanel, "Add symptoms", JOptionPane.DEFAULT_OPTION,
				JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
		if (x == 0) {
			for (int i = 0; i < emotionsText.length; i++) {
				try {
					String query = "SELECT * FROM emotions" + Login.getUsername() + " WHERE date = '"
							+ Cell.convertToDate(cell.getDate()) + "'" + ";";
					Statement statement = Database.getConnection().createStatement();
					ResultSet result = statement.executeQuery(query);

					if (result.next()) {
						query = "UPDATE emotions" + Login.getUsername() + " SET " + emotionsText[i] + " = ?"
								+ " WHERE date=" + "'" + Cell.convertToDate(cell.getDate()) + "'" + ";";
						PreparedStatement preparedStmt = Database.getConnection().prepareStatement(query);
						if (emotionsButton[i].isSelected()) {
							preparedStmt.setInt(1, 1);
						} else {
							preparedStmt.setInt(1, 0);
						}
						preparedStmt.executeUpdate();
					} else {
						query = "INSERT INTO emotions" + Login.getUsername() + " (date," + emotionsText[i] + ")"
								+ " VALUES (?,?)";

						PreparedStatement preparedStmt = Database.getConnection().prepareStatement(query);
						preparedStmt.setString(1, Cell.convertToDate(cell.getDate()));
						if (emotionsButton[i].isSelected()) {
							preparedStmt.setInt(2, 1);
						} else {
							preparedStmt.setInt(2, 0);
						}
						preparedStmt.executeUpdate();
					}
				}

				catch (SQLException e1) {
					JOptionPane.showMessageDialog(null, e1);
				}
			}

			for (int i = 0; i < symptomsText.length; i++) {
				try {
					String query = "SELECT * FROM symptoms" + Login.getUsername() + " WHERE date = '"
							+ Cell.convertToDate(cell.getDate()) + "'" + ";";
					Statement statement = Database.getConnection().createStatement();
					ResultSet result = statement.executeQuery(query);

					if (result.next()) {
						query = "UPDATE symptoms" + Login.getUsername() + " SET " + "\"" + symptomsText[i] + "\""
								+ " = ?" + " WHERE date = " + "'" + Cell.convertToDate(cell.getDate()) + "'" + ";";
						PreparedStatement preparedStmt = Database.getConnection().prepareStatement(query);
						if (symptomsButton[i].isSelected()) {
							preparedStmt.setInt(1, 1);
						} else {
							preparedStmt.setInt(1, 0);
						}
						preparedStmt.executeUpdate();
					} else {
						query = "INSERT INTO symptoms" + Login.getUsername() + " (date," + "\"" + symptomsText[i] + "\""
								+ ")" + " VALUES (?,?)";

						PreparedStatement preparedStmt = Database.getConnection().prepareStatement(query);
						preparedStmt.setString(1, Cell.convertToDate(cell.getDate()));
						if (symptomsButton[i].isSelected()) {
							preparedStmt.setInt(2, 1);
						} else {
							preparedStmt.setInt(2, 0);
						}
						preparedStmt.executeUpdate();
					}
				}

				catch (SQLException e1) {
					JOptionPane.showMessageDialog(null, e1);
				}
			}

			for (int i = 0; i < dischargesText.length; i++) {
				try {
					String query = "SELECT * FROM discharges" + Login.getUsername() + " WHERE date = '"
							+ Cell.convertToDate(cell.getDate()) + "'" + ";";
					Statement statement = Database.getConnection().createStatement();
					ResultSet result = statement.executeQuery(query);

					if (result.next()) {
						query = "UPDATE discharges" + Login.getUsername() + " SET " + "\"" + dischargesText[i] + "\""
								+ " = ?" + " WHERE date=" + "'" + Cell.convertToDate(cell.getDate()) + "'" + ";";
						PreparedStatement preparedStmt = Database.getConnection().prepareStatement(query);
						if (dischargesButton[i].isSelected()) {
							preparedStmt.setInt(1, 1);
						} else {
							preparedStmt.setInt(1, 0);
						}
						preparedStmt.executeUpdate();
					} else {
						query = "INSERT INTO discharges" + Login.getUsername() + " (date," + "\"" + dischargesText[i]
								+ "\"" + ")" + " VALUES (?,?)";

						PreparedStatement preparedStmt = Database.getConnection().prepareStatement(query);
						preparedStmt.setString(1, Cell.convertToDate(cell.getDate()));
						if (dischargesButton[i].isSelected()) {
							preparedStmt.setInt(2, 1);
						} else {
							preparedStmt.setInt(2, 0);
						}
						preparedStmt.executeUpdate();
					}
				}

				catch (SQLException e1) {
					JOptionPane.showMessageDialog(null, e1);
				}
			}
		}
	}

	/**
	 * Create the pop-up notes panel when the cell is clicked
	 * @param isPeriod Indicate if the client is on period
	 * @param cell Cell object contained within the grid of the calendar 
	 */
	public static void createNotesPane(boolean isPeriod, Cell cell) {
		JPanel notesPanel = new JPanel();
		notesPanel.setLayout(new GridBagLayout());

		GridBagConstraints gbd = new GridBagConstraints();
		gbd.anchor = GridBagConstraints.WEST;
		gbd.gridx = 0;
		gbd.gridy = 0;

		String[] options = { "Save", "Cancel" };
		JLabel notesLabel = new JLabel("Notes");
		notesLabel.setFont(new Font("Serif", Font.PLAIN, 15));
		JTextField notesText = new JTextField(10);

		if (isPeriod) {
			JLabel flowLabel = new JLabel("Period flow");
			flowLabel.setFont(new Font("Serif", Font.PLAIN, 15));

			ButtonGroup group = new ButtonGroup();
			JRadioButton lightButton = new JRadioButton("Light flow");
			JRadioButton medianButton = new JRadioButton("Median flow");
			JRadioButton heavyButton = new JRadioButton("Heavy flow");

			notesPanel.add(flowLabel, gbd);

			group.add(lightButton);
			group.add(medianButton);
			group.add(heavyButton);

			lightButton.setSelected(cell.getFlow(Cell.LIGHT) == 1);
			medianButton.setSelected(cell.getFlow(Cell.MID) == 1);
			heavyButton.setSelected(cell.getFlow(Cell.HEAVY) == 1);
			notesText.setText(cell.getNotes());

			gbd.gridy = 1;
			notesPanel.add(lightButton, gbd);
			gbd.gridy = 2;
			notesPanel.add(medianButton, gbd);
			gbd.gridy = 3;
			notesPanel.add(heavyButton, gbd);

			gbd.gridy = 4;
			notesPanel.add(notesLabel, gbd);

			gbd.gridy = 5;
			notesPanel.add(notesText, gbd);

			int x = JOptionPane.showOptionDialog(null, notesPanel, "Add notes", JOptionPane.DEFAULT_OPTION,
					JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
			if (x == 0) {
				if (lightButton.isSelected()) {
					cell.setFlow(Cell.LIGHT);
				} else if (medianButton.isSelected()) {
					cell.setFlow(Cell.MID);
				} else if (heavyButton.isSelected()) {
					cell.setFlow(Cell.HEAVY);
				}
				cell.setNotes(notesText.getText());
			}
		} else {
			gbd.gridy = 0;
			notesPanel.add(notesLabel, gbd);

			gbd.gridy = 1;
			notesPanel.add(notesText, gbd);

			notesText.setText(cell.getNotes());

			int x = JOptionPane.showOptionDialog(null, notesPanel, "Add notes", JOptionPane.DEFAULT_OPTION,
					JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
			if (x == 0) {
				cell.setNotes(notesText.getText());
			}
		}

	}

	/**
	 * Create a linked list of the list of expected menstruating days
	 */
	public void setExpectedDaysList() {

		// last day of current month
		Calendar tempCal = Calendar.getInstance();
		tempCal.set(Calendar.YEAR, year);
		tempCal.set(Calendar.MONTH, month - 1);
		tempCal.set(Calendar.DATE, 1);
		tempCal.set(Calendar.HOUR_OF_DAY, 0);
		tempCal.set(Calendar.MINUTE, 0);
		tempCal.set(Calendar.SECOND, 0);
		tempCal.set(Calendar.MILLISECOND, 0);
		tempCal.add(Calendar.DATE, 41);

		// Initializing the start of the linked list
		if (expectedDaysList.isEmpty()) {
			expectedDaysList.addLast();
		}
		if (expectedDaysList.getHeader().getStartDate() != null) {
			// expected days are calculated up to end of month
			while (expectedDaysList.getTrailer().getStartDate().compareTo(tempCal.getTime()) < 0) {
				expectedDaysList.addLast();
			}
		}

	}

	/**
	 * Discard the old list of expected menstruating dates and create a new list
	 */
	public static void refreshExpectedDaysList() {
		expectedDaysList = new LinkedList();
	}

	/**
	 * Getter method for the linked list of expected menstruating dates
	 * @return The linked list of expected menstruating 
	 */
	public static LinkedList getExpectedDaysList() {
		return expectedDaysList;
	}

}
