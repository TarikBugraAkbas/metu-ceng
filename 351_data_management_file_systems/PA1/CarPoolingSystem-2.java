package ceng.ceng351.carpoolingdb;

import com.mysql.cj.x.protobuf.MysqlxDatatypes;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CarPoolingSystem implements ICarPoolingSystem {

    private static String url = "jdbc:h2:mem:carpoolingdb;DB_CLOSE_DELAY=-1"; // In-memory database
    private static String user = "sa";          // H2 default username
    private static String password = "";        // H2 default password

    private Connection connection;

    public void initialize(Connection connection) {
        this.connection = connection;
    }

    //Given: getAllDrivers()
    //Testing 5.16: All Drivers after Updating the Ratings
    @Override
    public Driver[] getAllDrivers() {
        List<Driver> drivers = new ArrayList<>();
        
        //uncomment following code slice
        String query = "SELECT PIN, rating FROM Drivers ORDER BY PIN ASC;";

        try {
            PreparedStatement ps = this.connection.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int PIN = rs.getInt("PIN");
                double rating = rs.getDouble("rating");

                // Create a Driver object with only PIN and rating
                Driver driver = new Driver(PIN, rating);
                drivers.add(driver);
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return drivers.toArray(new Driver[0]); 
    }

    
    //5.1 Task 1 Create tables
    @Override
    public int createTables() {
        int tableCount = 0;
        String[] create_table_list = {  "Create Table Participants " +
                                                "(PIN INT, p_name VARCHAR(50), age INT, PRIMARY KEY(PIN));",

                                        "Create Table Passengers(PIN INT, membership_status VARCHAR(50)," +
                                                "PRIMARY KEY (PIN), FOREIGN KEY(PIN) REFERENCES PARTICIPANTS(PIN));",

                                        "Create Table Drivers(PIN INT, rating DOUBLE, PRIMARY KEY (PIN), " +
                                                "FOREIGN KEY (PIN) REFERENCES PARTICIPANTS(PIN));",

                                        "Create Table Cars(CarId INT, PIN INT, color VARCHAR(30), brand VARCHAR(50), " +
                                                "PRIMARY KEY (CarId), FOREIGN KEY (PIN) REFERENCES DRIVERS(PIN));",

                                        "Create Table Trips(TripID INT, CarID INT, date DATE, departure VARCHAR(50), destination VARCHAR(50), num_seats_available INT," +
                                                "PRIMARY KEY(TripID), FOREIGN KEY(CarID) REFERENCES Cars(CarId));",

                                        "Create Table Bookings (TripID INT, PIN INT, booking_status VARCHAR(50), " +
                                                "PRIMARY KEY(TripID, PIN), FOREIGN KEY(TripID) REFERENCES Trips(TripID), FOREIGN KEY(PIN) REFERENCES Passengers(PIN));"};

        try
        {
            Statement stmt = connection.createStatement();
            for(String query : create_table_list) {
                try
                {
                    stmt.executeUpdate(query);
                    tableCount++;
                }
                catch (SQLException e)
                {
                    System.err.println("Error creating table: " + query);
                    System.err.println("Failed to create table: " + e.getMessage());
                }
            }
            stmt.close();
        }
        catch (SQLException e)
        {
            System.err.println("Failed to create statement: " + e.getMessage());
        }



        return tableCount;
    }


    //5.17 Task 17 Drop tables
    @Override
    public int dropTables() {
        int tableCount = 0;

        String[] drop_table_list = {  "DROP TABLE Participants;",
                                      "DROP TABLE Passengers;",
                                      "DROP TABLE Drivers;",
                                      "DROP TABLE Cars;",
                                      "DROP TABLE Trips;",
                                      "DROP TABLE Bookings;"};

        try
        {
            Statement stmt = connection.createStatement();
            for(String query : drop_table_list) {
                try
                {
                    stmt.executeUpdate(query);
                    tableCount++;
                }
                catch (SQLException e)
                {
                    System.err.println("Failed to drop table: " + e.getMessage());
                }
            }
            stmt.close();
        }
        catch (SQLException e)
        {
            System.err.println("Failed to create statement: " + e.getMessage());
        }

        return tableCount;
    }

    //5.2 Task 2 Insert Participants
    @Override
    public int insertParticipants(Participant[] participants) {
        int rowsInserted = 0;
        String insert_participants_query = "INSERT INTO PARTICIPANTS (PIN, p_name, age) VALUES(?,?,?)";
        try
        {
            PreparedStatement pstmt = connection.prepareStatement(insert_participants_query);
            for(Participant p : participants)
            {
                pstmt.setInt(1, p.getPIN());
                pstmt.setString(2, p.getP_name());
                pstmt.setInt(3, p.getAge());
                pstmt.executeUpdate();
                rowsInserted++;
            }
            pstmt.close();
        }
        catch (SQLException e)
        {
            System.err.println("Failed to insert into participants: " + e.getMessage());
        }
        return rowsInserted;
    }

    
    //5.2 Task 2 Insert Passengers
    @Override
    public int insertPassengers(Passenger[] passengers) {
        int rowsInserted = 0;
        String insert_passengers_query = "INSERT INTO Passengers (PIN, membership_status) VALUES (?,?)";
        try
        {
            PreparedStatement pstmt = connection.prepareStatement(insert_passengers_query);
            for(Passenger p : passengers)
            {
                pstmt.setInt(1, p.getPIN());
                pstmt.setString(2, p.getMembership_status());
                pstmt.executeUpdate();
                rowsInserted++;
            }
            pstmt.close();
        } catch (Exception e)
        {
            System.err.println("Failed to insert into passengers: " + e.getMessage());
        }
        return rowsInserted;
    }


    //5.2 Task 2 Insert Drivers
    @Override
    public int insertDrivers(Driver[] drivers) {
        int rowsInserted = 0;
        String insert_drivers_query = "INSERT INTO Drivers (PIN, rating) VALUES(?,?)";

        try
        {
            PreparedStatement pstmt = connection.prepareStatement(insert_drivers_query);
            for(Driver d : drivers)
            {
                pstmt.setInt(1, d.getPIN());
                pstmt.setDouble(2, d.getRating());
                pstmt.executeUpdate();
                rowsInserted++;
            }
            pstmt.close();
        } catch (SQLException e)
        {
            System.err.println("Failed to insert into drivers: " + e.getMessage());
        }
        return rowsInserted;
    }

    
    //5.2 Task 2 Insert Cars
    @Override
    public int insertCars(Car[] cars) {
        int rowsInserted = 0;
        String insert_cars_query = "INSERT INTO Cars (CarID, PIN, color, brand) VALUES(?,?,?,?)";

        try
        {
            PreparedStatement pstmt = connection.prepareStatement(insert_cars_query);
            for(Car c : cars)
            {
                pstmt.setInt(1, c.getCarID());
                pstmt.setInt(2, c.getPIN());
                pstmt.setString(3, c.getColor());
                pstmt.setString(4, c.getBrand());
                pstmt.executeUpdate();
                rowsInserted++;
            }
            pstmt.close();
        }
        catch (Exception e)
        {
            System.err.println("Failed to insert into cars: " + e.getMessage());
        }
        return rowsInserted;
    }


    //5.2 Task 2 Insert Trips
    @Override
    public int insertTrips(Trip[] trips) {
        int rowsInserted = 0;
        String insert_into_trips = "INSERT INTO Trips (TripID, CarID, date, departure, destination, num_seats_available) VALUES (?,?,?,?,?,?)";

        try
        {
            PreparedStatement pstmt = connection.prepareStatement(insert_into_trips);
            for(Trip t : trips)
            {
                pstmt.setInt(1, t.getTripID());
                pstmt.setInt(2, t.getCarID());
                pstmt.setString(3, t.getDate());
                pstmt.setString(4, t.getDeparture());
                pstmt.setString(5, t.getDestination());
                pstmt.setInt(6, t.getNum_seats_available());
                pstmt.executeUpdate();
                rowsInserted++;
            }
            pstmt.close();
        }
        catch (Exception e)
        {
            System.err.println("Failed to insert into trips: " + e.getMessage());
        }
        return rowsInserted;
    }

    //5.2 Task 2 Insert Bookings
    @Override
    public int insertBookings(Booking[] bookings) {
        int rowsInserted = 0;
        String insert_into_bookings = "INSERT INTO Bookings(TripID, PIN, booking_status) VALUES(?,?,?)";

        try
        {
            PreparedStatement pstmt = connection.prepareStatement(insert_into_bookings);
            for(Booking b : bookings)
            {
                pstmt.setInt(1, b.getTripID());
                pstmt.setInt(2, b.getPIN());
                pstmt.setString(3, b.getBooking_status());
                pstmt.executeUpdate();
                rowsInserted++;
            }
            pstmt.close();
        }
        catch (SQLException e)
        {
            System.err.println("Failed to insert into bookings: " + e.getMessage());
        }
        return rowsInserted;
    }

    
    //5.3 Task 3 Find all participants who are recorded as both drivers and passengers
    @Override
    public Participant[] getBothPassengersAndDrivers() {

        String query = "SELECT * " +
                    "FROM Participants P " +
                "WHERE P.PIN IN ( " +
                        "SELECT D.PIN " +
                        "FROM Drivers D " +
                        "WHERE D.PIN IN (" +
                        "SELECT Pas.PIN " +
                        "FROM Passengers Pas )) " +
                "ORDER BY P.PIN ASC ";

        List<Participant> Parts = new ArrayList<>();

        try
        {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while(rs.next())
            {
                int a = rs.getInt("PIN");
                String b = rs.getString("p_name");
                int c = rs.getInt("age");
                Parts.add(new Participant(a, b, c));

            }
            rs.close();
            stmt.close();

        }
        catch (Exception e)
        {
            System.err.println("Failed to execute query 5.3: " + e.getMessage());
        }

    	return Parts.toArray(new Participant[0]);
    }

 
    //5.4 Task 4 Find the PINs, names, ages, and ratings of drivers who do not own any cars
    @Override
    public QueryResult.DriverPINNameAgeRating[] getDriversWithNoCars() {

        String query = "SELECT D.PIN, P.p_name, P.age, D.rating " +
                "FROM Drivers D, Participants P " +
                "WHERE D.PIN = P.PIN AND D.PIN NOT IN " +
                "(SELECT C.PIN " +
                "FROM Cars C) " +
                "order by D.PIN ASC ";
        List<QueryResult.DriverPINNameAgeRating> driverList = new ArrayList<>();

        try
        {
            PreparedStatement pstmt = connection.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next())
            {
                int a = rs.getInt("PIN");
                String b = rs.getString("p_name");
                int c = rs.getInt("age");
                double d = rs.getDouble("rating");
                driverList.add(new QueryResult.DriverPINNameAgeRating(a,b,c,d));

            }
            rs.close();
            pstmt.close();


        } catch (SQLException e)
        {
            System.err.println("Failed to execute query 5.4: " + e.getMessage());
        }
    	
    	return driverList.toArray(new QueryResult.DriverPINNameAgeRating[0]);
    }
 
    
    //5.5 Task 5 Delete Drivers who do not own any cars
    @Override
    public int deleteDriversWithNoCars() {
        int rowsDeleted = 0;
        String query = "DELETE FROM DRIVERS D " +
                "WHERE D.PIN NOT IN (" +
                "SELECT C.PIN " +
                "FROM Cars C) ";
        try
        {
            Statement stmt = connection.createStatement();
            rowsDeleted = stmt.executeUpdate(query);
            stmt.close();

        }
        catch (Exception e)
        {
            System.err.println("Failed to execute query 5.5: " + e.getMessage());
        }
        return rowsDeleted;  
    }

    
    //5.6 Task 6 Find all cars that are not taken part in any trips
    @Override
    public Car[] getCarsWithNoTrips() {
        String query = "SELECT C.CarID, C.PIN, C.color, C.brand " +
                        "FROM Cars C " +
                        "WHERE C.CarID NOT IN " +
                                "(SELECT T.CarID " +
                                "FROM Trips T) " +
                        "ORDER BY C.CarID ASC ;";
        List<Car> carList = new ArrayList<>();

        try
        {
            PreparedStatement pstmt = connection.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next())
            {
                int a = rs.getInt("CarID");
                int b = rs.getInt("PIN");
                String c = rs.getString("color");
                String d = rs.getString("brand");
                carList.add(new Car(a,b,c,d));
            }
            rs.close();
            pstmt.close();
        }
        catch (Exception e)
        {
            System.err.println("Failed to execute query 5.6: " + e.getMessage());
        }
    	
        return carList.toArray(new Car[0]);
    }
    
    
    //5.7 Task 7 Find all passengers who didn't book any trips
    @Override
    public Passenger[] getPassengersWithNoBooks() {
        String query = "SELECT P.PIN, P.membership_status " +
                        "FROM Passengers P " +
                        "WHERE P.PIN NOT IN " +
                            "(SELECT Pas.PIN " +
                            "FROM Passengers Pas, Bookings B, Trips T " +
                            "WHERE Pas.PIN = B.PIN AND B.TripID = T.TripID) " +
                            "ORDER BY P.PIN ASC; ";
        List<Passenger> passengerList = new ArrayList<>();
        try
        {
            PreparedStatement pstmt = connection.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next())
            {
                int a = rs.getInt("PIN");
                String b = rs.getString("membership_status");
                passengerList.add(new Passenger(a,b));
            }
            rs.close();
            pstmt.close();
        }
        catch (SQLException e)
        {
            System.err.println("Failed to execute query 5.7: " + e.getMessage());
        }
    	
        return passengerList.toArray(new Passenger[0]);
    }


    //5.8 Task 8 Find all trips that depart from the specified city to specified destination city on specific date
    @Override
    public Trip[] getTripsFromToCitiesOnSpecificDate(String departure, String destination, String date) {
        String query = "SELECT * " +
                        "FROM Trips T " +
                        "WHERE T.departure = ? AND T.destination = ? AND T.date = ? " +
                        "ORDER BY T.TripID ASC ";

        List<Trip> tripList = new ArrayList<>();

        try
        {
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, departure);
            pstmt.setString(2, destination);
            pstmt.setString(3, date);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next())
            {
                int a = rs.getInt("TripID");
                int b = rs.getInt("CarID");
                String c = rs.getString("date");
                String d = rs.getString("departure");
                String e = rs.getString("destination");
                int f = rs.getInt("num_seats_available");
                tripList.add(new Trip(a,b,c,d,e,f));

            }
            rs.close();
            pstmt.close();
        }
        catch (Exception e)
        {
            System.err.println("Failed to execute query 5.8: " + e.getMessage());
        }
    	
        return tripList.toArray(new Trip[0]);
    }


    //5.9 Task 9 Find the PINs, names, ages, and membership_status of passengers who have bookings on all trips destined at a particular city
    @Override
    public QueryResult.PassengerPINNameAgeMembershipStatus[] getPassengersWithBookingsToAllTripsForCity(String city) {
        
    	String query = "Select P.PIN, Par.p_name, Par.age, P.membership_status " +
                        "FROM Passengers P, Participants Par " +
                        "WHERE P.PIN = Par.PIN AND NOT EXISTS( " +
                                    "SELECT T.TripID " +
                                    "FROM Trips T " +
                                    "WHERE T.destination = ? AND NOT EXISTS ( " +
                                                                    "SELECT B.TripID, B.PIN " +
                                                                    "FROM Bookings B " +
                                                                    "WHERE B.PIN = P.PIN AND B.TripID = T.TripID)) " +
                        "ORDER BY P.PIN ASC; ";
        List<QueryResult.PassengerPINNameAgeMembershipStatus> passengerList = new ArrayList<>();

        try
        {
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, city);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next())
            {
                int a = rs.getInt("PIN");
                String b = rs.getString("p_name");
                int c = rs.getInt("age");
                String d = rs.getString("membership_status");
                passengerList.add(new QueryResult.PassengerPINNameAgeMembershipStatus(a, b, c, d));
            }
            rs.close();
            pstmt.close();

        }
        catch (Exception e)
        {
            System.err.println("Failed to execute query 5.9: " + e.getMessage());
        }

    	
        return passengerList.toArray(new QueryResult.PassengerPINNameAgeMembershipStatus[0]);
    }

    
    //5.10 Task 10 For a given driver PIN, find the CarIDs that the driver owns and were booked at most twice.    
    @Override
    public Integer[] getDriverCarsWithAtMost2Bookings(int driverPIN) {

        String query = "SELECT C.CarID " +
                        "FROM Cars C, Bookings B, Trips T " +
                        "WHERE C.PIN = ? AND T.CarID = C.CarID AND T.TripID = B.TripID " +
                        "GROUP BY C.CarID " +
                        "HAVING COUNT(*) < 3" +
                        "ORDER BY C.CarID ASC ";
        List<Integer> carList = new ArrayList<>();
        try
        {
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, driverPIN);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next())
            {
                int a = rs.getInt("CarID");
                carList.add(a);
            }
            rs.close();
            pstmt.close();
        }
        catch (Exception e)
        {
            System.err.println("Failed to execute query 5.10: " + e.getMessage());
        }

        return carList.toArray(new Integer[0]);
    }


    //5.11 Task 11 Find the average age of passengers with "Confirmed" bookings (i.e., booking_status is ”Confirmed”) on trips departing from a given city and within a specified date range
    @Override
    public Double getAvgAgeOfPassengersDepartFromCityBetweenTwoDates(String city, String start_date, String end_date) {
        Double averageAge = null;
        String query = "SELECT AVG(Par.age) " +
                "FROM Passengers P, Participants Par, Bookings B, Trips T " +
                "WHERE P.PIN = Par.PIN AND P.PIN = B.PIN AND B.booking_status = 'Confirmed' AND " +
                "B.TripID = T.TripID AND T.departure = ? AND T.date >= ? AND T.date <= ? ";


        try
        {
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, city);
            pstmt.setString(2, start_date);
            pstmt.setString(3, end_date);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next())
            {
                averageAge = rs.getDouble("AVG(Par.age)");
            }
            rs.close();
            pstmt.close();

        }
        catch (Exception e)
        {
            System.err.println("Failed to execute query 5.11: " + e.getMessage());
        }


        return averageAge;
    }


    //5.12 Task 12 Find Passengers in a Given Trip.
    @Override
    public QueryResult.PassengerPINNameAgeMembershipStatus[] getPassengerInGivenTrip(int TripID) {

        String query = "Select P.PIN, Par.p_name, Par.age, P.membership_status " +
                "FROM Passengers P, Participants Par, Bookings B, Trips T " +
                "WHERE P.PIN = Par.PIN AND T.TripID = B.TripID AND B.PIN = P.PIN AND T.TripID = ?" +
                "ORDER BY P.PIN ASC ";
        List<QueryResult.PassengerPINNameAgeMembershipStatus> passengerList = new ArrayList<>();

        try
        {
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, TripID);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next())
            {
                int a = rs.getInt("PIN");
                String b = rs.getString("p_name");
                int c = rs.getInt("age");
                String d = rs.getString("membership_status");
                passengerList.add(new QueryResult.PassengerPINNameAgeMembershipStatus(a, b, c, d));

            }
        }
        catch (Exception e)
        {
            System.err.println("Failed to execute query 5.12: " + e.getMessage());
        }
    	
        return passengerList.toArray(new QueryResult.PassengerPINNameAgeMembershipStatus[0]);
    }


    //5.13 Task 13 Find Drivers’ Scores
    @Override
    public QueryResult.DriverScoreRatingNumberOfBookingsPIN[] getDriversScores() {
        
    	String query = "SELECT D.rating, Count(B.TripID), D.PIN, (D.rating * Count(B.TripID)) AS driver_score " +
                "FROM Drivers D, Cars C, Trips T, Bookings B " +
                "WHERE D.PIN = C.PIN AND T.CarID = C.CarID AND B.TripID = T.TripID " +
                "GROUP BY D.rating, D.PIN " +
                "ORDER BY driver_score DESC, D.PIN ASC; ";
        List<QueryResult.DriverScoreRatingNumberOfBookingsPIN> scoreList = new ArrayList<>();
        try
        {
            PreparedStatement pstmt = connection.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next())
            {
                double a = rs.getDouble("driver_score");
                double b = rs.getDouble("rating");
                int c = rs.getInt("Count(B.TripID)");
                int d = rs.getInt("PIN");
                scoreList.add(new QueryResult.DriverScoreRatingNumberOfBookingsPIN(a, b, c, d));


            }
            rs.close();
            pstmt.close();
        }
        catch (Exception e)
        {
            System.err.println("Failed to execute query 5.13: " + e.getMessage());
        }

    	
        return scoreList.toArray(new QueryResult.DriverScoreRatingNumberOfBookingsPIN[0]);
    }

    
    //5.14 Task 14 Find average ratings of drivers who have trips destined to each city
    @Override
    public QueryResult.CityAndAverageDriverRating[] getDriversAverageRatingsToEachDestinatedCity() {
        
    	String query = "SELECT T.destination, AVG(D.rating) " +
                        "FROM Drivers D, Trips T, Cars C " +
                        "WHERE D.PIN = C.PIN AND T.CarID = C.CarID " +
                        "GROUP BY T.destination " +
                        "ORDER BY T.destination ASC; ";
        List<QueryResult.CityAndAverageDriverRating> avrList = new ArrayList<>();

        try
        {
            PreparedStatement pstmt = connection.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next())
            {
                String a = rs.getString("destination");
                double r = rs.getDouble("AVG(D.rating)");
                avrList.add(new QueryResult.CityAndAverageDriverRating(a, r));
            }
            rs.close();
            pstmt.close();
        }
        catch (Exception e)
        {
            System.err.println("Failed to execute query 5.14: " + e.getMessage());
        }
    	
        return avrList.toArray(new QueryResult.CityAndAverageDriverRating[0]);
    }


    //5.15 Task 15 Find total number of bookings of passengers for each membership status
    @Override
    public QueryResult.MembershipStatusAndTotalBookings[] getTotalBookingsEachMembershipStatus() {
        
    String query = "SELECT Count(B.TripID) , P.membership_status " +
                    "FROM Passengers P, Bookings B " +
                    "WHERE B.PIN = P.PIN " +
                    "GROUP BY P.membership_status " +
                    "ORDER BY P.membership_status ASC; ";
    List<QueryResult.MembershipStatusAndTotalBookings> memList = new ArrayList<>();

    try
    {
        PreparedStatement pstmt = connection.prepareStatement(query);
        ResultSet rs = pstmt.executeQuery();
        while (rs.next())
        {
            String a = rs.getString("membership_status");
            Integer b = rs.getInt("Count(B.TripID)");
            memList.add(new QueryResult.MembershipStatusAndTotalBookings(a, b));


        }
        rs.close();
        pstmt.close();

    }
    catch (Exception e)
    {
        System.err.println("Failed to execute query 5.15: " + e.getMessage());
    }
    	
        return memList.toArray(new QueryResult.MembershipStatusAndTotalBookings[0]);
    }

    
    //5.16 Task 16 For the drivers' ratings, if rating is smaller than 2.0 or equal to 2.0, update the rating by adding 0.5.
    @Override
    public int updateDriverRatings() {
        int rowsUpdated = 0;
        String query = "UPDATE Drivers D " +
                        "SET D.rating = D.rating + 0.5 " +
                        "WHERE D.rating <=2 ";

        try
        {
            Statement stmt = connection.createStatement();
            rowsUpdated = stmt.executeUpdate(query);
            stmt.close();
        }
        catch (Exception e)
        {
            System.err.println("Failed to execute query 5.16: " + e.getMessage());
        }
    	
        return rowsUpdated;
    }
    

    //6.1 (Optional) Task 18 Find trips departing from the given city
    @Override
    public Trip[] getTripsFromCity(String city) {
        
    	/*****************************************************/
        /*****************************************************/
        /*****************  TODO  (Optional)   ***************/
        /*****************************************************/
        /*****************************************************/
    	
        return new Trip[0];
    }
    
    
    //6.2 (Optional) Task 19 Find all trips that have never been booked
    @Override
    public Trip[] getTripsWithNoBooks() {
        
    	/*****************************************************/
        /*****************************************************/
        /*****************  TODO  (Optional)   ***************/
        /*****************************************************/
        /*****************************************************/
    	
        return new Trip[0];
    }
    
    
    //6.3 (Optional) Task 20 For each driver, find the trip(s) with the highest number of bookings
    @Override
    public QueryResult.DriverPINandTripIDandNumberOfBookings[] getTheMostBookedTripsPerDriver() {
        
    	/*****************************************************/
        /*****************************************************/
        /*****************  TODO  (Optional)   ***************/
        /*****************************************************/
        /*****************************************************/
    	
        return new QueryResult.DriverPINandTripIDandNumberOfBookings[0];
    }
    
    
    //6.4 (Optional) Task 21 Find Full Cars
    @Override
    public QueryResult.FullCars[] getFullCars() {
        
    	/*****************************************************/
        /*****************************************************/
        /*****************  TODO  (Optional)   ***************/
        /*****************************************************/
        /*****************************************************/
    	
        return new QueryResult.FullCars[0];
    }

}
