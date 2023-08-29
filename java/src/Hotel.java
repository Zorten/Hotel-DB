/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.lang.Math;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */
public class Hotel {

   // reference to physical database connection.
   private Connection _connection = null;

   // handling the keyboard inputs through a BufferedReader
   // This variable can be global for convenience.
   static BufferedReader in = new BufferedReader(
                                new InputStreamReader(System.in));

   /**
    * Creates a new instance of Hotel 
    *
    * @param hostname the MySQL or PostgreSQL server hostname
    * @param database the name of the database
    * @param username the user name used to login to the database
    * @param password the user login password
    * @throws java.sql.SQLException when failed to make a connection.
    */
   public Hotel(String dbname, String dbport, String user, String passwd) throws SQLException {

      System.out.print("Connecting to database...");
      try{
         // constructs the connection URL
         String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
         System.out.println ("Connection URL: " + url + "\n");

         // obtain a physical connection
         this._connection = DriverManager.getConnection(url, user, passwd);
         System.out.println("Done");
      }catch (Exception e){
         System.err.println("Error - Unable to Connect to Database: " + e.getMessage() );
         System.out.println("Make sure you started postgres on this machine");
         System.exit(-1);
      }//end catch
   }//end Hotel

   // Method to calculate euclidean distance between two latitude, longitude pairs. 
   public double calculateDistance (double lat1, double long1, double lat2, double long2){
      double t1 = (lat1 - lat2) * (lat1 - lat2);
      double t2 = (long1 - long2) * (long1 - long2);
      return Math.sqrt(t1 + t2); 
   }
   /**
    * Method to execute an update SQL statement.  Update SQL instructions
    * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
    *
    * @param sql the input SQL string
    * @throws java.sql.SQLException when update failed
    */
   public void executeUpdate (String sql) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the update instruction
      stmt.executeUpdate (sql);

      // close the instruction
      stmt.close ();
   }//end executeUpdate

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and outputs the results to
    * standard out.
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQueryAndPrintResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and output them to standard out.
      boolean outputHeader = true;
      StringBuilder sb=new StringBuilder();
      StringBuilder sb2=new StringBuilder();
      String rowString = "";
      while (rs.next()){
		 if(outputHeader){
			for(int i = 1; i <= numCol; i++){
			   //System.out.print(rsmd.getColumnName(i) + "\t");
            sb.append(String.format("|%-20s", rsmd.getColumnName(i)));
            sb2.append("--------------------");
			}
         System.out.print(sb);
			System.out.println();
         System.out.print(sb2);
			System.out.println();
			outputHeader = false;
		 }
         for (int i=1; i<=numCol; ++i){
            //System.out.print(rs.getString(i) + "\t\t");
            rowString = rs.getString(i);
            rowString = rowString.trim();
            String newString = String.format("|%-20s", rowString);
            System.out.print(newString);
         }
         System.out.println ();
         ++rowCount;
      }//end while
      stmt.close ();
      return rowCount;
   }//end executeQuery

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the results as
    * a list of records. Each record in turn is a list of attribute values
    *
    * @param query the input query string
    * @return the query result as a list of records
    * @throws java.sql.SQLException when failed to execute the query
    */
   public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and saves the data returned by the query.
      boolean outputHeader = false;
      List<List<String>> result  = new ArrayList<List<String>>();
      while (rs.next()){
        List<String> record = new ArrayList<String>();
		for (int i=1; i<=numCol; ++i)
			record.add(rs.getString (i));
        result.add(record);
      }//end while
      stmt.close ();
      return result;
   }//end executeQueryAndReturnResult

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the number of results
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQuery (String query) throws SQLException {
       // creates a statement object
       Statement stmt = this._connection.createStatement ();

       // issues the query instruction
       ResultSet rs = stmt.executeQuery (query);

       int rowCount = 0;

       // iterates through the result set and count nuber of results.
       while (rs.next()){
          rowCount++;
       }//end while
       stmt.close ();
       return rowCount;
   }

   /**
    * Method to fetch the last value from sequence. This
    * method issues the query to the DBMS and returns the current
    * value of sequence used for autogenerated keys
    *
    * @param sequence name of the DB sequence
    * @return current value of a sequence
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int getCurrSeqVal(String sequence) throws SQLException {
      Statement stmt = this._connection.createStatement ();

      ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
      if (rs.next())
         return rs.getInt(1);
      return -1;
   }

   public int getNewUserID(String sql) throws SQLException {
      Statement stmt = this._connection.createStatement ();
      ResultSet rs = stmt.executeQuery (sql);
      if (rs.next())
         return rs.getInt(1);
      return -1;
   }
   /**
    * Method to close the physical connection if it is open.
    */
   public void cleanup(){
      try{
         if (this._connection != null){
            this._connection.close ();
         }//end if
      }catch (SQLException e){
         // ignored.
      }//end try
   }//end cleanup

   /**
    * The main execution method
    *
    * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
    */
   public static void main (String[] args) {
      if (args.length != 3) {
         System.err.println (
            "Usage: " +
            "java [-classpath <classpath>] " +
            Hotel.class.getName () +
            " <dbname> <port> <user>");
         return;
      }//end if

      Greeting();
      Hotel esql = null;
      try{
         // use postgres JDBC driver.
         Class.forName ("org.postgresql.Driver").newInstance ();
         // instantiate the Hotel object and creates a physical
         // connection.
         String dbname = args[0];
         String dbport = args[1];
         String user = args[2];
         esql = new Hotel (dbname, dbport, user, "");

         boolean keepon = true;
         while(keepon) {
            // These are sample SQL statements
            System.out.println("MAIN MENU");
            System.out.println("---------");
            System.out.println("1. Create user");
            System.out.println("2. Log in");
            System.out.println("9. < EXIT");
            String authorisedUser = null;
            switch (readChoice()){
               case 1: CreateUser(esql); break;
               case 2: authorisedUser = LogIn(esql); break;
               case 9: keepon = false; break;
               default : System.out.println("Unrecognized choice!"); break;
            }//end switch
            if (authorisedUser != null) {
              boolean usermenu = true;
              boolean managermenu = false;

              String query = String.format("SELECT * FROM USERS WHERE userID = '%s' AND userType = 'manager'", authorisedUser);
              int userNum = esql.executeQuery(query);
              if (userNum > 0){
               managermenu =  true;
              }

              while(usermenu) {
                System.out.println("MAIN MENU");
                System.out.println("---------");
                System.out.println("1. View Hotels within 30 units");
                System.out.println("2. View Rooms");
                System.out.println("3. Book a Room");
                System.out.println("4. View recent booking history");

                //the following functionalities basically used by managers
                if (managermenu){
                  System.out.println("5. Update Room Information");
                  System.out.println("6. View 5 recent Room Updates Info");
                  System.out.println("7. View booking history of the hotel");
                  System.out.println("8. View 5 regular Customers");
                  System.out.println("9. Place room repair Request to a company");
                  System.out.println("10. View room repair Requests history");
                }

                System.out.println(".........................");
                System.out.println("20. Log out");
                switch (readChoice()){
                   case 1: viewHotels(esql); break;
                   case 2: viewRooms(esql); break;
                   case 3: bookRooms(esql, authorisedUser); break; //=====NEW PARAMETER ADDED BY ZERGIO
                   case 4: viewRecentBookingsfromCustomer(esql, authorisedUser); break;
                   case 5: updateRoomInfo(esql, authorisedUser); break;
                   case 6: viewRecentUpdates(esql, authorisedUser); break;
                   case 7: viewBookingHistoryofHotel(esql, authorisedUser); break;
                   case 8: viewRegularCustomers(esql, authorisedUser); break;
                   case 9: placeRoomRepairRequests(esql, authorisedUser); break;
                   case 10: viewRoomRepairHistory(esql, authorisedUser); break;
                   case 20: usermenu = false; managermenu = false; break;
                   default : System.out.println("Unrecognized choice!"); break;
                }
              }
            }
         }//end while
      }catch(Exception e) {
         System.err.println (e.getMessage ());
      }finally{
         // make sure to cleanup the created table and close the connection.
         try{
            if(esql != null) {
               System.out.print("Disconnecting from database...");
               esql.cleanup ();
               System.out.println("Done\n\nBye !");
            }//end if
         }catch (Exception e) {
            // ignored.
         }//end try
      }//end try
   }//end main

   public static void Greeting(){
      System.out.println(
         "\n\n*******************************************************\n" +
         "              User Interface      	               \n" +
         "*******************************************************\n");
   }//end Greeting

   /*
    * Reads the users choice given from the keyboard
    * @int
    **/
   public static int readChoice() {
      int input;
      // returns only if a correct value is given.
      do {
         System.out.print("Please make your choice: ");
         try { // read the integer, parse it and break.
            input = Integer.parseInt(in.readLine());
            break;
         }catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
         }//end try
      }while (true);
      return input;
   }//end readChoice

   /*
    * Creates a new user
    **/
   public static void CreateUser(Hotel esql){
      try{
         System.out.print("\tEnter name: ");
         String name = in.readLine();
         System.out.print("\tEnter password: ");
         String password = in.readLine(); 
         String type="Customer";
			String query = String.format("INSERT INTO USERS (name, password, userType) VALUES ('%s','%s', '%s')", name, password, type);
         esql.executeUpdate(query);
         System.out.println ("User successfully created with userID = " + esql.getNewUserID("SELECT last_value FROM users_userID_seq"));
         
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }//end CreateUser


   /*
    * Check log in credentials for an existing user
    * @return User login or null is the user does not exist
    **/
   public static String LogIn(Hotel esql){
      try{
         System.out.print("\tEnter userID: ");
         String userID = in.readLine();
         System.out.print("\tEnter password: ");
         String password = in.readLine();

         String query = String.format("SELECT * FROM USERS WHERE userID = '%s' AND password = '%s'", userID, password);
         int userNum = esql.executeQuery(query);
         if (userNum > 0){
            return userID;
         }
         else{
            System.out.print("\tNot a valid user. Please try again.\n");
            return null;
         }
      }catch(Exception e){
         System.err.println (e.getMessage ());
         return null;
      }
   }//end

// Rest of the functions definition go in here

   public static void viewHotels(Hotel esql) {
      try{
         //Get user input
         System.out.print("\tEnter Latitude: ");
         String userLatitude = in.readLine();
         System.out.print("\tEnter Longitude: ");
         String userLongitude = in.readLine();

         //Query that gets the hotels within 30 distance units
         String query = String.format("SELECT H.hotelID, H.hotelName, H.latitude, H.longitude, H.dateEstablished FROM Hotel H WHERE calculate_distance(H.latitude, H.longitude, %s, %s) < 30", userLatitude, userLongitude);
         esql.executeQueryAndPrintResult(query);
      }catch(Exception e){
         System.err.println (e.getMessage ());
         return;
      }
   }//end
   


   public static void viewRooms(Hotel esql) {
      try{
         //Get user input
         System.out.print("\tEnter Hotel ID: ");
         String hotelID = in.readLine();
         System.out.print("\tEnter Date: ");
         String date = in.readLine();

         //Query that gets the available rooms in a Hotel on a specific Booking Date
         String availableQuery = String.format("SELECT DISTINCT B.roomNumber, R.price FROM RoomBookings B, Rooms R WHERE B.hotelID = '%s' AND R.hotelID = '%s' AND B.roomNumber = R.roomNumber AND B.bookingDate != '%s' UNION SELECT R.roomNumber, R.price FROM Rooms R WHERE R.hotelID='%s' AND R.roomNumber NOT IN (SELECT DISTINCT B.roomNumber FROM RoomBookings B WHERE B.hotelID='%s') ORDER BY roomNumber", hotelID, hotelID, date, hotelID, hotelID);
         System.out.print("\tHere is a list of the available rooms:\n");
         esql.executeQueryAndPrintResult(availableQuery);

         //now print out the unavailable rooms
         String unavailableQuery = String.format("SELECT DISTINCT B.roomNumber, R.price FROM RoomBookings B, Rooms R WHERE B.hotelID = '%s' AND R.hotelID = '%s' AND B.roomNumber = R.roomNumber AND B.bookingDate = '%s'", hotelID, hotelID, date);
         System.out.print("\tHere is a list of the unavailable rooms:\n");
         esql.executeQueryAndPrintResult(unavailableQuery);

      }catch(Exception e){
         System.err.println (e.getMessage ());
         return;
      }
   }



   public static void bookRooms(Hotel esql, String authorizedUser) {
      try{
         //Get user input
         System.out.print("\tEnter HotelID: ");
         String userHotelID = in.readLine();
         System.out.print("\tEnter Room Number: ");
         String userRoomNum = in.readLine();
         System.out.print("\tEnter Date in Format (MM/DD/YYYY): ");
         String userDate = in.readLine();

         //Query to select rooms booked on the date put in by user
         String query = String.format("SELECT * FROM RoomBookings B WHERE B.hotelID='%s' AND B.roomNumber='%s' AND B.bookingDate='%s'", userHotelID, userRoomNum, userDate);
         int availability = esql.executeQuery(query);
         //If query has a result, then room isn't available on that date
         if (availability > 0){
            System.out.print("There is no availability for this room on this date. Sorry!\n");
            return;
         }
         //If no result to query, the room isn't booked and is thus available
         else{
            System.out.print("This room is available on this date! Booking now...\n");
            int currUID = Integer.valueOf(authorizedUser);
            //Query to Update Bookings table
            String query2 = String.format("INSERT INTO RoomBookings (customerID, hotelID, roomNumber, bookingDate) VALUES ('%s','%s', '%s', '%s')", currUID, userHotelID, userRoomNum, userDate);
            esql.executeUpdate(query2);

            System.out.print("\tSuccessfully booked Room Number: " + userRoomNum + "\n \tAt hotel with hotel ID: " + userHotelID + "\n");
            //Query to get room price
            String query3 = String.format("SELECT R.price FROM Rooms R WHERE R.hotelID='%s' AND R.roomNumber='%s'", userHotelID, userRoomNum);
            System.out.print("\tWith Price: $");
            List<List<String>> priceList = esql.executeQueryAndReturnResult(query3);
            String roomPrice= priceList.get(0).get(0);
            System.out.print(roomPrice + "\n");

            return;
         }
      }catch(Exception e){
         System.err.println (e.getMessage ());
         return;
      }
   }//end




   public static void viewRecentBookingsfromCustomer(Hotel esql, String authorizedUser) {
      try{
         int currUID = Integer.valueOf(authorizedUser);
         //query to select last 5 bookings for the currently logged in user
         String query = String.format("SELECT B.hotelID, B.roomNumber, R.price, B.bookingDate FROM RoomBookings B, Rooms R WHERE R.roomNumber=B.roomNumber AND R.hotelID=B.hotelID AND B.customerID='%s' ORDER BY B.bookingDate DESC LIMIT 5", currUID);
         esql.executeQueryAndPrintResult(query);
      }catch(Exception e){
         System.err.println (e.getMessage ());
         return;
      }
   }




   public static void updateRoomInfo(Hotel esql, String authorizedUser) {
      try {
         int currUserID = Integer.valueOf(authorizedUser);
         //query to find user matching current user and of type manager
         String currUserTypeQuery = String.format("SELECT * FROM USERS WHERE userID = '%s' AND userType = 'manager'", currUserID);
         int userNum = esql.executeQuery(currUserTypeQuery);
         //if query returns a result, it means user is a manager
         if (userNum > 0){
            //Get user input
            System.out.print("\tEnter Hotel ID: ");
            String hotelID = in.readLine();
            System.out.print("\tEnter Room Number: ");
            String roomNumber = in.readLine();

            //check if the current user manages the hotel that they inputted !!!
            String managerHotelCheckQuery = String.format("SELECT * FROM HOTEL WHERE hotelID = '%s' AND managerUserID = '%s'", hotelID, currUserID);
            int userHotel = esql.executeQuery(managerHotelCheckQuery);

            //if query returns a result, it means the user manages the inputted hotel
            if (userHotel > 0){
               //ask user what part of the room they want to edit
               System.out.print("\tWhat part of the room would you like to edit? 1. price 2. imageURL \n");
               String userChoice = in.readLine();

               System.out.print("\tWhat would you like the new value to be?\n");
               String newValue = in.readLine();

               if (userChoice.equals("1")) {
                  //query to update room info
                  System.out.print("\tUpdating Room Price \n");
                  String updateRoomPriceQuery = String.format("UPDATE Rooms SET price = '%s' WHERE roomNumber = '%s'", newValue, roomNumber);
                  esql.executeUpdate(updateRoomPriceQuery);

                  //query to update room update logs
                  String updateRoomUpdateLogsPrice = String.format("INSERT INTO RoomUpdatesLog (managerID, hotelID, roomNumber, updatedOn) VALUES ('%s', '%s', '%s', NOW())", currUserID, hotelID, roomNumber);
                  esql.executeUpdate(updateRoomUpdateLogsPrice);
                  System.out.println("Successfully Updated Room Price");
               }
               else if (userChoice.equals("2")) {
                  //query to update room info
                  System.out.print("\tUpdating Room Image URL \n");
                  String updateRoomImageQuery = String.format("UPDATE Rooms SET imageURL = '%s' WHERE roomNumber = '%s'", newValue, roomNumber);
                  esql.executeUpdate(updateRoomImageQuery);

                  //query to update room update logs
                  String updateRoomUpdateLogsImage = String.format("INSERT INTO RoomUpdatesLog (managerID, hotelID, roomNumber, updatedOn) VALUES ('%s', '%s', '%s', NOW())", currUserID, hotelID, roomNumber);
                  esql.executeUpdate(updateRoomUpdateLogsImage);
                  System.out.println("Successfully Updated Room Image");
               }
               else{
                  System.out.print("\tNot an option! Goodbye!");
                  return;
               }
            }
            else{
               System.out.print("\tYou can only update rooms of hotels that you manage.\n");
            }
         }
         //if no result, current user is not a manager
         else{
            System.out.print("\tThis option is for MANAGERS only.\n");
            return;
         }

      }catch(Exception e){
         System.err.println (e.getMessage ());
	 return;
      }
   }



   public static void viewRecentUpdates(Hotel esql, String authorizedUser) {
      try{
         int currUID = Integer.valueOf(authorizedUser);
         //query to select user matching currently logged in user, and with manager type
         String query = String.format("SELECT * FROM USERS WHERE userID = '%s' AND userType = 'manager'", currUID);
         int userNum = esql.executeQuery(query);
         //if query has result, means current user is a manger
         if (userNum > 0){
            //query to select the last 5 room updates
            String query2 = String.format("SELECT * FROM RoomUpdatesLog U WHERE U.managerID='%s' ORDER BY U.updatedOn DESC LIMIT 5", currUID);
            esql.executeQueryAndPrintResult(query2);
            return;
         }
         //if no result, current user is not a manager
         else{
            System.out.print("\tThis option is for MANAGERS only.\n");
            return;
         }
      }catch(Exception e){
         System.err.println (e.getMessage ());
         return;
      }
   }



   public static void viewBookingHistoryofHotel(Hotel esql, String authorizedUser) {
      try{
         int currUID = Integer.valueOf(authorizedUser);
         //query to select user matching currently logged in user, and with manager type
         String query = String.format("SELECT * FROM USERS WHERE userID = '%s' AND userType = 'manager'", currUID);
         int userNum = esql.executeQuery(query);
         //if query has result, means current user is a manger
         if (userNum > 0){
            System.out.print("\tDo you wish to select a date range? (y/n): ");
            String answer = in.readLine();
            if(answer.equals("n")){
               //query to view entire booking history for all hotels manager by user
               String query2 = String.format("SELECT B.bookingID, U.name, B.hotelID, B.roomNumber, B.bookingDate FROM RoomBookings B, Hotel H, Users U WHERE U.userID=B.customerID AND H.managerUserID='%s' AND H.hotelID=B.hotelID ORDER BY B.bookingDate", currUID);
               esql.executeQueryAndPrintResult(query2);
               return;
            }
            else if (answer.equals("y")){
               System.out.print("\tPlease enter the start date (MM/DD/YYYY): ");
               String startDate = in.readLine();
               System.out.print("\tPlease enter the end date (MM/DD/YYYY): ");
               String endDate = in.readLine();
               //query to view booking history for 
               String query3 = String.format("SELECT B.bookingID, U.name, B.hotelID, B.roomNumber, B.bookingDate FROM RoomBookings B, Hotel H, Users U WHERE U.userID=B.customerID AND H.managerUserID='%s' AND H.hotelID=B.hotelID AND B.bookingDate BETWEEN '%s' AND '%s' ORDER BY B.bookingDate", currUID, startDate, endDate);
               esql.executeQueryAndPrintResult(query3);
            }
            else{
               System.out.print("\tNot an option! Goodbye!");
               return;
            }

         }
         //if no result, current user is not a manager
         else{
            System.out.print("\tThis option is for MANAGERS only.\n");
            return;
         }
      }catch(Exception e){
         System.err.println (e.getMessage ());
         return;
      }
   }





   public static void viewRegularCustomers(Hotel esql, String authorizedUser) {
      try{
         int currUID = Integer.valueOf(authorizedUser);
         //check for manager
         String query = String.format("SELECT * FROM USERS WHERE userID = '%s' AND userType = 'manager'", currUID);
         int userNum = esql.executeQuery(query);
         if (userNum > 0){
            System.out.print("\tEnter HotelID: ");
            String userHotelID = in.readLine();
            //Select records from Hotel relation under your management and with provided hotelID
            String query2 = String.format("SELECT * FROM Hotel H WHERE H.hotelID='%s' AND H.managerUserID='%s'", userHotelID, currUID);
            int hotelNum = esql.executeQuery(query2);
            if (hotelNum > 0){
               //by top 5 customers by number of bookings
               String query3 = String.format("SELECT customerID, COUNT(*) AS Bookings FROM RoomBookings WHERE hotelID='%s' GROUP BY customerID ORDER BY Bookings DESC LIMIT 5", userHotelID);
               esql.executeQueryAndPrintResult(query3);
            }
            else{
               System.out.print("\t\tFound no hotel with this hotelID under your management.\n");
               return;
            }

            return;
         }
         else{
            System.out.print("\tThis option is for MANAGERS only.\n");
            return;
         }
      }catch(Exception e){
         System.err.println (e.getMessage ());
         return;
      }
   }




   //==========NEW FUNCTION ADDED BY VINCENT==========//
   public static void placeRoomRepairRequests(Hotel esql, String authorizedUser) {
      try{
	      //check to see if current user is manager
         int currUserID = Integer.valueOf(authorizedUser);
         String query = String.format("SELECT * FROM USERS WHERE userID = '%s' AND userType = 'manager'", currUserID);
         int userNum = esql.executeQuery(query);
	      //if there is a result, that means the user is a manager
         if (userNum > 0){
            //inputs for hotelID, roomNumber, and companyID of repair company
            System.out.print("\tPlease enter the following information for your Room Repair Request: \n");
            System.out.print("\tHotelID: ");
            String hotelID = in.readLine();
	         System.out.print("\tRoom Number: ");
            String roomNumber = in.readLine();
	         System.out.print("\tCompany ID of maintenance company: ");
            String companyID = in.readLine();

	         //make sure User MANAGES HotelID from input -- NEW
            String managerHotelCheckQuery = String.format("SELECT * FROM HOTEL WHERE hotelID = '%s' AND managerUserID = '%s'", hotelID, currUserID);
            int userHotel = esql.executeQuery(managerHotelCheckQuery);

            //if query returns a result, it means the user manages the inputted hotel -- NEW
            if(userHotel > 0) {

	            //update RoomRepairs table
	            String roomRepairRequestQuery = String.format("INSERT INTO RoomRepairs (companyID, hotelID, roomNumber, repairDate) VALUES ('%s', '%s', '%s', CURRENT_DATE)", companyID, hotelID, roomNumber);
               esql.executeUpdate(roomRepairRequestQuery);

	            //Get lastest repairID
               int repairID = esql.getCurrSeqVal("RoomRepairs_repairID_seq");

	            //update RoomRepairRequests table
               String roomRepairsRequestInsertQuery = String.format("INSERT INTO RoomRepairRequests (managerID, repairID) VALUES ('%s', '%s')", currUserID, repairID);
	            esql.executeUpdate(roomRepairsRequestInsertQuery);
	            System.out.print("\tSuccessfully placed Room Repair Request!\n");
            }
	         else{
               System.out.print("\tYou can only update rooms of hotels that you manage.\n");
            }

            return;
         }
         else{
            System.out.print("\tThis option is for MANAGERS only.\n");
            return;
         }
      }catch(Exception e){
         System.err.println (e.getMessage ());
         return;
      }
   }
   //==========END NEW FUNCTION ADDED BY VINCENT==========//




   public static void viewRoomRepairHistory(Hotel esql, String authorizedUser) {
      try{
         int currUID = Integer.valueOf(authorizedUser);
         String query = String.format("SELECT * FROM USERS WHERE userID = '%s' AND userType = 'manager'", currUID);
         int userNum = esql.executeQuery(query);
         if (userNum > 0){
            String query2 = String.format("SELECT R.companyID, R.hotelID, R.roomNumber, R.repairDate FROM RoomRepairs R, Hotel H WHERE R.hotelID=H.hotelID AND H.managerUserID='%s'", currUID);
            esql.executeQueryAndPrintResult(query2);
            return;
         }
         else{
            System.out.print("\tThis option is for MANAGERS only.\n");
            return;
         }
      }catch(Exception e){
         System.err.println (e.getMessage ());
         return;
      }
   }

}//end Hotel

