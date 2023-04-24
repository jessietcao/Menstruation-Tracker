import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * 
 * Formats the panel that holds the calendar 
 *
 */
public class CalendarPanel extends JPanel implements ActionListener {
	private static GridBagConstraints gbd;

	private static int year;
	private static int month;

	private static BufferedImage left;
	private static BufferedImage right;
	private static JButton leftButton;
	private static JButton rightButton;
	private static Dates dates;

	private static Date date;
	private static LocalDate localDate;

	/**
	 * Defines the basic layout of the calendar panel (including left and right buttons, current date, etc.)
	 */
	public CalendarPanel() {
		this.setLayout(new GridBagLayout());
		gbd = new GridBagConstraints();

		date = new Date();
		localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		year = localDate.getYear();
		month = localDate.getMonthValue();
		// from 1 to 12

		try {
			left = ImageIO.read(new File("img/arrow-left.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			right = ImageIO.read(new File("img/arrow-right.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		leftButton = new JButton(new ImageIcon(left));
		leftButton.setContentAreaFilled(false);
		leftButton.setFocusPainted(false);
		leftButton.setBorderPainted(false);
		leftButton.addActionListener(this);
		rightButton = new JButton(new ImageIcon(right));
		rightButton.setContentAreaFilled(false);
		rightButton.setFocusPainted(false);
		rightButton.setBorderPainted(false);
		rightButton.addActionListener(this);
		gbd.gridx = 0;
		gbd.gridy = 0;
		this.add(Box.createVerticalStrut(20), gbd);
		gbd.gridx = 0;
		gbd.gridy = 1;
		this.add(leftButton, gbd);
		gbd.gridx = 2;
		this.add(rightButton, gbd);
		gbd.gridx = 1;
		gbd.gridy = 2;
		dates = new Dates(year, month);
		this.add(dates, gbd);

		gbd.gridx = 0;
		gbd.gridy = 3;
		this.add(Box.createVerticalStrut(20), gbd);
		this.setBackground(new Color(250, 225, 221));
		gbd.gridx = 1;
		gbd.gridy = 2;

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == leftButton) {
			leftClicked();
		} else if (e.getSource() == rightButton) {
			rightClicked();
		}

	}

	/**
	 * When the right arrow is clicked in the calendar panel, this function is called;
	 * used to increase the calendar month by one
	 */
	private void rightClicked() {
		// new year
		if (month == 12) {
			month = 1;
			year++;
		}
		// new month
		else {
			month++;
		}
		this.remove(dates);
		dates = new Dates(year, month);
		this.add(dates, gbd);
		this.invalidate();
		this.validate();

	}

	/**
	 * When the left arrow is clicked in the calendar panel, this function is called;
	 * used to decrease the calendar month by one
	 */
	private void leftClicked() {
		if (month == 1) {
			month = 12;
			year--;
		} else {
			month--;
		}
		this.remove(dates);
		dates = new Dates(year, month);
		this.add(dates, gbd);
		this.invalidate();
		this.validate();
	}

	/**
	 * Returns the current date of user's area
	 * @return LocalDate Local current date 
	 */
	public static LocalDate getLocalDate() {
		return localDate;
	}
}
