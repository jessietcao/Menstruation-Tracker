
import java.awt.Font;

/**
 * This class defines the overall template of the graph (e.g. width, height, width of the bars, etc.)
 */
public class GraphTemplate {
	
	//private int index;
	private int width;
	private int height;
	
	private int x;
	private int y;
	
	private int barWidth;
	
	private int gap;
	//get frequency array
	private Font font;
	
	private int labelHeight;

	public GraphTemplate(int itemsCount, int panelHeight) {
		x = 40;
		y = 25;
		barWidth = 15;
		gap = barWidth*7;
		//Dimensions of only the graph itself
		width = (barWidth+gap)*itemsCount;
		height = 300;
		font = new Font("Serif", Font.PLAIN, 15);
		//Dimensions of the graph + the labels
		labelHeight = font.getSize();
	}
	
	public int getLabelHeight(){
		return labelHeight;
	}
	public Font getFont() {
		return font;
	}
	
	public int getBarWidth() {
		return barWidth;
	}
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	public int getGap () {
		return gap;
	}

	public void setSize (int width, int height) {
		this.width = width;
		this.height = height;
		gap = width *2;
	}
	
	public int getHeight() {
		return height;
	}
	public int getWidth() {
		return width;
	}
	
}
