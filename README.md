# Hotel Database Management System
Authors: Zergio Ruvalcaba & Vincent Raimondi

##Implementation

###Functions Implemented by Zergio:

* viewHotels(esql)
  * This function outputs the information for hotels within 30 distance units of the latitude and longitude values inputted by the user 
  * Struggled at first with how to calculate and compare the distances, but eventually learned that calculate_distance() function could be used within SQL query
  * Parameters passed in for calculate_distance() were the latitude/longitude values provided by the user and the latitude/longitude values of individual hotels in the Hotel relation.
  * To retrieve the hotels, a SELECT query is done on the id, name, latitude/longitude, and date established from the Hotels relation where calculate_distance() returns a value less than 30
 
* bookRooms(esql, authorisedUser)
  * For this function and the remaining ones I implemented, I passed in the user ID of the currently logged in user, so that I may be able to use it in my queries.
  * This function takes user input for a hotel ID, room number, and date, and then checks if the specified room is available on that date. It performs this check by executing a SELECT query on the RoomBookings relation with the data provided by the user. If the query returns an answer, it means there is a booking for this room and thus it is not available, otherwise the room is available.
  * If the room is unavailable the function returns. If the room is available, an INSERT query is performed on RoomBookings, inserting the user ID passed into the function, along with the other values inputted by the user.
  * Once RoomBookings is updated, the room and hotel information is outputted back to the user, and then a SELECT query is done on the Rooms relation to retrieve the price of the room that was just booked, and then this price is outputted to the user.
 
* viewBookingHistoryofHotel(esql, authorisedUser)
  * For this function and the two below it, there is a check to see if the currently logged in user is a manager, since only managers are meant to use these functions. This check is done by executing a SELECT query on the Users relation to retrieve a user matching the current user ID and with the ‘type’ attribute being equal to ‘manager’. If this query returns an answer, the user is a manager and we proceed, otherwise the function returns.
  * If the user is confirmed to be a manager, then the function asks them whether or not they wish to select a date range for the booking history. They can enter either ‘y’ for yes, or ‘n’ for no. Any other inputs will make the function return
  * If the user inputs no, then a SELECT query is performed on the booking ID, the customer’s name, hotel ID, room number, and booking date from the join of the RoomBookings, Hotel, and Users relations. This join operation is done on the user ID and customer ID matching from the Users and RoomBookings relations, as well as on the hotel IDs from the Hotel and RoomBookings relations, and lastly on the Hotel managerUserID being equal to the current user ID. Finally, the query is ordered by the bookingDate from the RoomBookings relation.
  * If the user inputs yes, then they are prompted to enter a start date and an end date. After this, the same query as the one described above is performed, except that it makes use of the BETWEEN…AND… operators to select records between the dates entered by the user.
 
* viewRegularCustomers(esql, authorisedUser)
  * First there’s the manager check, if the current user is not a manager the function returns.
  * If the user is a manager, then the function prompts the user to enter a hotel ID. A SELECT query is then performed on the Hotel relation to check that a hotel with the provided ID exists, and that the current user is the manager for that hotel.
  * If the query returns no answer, then no such hotel under the management of the current user exists, so the function returns.
  * If the query returns an answer, then we execute a SELECT query on the RoomBookings relation. The query selects the customer ID and the count of bookings for each customer in the relation matching the hotel ID provided. This is then grouped by customer IDs, and then it is ordered by the number of bookings, in descending order, and then the result is limited to the top 5 records. This returns the 5 customers with the most bookings for the provided hotel ID.
 
* viewRoomRepairHistory(esql, authorisedUser)
  * First there’s the manager check, if the current user is not a manager the function returns.
  * If the user is a manager, then the function executes a SELECT query on company ID, hotel ID, room number, and repair date from the join of the RoomRepairs and the Hotels relations. This join is performed on the hotel IDs of both relations matching, and on the managerUserID of the Hotels relation being equal to the current user ID.


 ###Functions Implemented by Vincent:

* viewRooms(esql)
  * This function takes in user input of Hotel ID and Date to view the available rooms within that hotel on that specific date.
  * This function then displays a list of available and non-available rooms based on the user inputted Hotel ID and Date. This is achieved by utilizing two SELECT queries on the RoomBookings and Rooms relations that obtain the room number and price of each room.
  * For Rooms that have never been booked before (rooms that are not in bookings.csv), we added those rooms as available with a UNION with another SELECT query to find room numbers not located in RoomBookings.
 
* viewRecentBookingsfromCustomer(esql, authorisedUser)
  * This function utilizes the authorisedUser variable within the SELECT query to obtain the booking history of the current user
  * In order to obtain the 5 most recent bookings of the user, the query includes the ORDER BY method to sort by ‘bookingDate’ and uses DESC LIMIT method with the value 5 to only return five values from the query.
 
* updateRoomInfo(esql, authorisedUser)
  * This function allows managers to update the price and image URL attributes of rooms of hotels that they manage
  * First, this function checks if the current user is a manager AND checks if the user manages the inputted Hotel ID
  * Once these checks are satisfied, the function provides an option to either change the inputted room price or image URL with different UPDATE queries within two if-statements.
  * After the UPDATE query is finished, an INSERT query is executed to update the RoomUpdatesLog relation with the most recent changes to either room price or image URL → uses NOW() function to get timestamp for updatedOn attribute.
 
* viewRecentUpdates(esql, authorisedUser)
  * Since this function is for managers, we implement a query to check if the current user is of userType = ‘manager’ with a SELECT query
  * After the user is verified to be a manager, we do a SELECT query to obtain the 5 most recent updates (in a similar fashion to viewRecentBookingsfromCustomer function) with the ORDER BY and DESC LIMIT methods.
 
* placeRoomRepairRequests(esql, authorisedUser)
  * This function is for managers, so we implement query checks to make sure that current user is a manager and that they manage the user inputted Hotel
  * Initial implementation is to have a SELECT query to get current value of repairID
  * Newest implementation uses the defined function getCurrSeqVal() where we can pass the sequence string of RoomRepairs to get the updated value of repairID – this will properly update the RoomRepairRequests table with the correct repairID. To update the RoomRepairs table with the correct repairDate attribute, we utilized the CURRENT_DATE function.
