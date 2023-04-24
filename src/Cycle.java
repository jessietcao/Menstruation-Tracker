import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

/**
 * Formats the overall panel for the cycle function
 *
 */
public class Cycle extends JPanel implements ActionListener{

	private JTable table;
	private String[] title;
	private String[][] data;
	private int[] frequency;
	private String[] titleOfBars;
	private JLabel availableText;
	private Graph graph;
	private JButton menuButton;
	private JFrame frame;
	
	/**
	 * Defines the overall layout of the cycle panel, including the JTable, JLabels, and the JPanel for the graph
	 * @param frame Main JFrame which holds all of the panels
	 */
	public Cycle(JFrame frame) {
		this.frame = frame;
		this.setLayout(new GridBagLayout());
		GridBagConstraints gbd = new GridBagConstraints();
		
		title = new String[] {"Start Date","Duration"};
		availableText = new JLabel("");
		
		gbd.gridx=0;
		gbd.gridy=0;
		this.add(availableText,gbd);
		initTable();
		gbd.gridy=1;
		this.add(Box.createVerticalStrut(10),gbd);
		
		graph = new Graph(titleOfBars,frequency);
	    
		table = new JTable(data, title);
	    table.setPreferredScrollableViewportSize(table.getPreferredSize());
	    table.setFillsViewportHeight(true);
	
	    
	    JScrollPane tablePanel = new JScrollPane(table);
	    JScrollPane graphPanel = new JScrollPane(graph);
	    
		graphPanel.setPreferredSize(new Dimension(500,400));
	  
		gbd.gridy=2;
		this.add(graphPanel,gbd);
		gbd.gridy=3;
		this.add(Box.createVerticalStrut(10),gbd);
		gbd.gridy=4;
		tablePanel.setPreferredSize(new Dimension(500,150));
	    this.add(tablePanel,gbd);
		
		gbd.gridy+=1;
		this.add(Box.createVerticalStrut(10),gbd);
		gbd.gridy+=1;
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
	
	public void initTable() {
		
		String query = "SELECT COUNT(*) FROM Cycle"+Login.getUsername()
		+" WHERE startDate != (SELECT MIN(startDate) FROM Cycle"+Login.getUsername()+ ")";;
		int row = 0;
		try {
			Statement statement = Database.getConnection().createStatement();
			ResultSet result = statement.executeQuery(query);
			if(result.next()) {
				row = result.getInt("COUNT(*)");
			}
		}
		
		catch (SQLException e) {
			e.printStackTrace();
		}
		
		data = new String [row][2];
		titleOfBars = new String [row];
		Date[] titleOfBarsDate = new Date [row];
		
		frequency = new int [row];
		int count = 0;
		
		if (row==0 || row ==1) {
			availableText.setText("Not enough data");
			availableText.setFont(new Font("Impact", Font.PLAIN, 30));
		}
		else {
			availableText.setText("Cycles");
			availableText.setFont(new Font("Impact", Font.PLAIN, 30));
			query = "SELECT * FROM Cycle"+Login.getUsername()
			+" WHERE startDate != (SELECT MIN(startDate) FROM Cycle"+Login.getUsername()+ ")";
			try {
				
				Statement statement = Database.getConnection().createStatement();
				ResultSet result = statement.executeQuery(query);
				while(result.next()) {
					String date = result.getString("startDate");
					int dur = result.getInt("frequency");
					
					frequency[count]=dur;
					
				    Date dateDate=new SimpleDateFormat("yyyy-MM-dd").parse(date);  
					
					titleOfBarsDate[count]=dateDate;
					titleOfBars[count]=date;
					count++;
				}
		}
			
			catch (SQLException e) {
				e.printStackTrace();} 
			catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		
			quickSort(titleOfBarsDate, titleOfBars, frequency);
			
			for(int i = 0; i<frequency.length; i++) {
				data[i][0]=titleOfBars[i];
				
				int dur = frequency[i];
				
				String durString="";
				if (dur<21 || dur>35) {
					durString+=dur+"*";
				}
				else {
					durString+=dur;
				}
				data[i][1]=durString;
			}
		
		}
	}
	
	
	/**
	 * Sorts the sqlite strings in the order of the text instead
	 * @param list List of Date object titles used for sorting
	 * @param listString List of String object titles used for x-axis of the graph
	 * @param frequency List of frequencies stored for each date
	 */
	public static void quickSort (Date[] list,String[] listString,int[]frequency)
	{
	  quickSort(list,listString,frequency,0,list.length-1);
	}
	/**
	 * Recursive algorithm used for sorting
	 * @param list List of Date object titles used for sorting
	 * @param listString List of String object titles used for x-axis of the graph
	 * @param frequency List of frequencies stored for each date
	 * @param low Starting point in sorting
	 * @param high Ending point in sorting
	 */
	private static void quickSort (Date[] list,String[] listString,int[]frequency, int low, int high)
	{
	  final int MOVING_LEFT = 0;
	  final int MOVING_RIGHT = 1;

	  if (low < high)
	  {
	    int left = low;
	    int right = high;
	    int currentDirection = MOVING_LEFT;
	    Date pivot = list[low];
	    String stringPivot = listString[low];
	    int pivotInt = frequency[low];
	    while (left < right)
	    {
	      if (currentDirection == MOVING_LEFT)
	      {
	        while (list[right].compareTo(pivot) >= 0
	               && left < right)
	          right--;
	        list[left] = list[right];
	        listString[left]=listString[right];
	        frequency[left]=frequency[right];
	        currentDirection = MOVING_RIGHT;
	      }
	      if (currentDirection == MOVING_RIGHT)
	      {
	        while (list[left].compareTo(pivot) <= 0 
	               && left < right)
	          left++;
	        list[right] = list[left];
	        listString[right]=listString[left];
	        frequency[right]=frequency[left];
	        currentDirection = MOVING_LEFT;
	      }
	    }
	    list[left] = pivot; 
	    listString[left]= stringPivot;
	    frequency[left]=pivotInt;
	    quickSort(list,listString,frequency, low,left-1);
	    quickSort(list,listString,frequency, right+1,high);
	  }
	}


	
	

}
