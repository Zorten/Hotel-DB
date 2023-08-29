DROP INDEX IF EXISTS hotel_location;
DROP INDEX IF EXISTS room_id;
DROP INDEX IF EXISTS user_id;
DROP INDEX IF EXISTS manager_user_id;
DROP INDEX IF EXISTS room_booking_room_number;

CREATE INDEX hotel_location
ON Hotel
USING BTREE(latitude, longitude);

CREATE INDEX user_id
ON Users
USING BTREE(userID);

CREATE INDEX room_id
ON Rooms
USING BTREE(hotelID, roomNumber);

CREATE INDEX manager_user_id
ON Hotel
USING BTREE(managerUserID);

CREATE INDEX room_booking_room_number
ON RoomBookings
USING BTREE(roomNumber);

