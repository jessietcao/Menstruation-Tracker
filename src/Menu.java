import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Formats the overall panel for the menu function
 *
 */
public class Menu extends JPanel implements ActionListener{
	
	private static JPanel displayPanel;
	private static JPanel optionsPanel;
	private static JPanel daysPanel;
	private static JPanel nextCyclePanel;
	private static JButton daysLabel;
	private static JLabel nextCycleLabel;
	private static JButton calendarButton;
	private static JButton cycleButton;
	private static JButton settingsButton;
	private static GridBagConstraints gbd;
	private JFrame frame;
	private static PeriodCalculate pCal;

	/**
	 * Defines the overall layout for the menu panel, including the display of completion of cycle, buttons which lead to different functions, etc.
	 * @param frame Main JFrame which holds all of the panels
	 */
	public Menu(JFrame frame) {
		this.frame = frame;
		
		
		this.setLayout(new GridBagLayout());
		gbd = new GridBagConstraints();
		pCal= new PeriodCalculate();
		
		
		displayPanel = new JPanel();
		displayPanel.setLayout(new GridBagLayout());
		optionsPanel = new JPanel();
		optionsPanel.setLayout(new GridBagLayout());
		daysPanel = new JPanel();
		daysPanel.setLayout(new BoxLayout(daysPanel, BoxLayout.X_AXIS));
		nextCyclePanel = new JPanel();
		nextCyclePanel.setLayout(new BoxLayout(nextCyclePanel, BoxLayout.X_AXIS));
		daysLabel = new JButton("");
		
		daysLabel.addActionListener(this);
		daysLabel.setPreferredSize(new Dimension(400, 150));
		daysLabel.setEnabled(false);
		daysLabel.setBackground(Color.white);
		
		nextCycleLabel = new JLabel("");
		nextCycleLabel.setFont(new Font("Franklin Gothic Medium", Font.BOLD, 20));
		nextCycleLabel.setPreferredSize(new Dimension(400, 100));
		nextCycleLabel.setForeground(Color.gray);
		
		
		if(pCal.getNoData()||pCal.getPaused()||pCal.getNextDate()<0)
		{
			daysLabel.setText("Click to begin new cycle");
			daysLabel.setFont(new Font("Impact", Font.PLAIN, 30));
			daysLabel.setEnabled(true);
			nextCycleLabel.setText("No current data to be displayed");
		}
		else {
			daysLabel.setText("Day "+pCal.getProgressDate()+" of period");
			daysLabel.setFont(new Font("Impact", Font.PLAIN, 50));

			if (pCal.getNextDate()==0) {
				nextCycleLabel.setText("Next cycle starts today");
			}
			else if (pCal.getNextDate()==1) {
				nextCycleLabel.setText("Next cycle starts in 1 day");
			}
			else {
				nextCycleLabel.setText("Next cycle starts in "+pCal.getNextDate()+" days");
			}
		}
		
		daysPanel.add(Box.createHorizontalStrut(20));
		daysPanel.add(daysLabel);
		daysPanel.setBackground(Color.white);
		
		
		nextCyclePanel.add(Box.createHorizontalStrut(20));
		nextCyclePanel.add(nextCycleLabel);
		nextCyclePanel.setBackground(Color.white);
	
		gbd.gridx=0;
		gbd.gridy=0;
		displayPanel.add(daysPanel,gbd);
		
		gbd.gridy=1;
		displayPanel.add(nextCyclePanel,gbd);
		
		calendarButton = new JButton("Calendar");
		calendarButton.setPreferredSize(new Dimension(420,50));
		calendarButton.setBackground(new Color(248, 237, 235));
		calendarButton.addActionListener(this);
		gbd.gridy=0;
		optionsPanel.add(calendarButton,gbd);
		
		gbd.gridy=1;
		optionsPanel.add(Box.createVerticalStrut(20),gbd);
		
		cycleButton = new JButton ("Menstrual Cycle");
		cycleButton.setPreferredSize(new Dimension(420,50));
		cycleButton.setBackground(new Color(248, 237, 235));
		cycleButton.addActionListener(this);
		gbd.gridy=2;
		optionsPanel.add(cycleButton,gbd);
		
		gbd.gridy=3;
		optionsPanel.add(Box.createVerticalStrut(20),gbd);
		
		settingsButton = new JButton ("Settings");
		settingsButton.setPreferredSize(new Dimension(420,50));
		settingsButton.setBackground(new Color(248, 237, 235));
		settingsButton.addActionListener(this);
		gbd.gridy =4;
		optionsPanel.add(settingsButton,gbd);
		optionsPanel.setBackground(new Color(255, 229, 217));
		
		gbd.gridy=0;
		this.add(displayPanel,gbd);
		gbd.gridy=1;
		this.add(Box.createVerticalStrut(20),gbd);
	
		gbd.gridy=2;
		this.add(optionsPanel, gbd);
		this.setBackground(new Color(255, 229, 217));
		
	}
	
		@Override
		public void actionPerformed(ActionEvent e) {
				
			if(e.getSource() == calendarButton||e.getSource()==daysLabel) {
				MyCalendar calendar = new MyCalendar(frame);
				frame.getContentPane().remove(this);
                frame.getContentPane().add(calendar);
                frame.invalidate();
                frame.validate();
			}
				
			else if (e.getSource()==cycleButton) {
				Cycle cycle = new Cycle(frame);
				frame.getContentPane().remove(this);
                frame.getContentPane().add(cycle);
                frame.invalidate();
                frame.validate();
				
			}
			else if (e.getSource()==settingsButton) {
				Settings settings = new Settings(frame);
				frame.getContentPane().remove(this);
                frame.getContentPane().add(settings);
                frame.invalidate();
                frame.validate();
				
			}
		}
		
		/**
		 * Getter method for the periodCalculate object
		 * @return The periodCalculate object
		 */
		public static PeriodCalculate getpCal() {
			return pCal;
		}
		
}
