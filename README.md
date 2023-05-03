# Laundry Booking Application

This is a laundry booking app that allows residents in households to book laundry time slots in shared building laundry rooms.
The app is equipped with features such as the ability to list available and booked laundry times, book available slots, and allow cancellations.

The app can be utilized through its REST API endpoints while data is stored in an in-memory database to make setup easier for users. 

Prequisites:
---------------
- Java 17
- Springboot 3

How to run the application:
---------------------------
1. Clone the repository to your local machine
2. Open the terminal and navigate to the project directory.
3. Run the following command to start the application: 
    > ./mvnw spring-boot:run
4. You can access the endpoints through the following:
    > http://localhost:8080
   
How to run the tests in the application: 
-------------------------
1. Clone the repository to your local machine
2. Open the terminal and navigate to the project directory.
3. Run the following command to run the tests in the application:
   > ./mvnw test

API Endpoints:
----------------
Create a laundry booking:
> POST   /api/v1/laundry-bookings

Request Body:
```json
{
   "householdId": 0,
   "laundryRoomId": 0,
   "date": "string",
   "timeBlockId": 0
}
```
Get all laundry bookings by household id
> GET    /api/v1/laundry-bookings/household/{id}

Get all laundry bookings 
> GET    /api/v1/laundry-bookings

Get available laundry booking timeslots by date
> GET    /api/v1/laundry-bookings/time-blocks/available

Request parameter:
```
date 2023-05-01
```

Cancel laundry booking by id
> DELETE /api/v1/laundry-bookings/{id}
