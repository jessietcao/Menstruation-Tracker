import javax.swing.*;

/**
 * Main class which the program runs on
 *
 */
public class Main {
	
	public static void main(String[] args) {
		
		JFrame frame = new JFrame("Menstruation Tracker");
		frame.setSize(700, 700);
		frame.setIconImage(new ImageIcon("img/cherry-blossom.png").getImage());
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel loginPage = new Login(frame);
	
		frame.add(loginPage);
		frame.setVisible(true);

	}

}
