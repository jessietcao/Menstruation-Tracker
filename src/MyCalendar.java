import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Formats the overall panel for the calendar function
 *
 */
public class MyCalendar extends JPanel implements ActionListener{
	private static GridBagConstraints gbd;
	private JButton menuButton;
	private JFrame frame;

	/**
	 * Defines the overall layout of the calendar panel, including the panel with calendar, labels, etc.
	 * @param frame Main JFrame which holds all of the panels
	 */
	public MyCalendar(JFrame frame) {
		this.frame = frame;
		this.setLayout(new GridBagLayout());
		gbd = new GridBagConstraints();
		
		CalendarPanel calendarPanel = new CalendarPanel();
		gbd.gridx = 0;
		gbd.gridy = 0;

		this.add(calendarPanel,gbd);
		
		gbd.gridy = 1;
		Shapes shape = new Shapes();
		
		JPanel labelPanel = new JPanel();
		labelPanel.setLayout(new GridBagLayout());
		
		gbd.gridy = 0;
		gbd.gridheight = 3;
		
		labelPanel.add(shape,gbd);
		
		JLabel periodLabel = new JLabel("Menstruation");
		periodLabel.setFont(new Font("Serif", Font.PLAIN, 15));
		JLabel ovulateLabel = new JLabel ("Ovulation");
		ovulateLabel.setFont(new Font("Serif", Font.PLAIN, 15));
		JLabel expectedLabel = new JLabel ("Expected period");
		expectedLabel.setFont(new Font("Serif", Font.PLAIN, 15));
		
		gbd.gridx = 1;
		gbd.gridheight=1;
		gbd.anchor = GridBagConstraints.WEST;
		labelPanel.add(periodLabel,gbd);
		gbd.gridy = 1;
		labelPanel.add(ovulateLabel,gbd);
		gbd.gridy=2;
		gbd.gridwidth = 1;
		labelPanel.add(expectedLabel, gbd);
		labelPanel.setBackground(new Color (248, 237, 235));
		gbd.gridx=0;
		gbd.gridy = 1;
		this.add(Box.createVerticalStrut(10),gbd);
		gbd.gridy = 2;
		this.add(labelPanel,gbd);
		
		gbd.gridy=3;
		gbd.anchor = GridBagConstraints.EAST;
		menuButton = new JButton ("Home");
		menuButton.addActionListener(this);
		menuButton.setBackground(new Color (232, 232, 228));
		this.add(menuButton,gbd);
		
		this.setBackground(new Color(248, 237, 235));
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==menuButton) {
			Menu menu = new Menu(frame);
			frame.getContentPane().remove(this);
            frame.getContentPane().add(menu);
            frame.invalidate();
            frame.validate();
		}
		
	}

}
