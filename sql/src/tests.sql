--test to see last entry of RoomBookings
SELECT * FROM RoomBookings B WHERE B.bookingID = (SELECT MAX(B2.bookingID) FROM RoomBookings B2);