import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * Functional class which helps drawing the shapes used in the calendar panel
 */
public class Shapes extends JPanel{

	/**
	 * Defines the colours and the dimensions 
	 */
	public Shapes() {
		
		this.setPreferredSize(new Dimension (100,100));
		this.setBackground(new Color (248, 237, 235));
	}

	
	@Override
	public void paintComponent(Graphics g) {

			super.paintComponent(g);
			
			g.setColor(new Color (254, 197, 187));  
		    g.fillRect(0,0,20,20); 
		    g.setColor(Color.black);
		    g.drawRect(0,0,20,20); 
		    
		    g.setColor(new Color (255, 229, 217));  
		    g.fillRect(0,30,20,20); 
		    g.setColor(Color.black);
		    g.drawRect(0,30,20,20); 
		    
		      
		    g.setColor(new Color (232, 232, 228));
		    g.fillRect(0,60,20,20);
		    g.setColor(Color.black);
		    g.drawRect(0,60,20,20);
		    
		    
	  }
	  

 
   
}
