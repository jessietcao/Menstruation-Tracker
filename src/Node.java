import java.util.Date;

/**
 * Node class for expected periods that make up the linked list
 *
 */

public class Node {

	 private Date startDate;
	 private Date endDate;
     private Node next;
  
     /**
      * When the node defined has a node following it
      * @param startDate Starting date of the period
      * @param endDate Ending date of the period
      * @param next Next expecting cycle of period
      */
      public Node (Date startDate, Date endDate, Node next)
     {
        this.startDate = startDate;
        this.endDate = endDate;
        this.next = next;
     }
      /**
       * When the node defined is the last node in the linked list
       * @param startDate Starting date of the period
       * @param endDate Ending date of the period
       */
      public Node (Date startDate, Date endDate)
     {
		this.startDate = startDate;
	    this.endDate = endDate;
        this.next = null;
     }
  
  
      /**
       * Setter method for starting and ending dates of the period cycle
       * @param startDate Starting date of the period
       * @param endDate Ending date of the period
       */
      public void setData (Date startDate, Date endDate)
     {
        this.startDate = startDate;
        this.endDate = endDate;
     }
  
  
      /**
       * Setter method for next expected period 
       * @param next Next node 
       */
      public void setNext (Node next)
     {
        this.next = next;
     }
  
  
      /**
       * Getter method for the starting date of period
       * @return Starting date of period
       */
      public Date getStartDate ()
     {
        return startDate;
     }
      
      /**
       * Getter method of the ending date of period
       * @return Ending date of period
       */
      public Date getEndDate() {
    	  
    	  return endDate;
      }
  
  
      /**
       * Getter method of the next expecting period 
       * @return Next node 
       */
      public Node getNext ()
     {
        return next;
     }
}
