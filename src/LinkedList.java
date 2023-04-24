import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

/**
 * Defines the linked list which holds the list of expected menstruating dates
 *
 */

public class LinkedList 
   {
      
      private Node header;
      private Node trailer;
      private int size;
      private PeriodCalculate pCal = Menu.getpCal();
   
      /**
       * Initializes the header, trailer, and the size of linked list
       */
      LinkedList ()
      {
         header = null;
         trailer = null;
         size = 0;
      }
      
      /**
       * Getter method for the header
       * @return Header node
       */
      public Node getHeader() {
    	  return header;
      }
      /**
       * Getter method for the trailer
       * @return Trailer node
       */
      public Node getTrailer() {
    	  return trailer;
      }
   
   
      //search in the list of expected days
      /**
       * Traverse the list of expected days
       * @param today The date that needs to be found
       * @return True if date exists in the list, false if it does not
       */
      public boolean traverse (Date today)
      {
         Node n = header;
         boolean found = false;
        
    	 while(n!=null&&n.getStartDate()!=null)
         {	
    		 //when the cell's date is during the time period of an expected menstruation date
    		 if(today.compareTo(n.getStartDate())>=0 && today.compareTo(n.getEndDate())<=0) {
    			 found = true;
    			 return found;
    		 }
    		 n = n.getNext();
         }
         return found;
      }
   
      
      //search in the list of pms days
      /**
       * Traverse the list to find if the inputed day is during the PMS period
       * @param today The date that is being compared with
       * @return True if the date is during PMS, false if not
       */
      public boolean traversePMS (Date today) {
    	  Node n = header;
          boolean duringPMS = false;
          
         
     	 while(n!=null&&n.getStartDate()!=null)
          {
     		int daysBetween =(int) (ChronoUnit.DAYS.between(today.toInstant(), n.getStartDate().toInstant()));
			//if the date of the cell is 4 days before the start of period 
			//or 2 days after period starts (where pms occurs)
			if (daysBetween<=4 && daysBetween >=-1) {
				duringPMS = true;
				return duringPMS;
			}
			n = n.getNext();
          }
          return duringPMS;
      }
      
      
      /**
       * Add a node to the end of the list of expected periods
       */
      public void addLast() {
    	  if (size == 0) {
    		  Date firstFirstDay = firstExpectedFirstDay();
    		  
    		  header = new Node (firstFirstDay, expectedLastDay(firstFirstDay));
    		  trailer = new Node (firstFirstDay, expectedLastDay(firstFirstDay));
    	  }
    	  else if (size ==1){
    		  Date firstFirstDay = header.getStartDate();
    		  Date nextFirstDay = nextExpectedFirstDay(firstFirstDay);
    		 
    		  trailer = new Node (nextFirstDay, expectedLastDay(nextFirstDay));
    		  header.setNext(trailer);
    	  }
    	  else {
    		  
    		  Date firstFirstDay = trailer.getStartDate();
    		  Date nextFirstDay = nextExpectedFirstDay(firstFirstDay);
    		  
    		  Node n = new Node (nextFirstDay, expectedLastDay(nextFirstDay));
    		  trailer.setNext(n);
    		  trailer=n;
    	  }
    	  size++;
      }
      
      /**
       *Find the first expected period date 
       * @return starting date of the next first expected period
       */
      public Date firstExpectedFirstDay() {
  		String query = "SELECT * FROM  Cycle"+Login.getUsername()
  		+" WHERE startDate = (SELECT MAX(startDate) FROM Cycle"+Login.getUsername()+ ")";
  		
  		Date day = null;
  		Date endDate = null;
  		
  		try {
  		
  		PreparedStatement preparedStmt = Database.getConnection().prepareStatement(query);
  		ResultSet result = preparedStmt.executeQuery();
  		
  		if(result.next()) {
  			day = new SimpleDateFormat("yyyy-MM-dd").parse(result.getString("startDate"));
  			endDate = new SimpleDateFormat("yyyy-MM-dd").parse(result.getString("endDate"));
  			}
  		}
  		catch (ParseException e) {
  			e.printStackTrace();
  		}
  		catch (SQLException e) {
  		e.printStackTrace();}
  		
  		if(day!=null) {
  			Calendar tempCal = Calendar.getInstance();
  			tempCal.setTime(day);

  			tempCal.set(Calendar.HOUR_OF_DAY, 0);
  			tempCal.set(Calendar.MINUTE, 0);
  			tempCal.set(Calendar.SECOND, 0);
  			tempCal.set(Calendar.MILLISECOND, 0);
  			tempCal.add(Calendar.DATE, pCal.getLength()-1);
  			
  			if(endDate.compareTo(tempCal.getTime())>0) {
  				tempCal.setTime(day);

  				tempCal.set(Calendar.HOUR_OF_DAY, 0);
  				tempCal.set(Calendar.MINUTE, 0);
  				tempCal.set(Calendar.SECOND, 0);
  				tempCal.set(Calendar.MILLISECOND, 0);
  				
  				tempCal.add(Calendar.DATE, pCal.getFreq());
  				day = tempCal.getTime();
  			}
  		}
  		
  		return day;
  	}
      
    //Next last day of period
    /**
     * Find the ending date of period 
     * @param day First day of period
     * @return Last day of predicted period
     */
  	public Date expectedLastDay(Date day) {  		
  		if(day!=null) {
  			Calendar tempCal = Calendar.getInstance();
  			tempCal.setTime(day);

  			tempCal.set(Calendar.HOUR_OF_DAY, 0);
  			tempCal.set(Calendar.MINUTE, 0);
  			tempCal.set(Calendar.SECOND, 0);
  			tempCal.set(Calendar.MILLISECOND, 0);
  			tempCal.add(Calendar.DATE, pCal.getLength()-1);
  			
  			day = tempCal.getTime();
  		}
  		return day;
  	}
  	
  	/**
  	 * Find the next expected period date
  	 * @param day The recorded first day of the latest cycle
  	 * @return The expected start of the next period
  	 */
  	public Date nextExpectedFirstDay(Date day) {
		if(day!=null) {
			Calendar tempCal = Calendar.getInstance();
			tempCal.setTime(day);

			tempCal.set(Calendar.HOUR_OF_DAY, 0);
			tempCal.set(Calendar.MINUTE, 0);
			tempCal.set(Calendar.SECOND, 0);
			tempCal.set(Calendar.MILLISECOND, 0);
			
			tempCal.add(Calendar.DATE, pCal.getFreq());
			
			day = tempCal.getTime();
			}
		return day;
		
	}
	
   
   /**
    * Getter method for size of the linked list
    * @return Size of the linked list
    */
      public int getSize ()
      {
         return size;
      }
   
      /**
       * Getter method for if the list is empty
       * @return True for empty, false for not-empty
       */
   
      public boolean isEmpty ()
      {
         return size == 0;
      }
   
   }