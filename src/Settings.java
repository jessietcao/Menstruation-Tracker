import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Formats the overall panel for the settings function
 *
 */
public class Settings extends JPanel implements ActionListener, ChangeListener{

	private JButton menuButton;
	private static GridBagConstraints gbd;
	private JFrame frame;
	private static Font font = new Font ("Calibri", Font.PLAIN, 20);
	private JRadioButton preg;
	private JSlider freqSlider;
	private JSlider lengthSlider;
	private JLabel freqValueLabel;
	private JLabel lenValueLabel;
	private JButton changeButton;
	private JButton restoreButton;
	private static PeriodCalculate pCal;
	
	/**
	 * Defines the overall layout of the settings panel, including the sliders, labels, etc.
	 * @param frame Main JFrame which holds all of the panels
	 */
	public Settings(JFrame frame) {
		
		this.frame = frame;
		pCal = Menu.getpCal();
		this.setLayout(new GridBagLayout());
		gbd = new GridBagConstraints();
		
		preg= new JRadioButton("During pregnancy/ breastfeeding, or on contraceptives");
		preg.setFont(font);
		preg.setBackground(new Color (248, 237, 235));
		
		if(pCal.getPaused()) {
			preg.setSelected(true);
		}
		
		gbd.gridx=0;
		gbd.gridy=0;
		this.add(preg,gbd);
		
		if(pCal.getNormal()||pCal.getChanged()) {
			freqSlider = new JSlider(21,35,pCal.getFreq());
			lengthSlider = new JSlider(2,7,pCal.getLength());
		}
		else {
			freqSlider = new JSlider(21,35,28);
			lengthSlider = new JSlider(2,7,5);
		}
		
		freqSlider.setBackground(new Color (248, 237, 235));
		lengthSlider.setBackground(new Color (248, 237, 235));
		freqSlider.addChangeListener(this);
		lengthSlider.addChangeListener(this);
		
		
		gbd.gridy++;
		this.add(Box.createVerticalStrut(30),gbd);
		gbd.gridy++;
		JLabel freqLabel = new JLabel("Frequency");
		freqLabel.setFont(font);
		this.add(freqLabel,gbd);
		gbd.gridy++;
		this.add(freqSlider,gbd);
		gbd.gridy++;
		freqValueLabel=new JLabel (freqSlider.getValue()+"");
		freqValueLabel.setFont(font);
		this.add(freqValueLabel,gbd);
		
		gbd.gridy++;
		this.add(Box.createVerticalStrut(30),gbd);
		
		gbd.gridy++;
		JLabel lenLabel=new JLabel ("Period length");
		lenLabel.setFont(font);
		this.add(lenLabel,gbd);
		gbd.gridy++;
		this.add(lengthSlider,gbd);
		gbd.gridy++;
		lenValueLabel = new JLabel (lengthSlider.getValue()+"");
		lenValueLabel.setFont(font);
		this.add(lenValueLabel,gbd);
		
		gbd.gridy++;
		this.add(Box.createVerticalStrut(30),gbd);
		
		gbd.gridy++;
		
		changeButton = new JButton ("Save changes");
		changeButton.addActionListener(this);
		changeButton.setBackground(Color.white);
		restoreButton = new JButton ("Restore values");
		restoreButton.addActionListener(this);
		restoreButton.setBackground(Color.white);
		
		this.add(changeButton,gbd);
		gbd.gridy++;
		this.add(Box.createVerticalStrut(20),gbd);
		gbd.gridy++;
		this.add(restoreButton,gbd);
		gbd.gridy++;
		this.add(Box.createVerticalStrut(30),gbd);
		
		gbd.gridy++;
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
            
            Dates.refreshExpectedDaysList();
            
            frame.invalidate();
            frame.validate();
		}
		else if(e.getSource()==changeButton) {
			JOptionPane.showMessageDialog(null,  "Values changed");
			pCal.setChanged(true);
			pCal.setChangedFreq(freqSlider.getValue());
			pCal.setChangedLength(lengthSlider.getValue());
			
			Dates.refreshExpectedDaysList();
			
			if(preg.isSelected()) {
				pCal.setPaused(true);
			}
			else {
				pCal.setPaused(false);
			}
		}
		else if (e.getSource()==restoreButton) {
			JOptionPane.showMessageDialog(null,  "Values restored");
			pCal.setChanged(false);
			
			Dates.refreshExpectedDaysList();
			
			this.remove(freqSlider);
			this.remove(lengthSlider);

			
			if(pCal.getNormal()) {
				freqSlider = new JSlider(21,35,pCal.getFreq());
				lengthSlider = new JSlider(2,7,pCal.getLength());
				lenValueLabel.setText(pCal.getLength()+"");
				freqValueLabel.setText(pCal.getFreq()+"");
			}
			else {
				freqSlider = new JSlider(21,35,28);
				lengthSlider = new JSlider(2,7,5);
				lenValueLabel.setText(5+"");
				freqValueLabel.setText(28+"");
			}
			
			freqSlider.setBackground(new Color (248, 237, 235));
			lengthSlider.setBackground(new Color (248, 237, 235));
			freqSlider.addChangeListener(this);
			lengthSlider.addChangeListener(this);
			gbd.gridy = 3;
			this.add(freqSlider,gbd);
			gbd.gridy = 7;
			this.add(lengthSlider,gbd);
			this.invalidate();
			this.validate();
			preg.setSelected(false);
			pCal.setPaused(false);
			
			
		}
		
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		if (e.getSource()==freqSlider) {
			freqValueLabel.setText(freqSlider.getValue()+"");
		}
		else {
			lenValueLabel.setText(lengthSlider.getValue()+"");
		}
		
	}

}
