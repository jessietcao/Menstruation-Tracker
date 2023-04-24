import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * 
 * This class draws the graph which represents the inputed period cycles
 *
 */
public class Graph extends JPanel{

	private GraphTemplate template;
	private String[] title;
	private int[]frequency;
	private FontMetrics fontWidth;
	private int max;
	
	/**
	 * 
	 * @param title A list of dates that lie along the x-axis of the graph 
	 * @param frequency A list of frequencies that are used to represent the y-values of the graph
	 */
	public Graph(String [] title, int[] frequency) {
		this.setLayout(new BorderLayout());
		
		this.title = title;
		this.frequency = frequency;

		if(frequency.length==0||frequency.length==1) {
			max = 0;
		}
		else {
			 max = findMax(frequency)+20;
		}
		
		template = new GraphTemplate(frequency.length,getHeight());
		this.setPreferredSize(new Dimension (template.getWidth(),template.getHeight()));
		this.setBackground(new Color (80,80,80));
	}

	@Override
    public void paintComponent(Graphics g){
    	super.paintComponent(g);
    	if (title.length != 0 && title.length!=1)
    	{
    		paintBars(g);
    		paintBoarder(g);
    	}
        
    }
	/**
	 * Find the maximum frequency out of the list of cycles
	 * @param frequency List of frequencies
	 * @return Maximum frequency
	 */
    private int findMax(int[] frequency)
    {
    	max = frequency[0];
    	for ( int i = 1; i<frequency.length; i++) {
    		if(frequency[i]>max) {
    			max = frequency[i];
    		}
    	}
    	return max;
    }
    
    /**
     * Draw the bars in the bar graph
     * @param g Graphics component
     */
    private void paintBars(Graphics g){
        g.setFont(template.getFont());
        
        fontWidth = g.getFontMetrics(template.getFont());
        
        for (int i =0; i<frequency.length;i++){
            
        	
        	//Find the percentage of the bar height of the given area
        	int barHeight = ((int)((double)frequency[i]/max * template.getHeight()));
        
        	//Bars
            int x = template.getX()+ i *template.getGap();
            int y = template.getY()+ template.getHeight() - barHeight;
            
            //Deciding if the cycle is irregular
            //Irregular
            if(frequency[i]<21||frequency[i]>35) {
            	g.setColor(new Color (216, 226, 220));
            }
            //Regular
            else {
            	g.setColor(new Color (254, 197, 187));
            }
            g.fillRect(x, y, template.getBarWidth(), barHeight);
            
            //Labels
            	int titleWidth = fontWidth.stringWidth(title[i]);
	            if (titleWidth < template.getBarWidth()){
	                x = x + ((template.getBarWidth() - titleWidth)/2);
	            } else {
	                x = x - ((titleWidth - template.getBarWidth())/2);
	            }
	            y = template.getY() + template.getHeight() + template.getLabelHeight();
	            g.drawString(title[i], x, y);
            }
        	
        
    }
    
    /**
     * Draw the border of the bar graph
     * @param g Graphics component
     */
    private void paintBoarder(Graphics g){
        g.setColor(Color.white);
        g.drawRect(template.getX(), template.getY(), 
        		template.getWidth(), template.getHeight());
        int interval = 0;
        while (interval*6<max) {
        	interval+=10;
        }
        
        for ( int i =interval; i<max; i+=interval)
        {
        	 int gridHeight = ((int)((double)i/max * template.getHeight()));
        	 int labelY = template.getY()+template.getHeight()-gridHeight;
        	 int labelX = template.getX()-fontWidth.stringWidth(""+i)-5;
        	 g.drawString(i+"", labelX, labelY);
        }
       
    	
    }
 
}
