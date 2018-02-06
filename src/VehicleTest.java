
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VehicleTest {
    private Vehicle vehicle = new Vehicle(); // would always be the same though

    @BeforeEach
    void setUp() {
        // Initializes a new vehicle object, to be used on all test cases.
        vehicle = new Vehicle();
        // Sets the forward facing sensor to a non-detecting value.
        vehicle.radars[2].setValues(50.0, 50.0);
        // Sets the gyro to be the initial start positions.
        vehicle.gyro.longitude = 0;
        vehicle.gyro.latitude = 0;
    }

    /**
     * Calling move forward from the initiali start position on the first most right lane.
     * It asserts if after the function call that the gyro's position has correctly updated
     * with the indicated 5 meters.
     */
    @org.junit.jupiter.api.Test
    void tc0_moveForward() {
        // Sets the gyro to the initial start position.
        vehicle.gyro.longitude = 0;
        // Call the method to be tested, which should update the gyro.
        boolean moved = vehicle.moveForward(); // needs the appropriate Gyro inputs.
        assertTrue(moved, "Vehicle is suppose to move.");
        // Make a secondary gyro to be the expected result.
        Gyro gyro = new Gyro();
        gyro.longitude = 5;
        // Compare the expected with the vehicle gyro.
        assertEquals(gyro.longitude, vehicle.gyro.longitude,
                "Gyro longitude does not match after moving forward.");
    }

    /**
     * Checks a randomly selected middle position which is neither the start or end position.
     * It is meant to test that any randomly selected center value will work.
     */
    @org.junit.jupiter.api.Test
    void tc1_moveForward() {
        // Set the gyro to be in the middle of the road, where no obstacles are.
        vehicle.gyro.longitude = 50;
        // Call the method to be tested, which should update the gyro.
        boolean moved = vehicle.moveForward();
        assertTrue(moved, "Vehicle is suppose to move.");
        // Make a secondary gyro to be the expected result.
        Gyro gyro = new Gyro();
        gyro.longitude = 55;
        // Compare the expected with the vehicle gyro.
        assertEquals(gyro.longitude, vehicle.gyro.longitude,
                "Gyro position does not match after moving forward in the middle of the road.");
    }

    @org.junit.jupiter.api.Test
    void tc2_moveForward() {
        // Set the gyro to be 1 meter away from the end of the road.
        vehicle.gyro.longitude = 99;
        // Call the method to be tested, which should return false.
        boolean move = vehicle.moveForward();
        assertFalse(move, "Vehicle is not suppose to move");

    }

    @org.junit.jupiter.api.Test
    void tc3_moveForward() {
        // Set the gyro to be the end of the road.
        vehicle.gyro.longitude = 100;
        // Call the method to be tested, which should return false.
        boolean move = vehicle.moveForward();
        assertFalse(move, "Vehicle is not suppose to move");
    }


    /**
     * Pre-condition: No car in the left lane.
     */

    @org.junit.jupiter.api.Test
    // TC0 for leftLaneDetect, The radars readings are not valid.
    void tc0_leftLaneDetect() throws Error {

        try {
            // Set so that less than 2 sensors are valid readings.
            // This is done by making all radar values invalid, and thus only the lidar is valid.
            vehicle.radars[0].setValues(15, 55);
            vehicle.radars[1].setValues(40, 30);
            // Calls the test method and stores the result.


            boolean leftLaneIndicator;
            leftLaneIndicator = vehicle.leftLaneDetect();

            assertTrue(leftLaneIndicator, "The radars readings are not valid");
            
        }
        catch(Error e){
            System.out.println("\nThe sensors are not reading the valid values");
        }
    }
    @org.junit.jupiter.api.Test
    //TC1 for leftLaneDetect, 2 or more senors are working, and nothing is detected on the left lane
    void tc1_leftLaneDetect() throws Error{

        //Set 2 sensors are valid readings
        vehicle.radars[0].setValues(15, 15);
        vehicle.radars[1].setValues(30, 30);

        // Calls the test method and stores the result.
        boolean leftLaneIndicator;
        leftLaneIndicator = vehicle.leftLaneDetect();

        assertFalse(leftLaneIndicator, "Nothing is detected on the left lane");

    }
    @org.junit.jupiter.api.Test
    //TC2 for leftLaneDetect, 2 or more sensors are working, but there is a obstacle detected on the left lane.
    void tc2_leftLaneDetect() throws Error{

        //Set 2 sensors are valid readings
        //But one of the sensor is detecting someting on the left lane
        vehicle.radars[0].setValues(15, 15);
        vehicle.radars[1].setValues(3, 3);

        // Calls the test method and stores the result.
        boolean leftLaneIndicator;
        leftLaneIndicator = vehicle.leftLaneDetect();


        assertTrue(leftLaneIndicator, "there is something detected on the left lane");
    }


    @org.junit.jupiter.api.Test
    void tc0_changeLane() {
        // Set so that less than 2 sensors are valid readings.
        // This is done by making all radar values invalid, and thus only the lidar is valid.
        vehicle.radars[0].setValues(15, 55);
        vehicle.radars[1].setValues(40, 30);
        vehicle.radars[2].setValues(1, 3);

        // We are not able to move forward, because the end of road will be detected.
        // Because the radar values are invalid moving forward will be invalidated using two different methods
        // For this test case.
        int gyro_value = 96;
        vehicle.gyro.longitude = gyro_value;

        // Calls the test method and stores the result.
        boolean changeLaneIndicator = vehicle.changeLane();

        // Assert that we did not change lane.
        assertFalse(changeLaneIndicator,
                "The car can not change lane while less than two sensors are " +
                        "functioning and the road is at the end.");

        // Confirm that the car did move.
        assertEquals(vehicle.gyro.longitude, gyro_value,
                "The car cannot move beyond the end of the road even " +
                        "when using changeLane.");

        // Confirm that the car did move to the correct lane.
        assertEquals(vehicle.gyro.latitude, 0,
                "The car can not change lane.");
    }

    @org.junit.jupiter.api.Test
    void tc1_changeLane() {
        // Set so that more than two sensors provide a valid reading.
        vehicle.radars[0].setValues(15, 15);
        vehicle.radars[1].setValues(15, 15);

        // Front facing radar, has to be accurate with the amount of space left on the road.
        vehicle.radars[2].setValues(4, 4);

        // We are not able to move forward, because the end of road will be detected.
        int gyro_value = 96;
        vehicle.gyro.longitude = gyro_value;
        vehicle.gyro.latitude = 0;

        // Calls the test method and stores the result.
        boolean changeLaneIndicator = vehicle.changeLane();

        // Assert that we did not change lane.
        assertFalse(changeLaneIndicator,
                "The car can not change lane when there is no longitude distance to do so." +
                        "functioning and the road is at the end.");

        // Confirm that the car did move.
        assertEquals(vehicle.gyro.longitude, gyro_value,
                "The car cannot move beyond the end of the road even " +
                        "when using changeLane.");

        // Confirm that the car did move to the correct lane.
        assertEquals(vehicle.gyro.latitude, 0,
                "The car van not change lane.");
    }

    @org.junit.jupiter.api.Test
    void tc2_changeLane() {
        // Set so that more than two sensors provide a valid reading.
        vehicle.radars[0].setValues(4, 4);
        vehicle.radars[1].setValues(4, 4);

        // Front facing radar, has to be accurate with the amount of space left on the road.
        vehicle.radars[2].setValues(4, 4);

        // We are not able to move forward, because the end of road will be detected.
        int gyro_value = 96;
        vehicle.gyro.longitude = gyro_value;

        // Calls the test method and stores the result.
        boolean changeLaneIndicator = vehicle.changeLane();

        // Assert that we did not change lane, because of an obstacle.
        assertFalse(changeLaneIndicator,
                "The car can not change lane when there is a car detected in the lane to the left of it.");

        // Confirm that the car did move.
        assertEquals(vehicle.gyro.longitude, gyro_value,
                "The car cannot move beyond the end of the road even " +
                        "when using changeLane.");

        // Confirm that the car did move to the correct lane.
        assertEquals(vehicle.gyro.latitude, 0,
                "The car can not change lane.");
    }

    @org.junit.jupiter.api.Test
    void tc3_changeLane() {
        // Set it so that less than two sensors are functioning.
        vehicle.radars[0].setValues(45, 5);
        vehicle.radars[1].setValues(4, 45);

        // The car will be able to move forward, because we are in the middle of the road.
        int gyro_value = 50;
        vehicle.gyro.longitude = gyro_value;

        // Calls the test method and stores the result.
        boolean changeLaneIndicator = vehicle.changeLane();
        
        // Assert that we did not change lane, because incorrect sensor readings.
        assertFalse(changeLaneIndicator,
                "The car can not change lane when there is a car detected in the lane to the left of it.");

        // Confirm that the car did move.
        assertEquals(vehicle.gyro.longitude, gyro_value +5,
                "The car is suppose to move forward, even if it cannot change lane.");

        // Confirm that the car did move to the correct lane.
        assertEquals(vehicle.gyro.latitude, 0,
                "The car can not change lane.");
    }

    @org.junit.jupiter.api.Test
    void tc4_changeLane() {
        // Set so that more than two sensors provide a valid reading.
        vehicle.radars[0].setValues(4, 4);
        vehicle.radars[1].setValues(4, 4);

        // Front facing radar, has to be accurate with the amount of space left on the road.
        vehicle.radars[2].setValues(50, 50);

        // The car will be able to move forward, because we are in the middle of the road.
        int gyro_value = 50;
        vehicle.gyro.longitude = gyro_value;

        // Calls the test method and stores the result.
        boolean changeLaneIndicator = vehicle.changeLane();

        // Assert that we did not change lane, because incorrect sensor readings.
        assertFalse(changeLaneIndicator,
                "The car can not change lane when there is a car detected in the lane to the left of it.");

        // Confirm that the car did move.
        assertEquals(vehicle.gyro.longitude, gyro_value +5,
                "The car is suppose to move forward, even if it cannot change lane.");

        // Confirm that the car did move to the correct lane.
        assertEquals(vehicle.gyro.latitude, 0,
                "The car can not change lane.");
    }

    @org.junit.jupiter.api.Test
    void tc5_changeLane() {
        // Set so that more than two sensors provide a valid reading.
        vehicle.radars[0].setValues(50, 50);
        vehicle.radars[1].setValues(50, 50);

        // Front facing radar, has to be accurate with the amount of space left on the road.
        vehicle.radars[2].setValues(50, 50);

        // The car will be able to move forward, because we are in the middle of the road.
        int gyro_value = 50;
        vehicle.gyro.longitude = gyro_value;

        // Set lane to be 0.
        vehicle.gyro.latitude = 0;

        // Calls the test method and stores the result.
        boolean changeLaneIndicator = vehicle.changeLane();

        // Assert that we did not change lane, because incorrect sensor readings.
        assertTrue(changeLaneIndicator,
                "The car should change lane when there is no car detected in the lane to the left of it.");

        // Confirm that the car did move.
        assertEquals(vehicle.gyro.longitude, gyro_value +5,
                "The car is suppose to move forward, even if it cannot change lane.");

        // Confirm that the car did move to the correct lane.
        assertEquals(vehicle.gyro.latitude, 1,
                "The car must change lane.");
    }


    @org.junit.jupiter.api.Test
    void tc0_whereIs() {
        Gyro newGyro; //Setup a test Gyro
        vehicle.gyro.longitude = 0;
        vehicle.gyro.latitude = 0; // Should be in the right-most lane.

        //Fetch the vehicle gyro values using whereIs().
        newGyro = vehicle.whereIs();
        //Test if the vehicle is said to be in the correct position.
        assertEquals(newGyro.longitude, 0, "Should be in start position.");
        assertEquals(newGyro.latitude, 0, "Should be in the first lane.");
    }

    @org.junit.jupiter.api.Test
    void tc1_whereIs(){
        Gyro testGyro; //Setup a test Gyro
        vehicle.gyro.longitude = 55;
        vehicle.gyro.latitude = 0; //Should be in the right-most lane.
        vehicle.moveForward();
        //Fetch the vehicle gyro values using whereIs().
        testGyro = vehicle.whereIs();
        //Test if the vehicle is said to be in the correct position.
        assertEquals(testGyro.longitude, 60, "Should be in the middle of the street.");
        assertEquals(testGyro.latitude, 0, "Should be in the first lane.");

    }

    @org.junit.jupiter.api.Test
    void tc2_whereIs(){
        Gyro testGyro; //Setup a test Gyro
        vehicle.gyro.longitude = 35;
        vehicle.gyro.latitude = 1; //Should be in the middle lane.
        vehicle.moveForward();
        //Fetch the vehicle gyro values using whereIs().
        testGyro = vehicle.whereIs();
        //Test if the vehicle is said to be in the correct position.
        assertEquals(testGyro.longitude, 40, "Should be in the middle of the street.");
        assertEquals(testGyro.latitude, 1, "Should be in the middle lane");

    }

    @org.junit.jupiter.api.Test
    void tc3_whereIs(){
        Gyro testGyro; //Setup a test Gyro
        //Sets values to the vehicle gyro that we can fetch.
        vehicle.gyro.longitude = 45;
        vehicle.gyro.latitude = 2; //Should be in the left-most lane.
        //Move forward to the expected position
        vehicle.moveForward();
        //Fetch the vehicle gyro values using whereIs().
        testGyro = vehicle.whereIs();

        //Test if the vehicle is said to be in the correct position.
        assertEquals(testGyro.longitude, 50, "Should be in the middle of the street.");
        assertEquals(testGyro.latitude, 2, "Should be in the left most lane.");

    }

    @org.junit.jupiter.api.Test
    void tc4_whereIs(){
        Gyro testGyro; //Setup a test Gyro
        //Sets values to the gyro that we can fetch.
        vehicle.gyro.longitude = 95;
        vehicle.gyro.latitude = 0; //Should be in the right-most lane.

        //Move the vehicle forward using the moveForward() method to end up at the street end.
        vehicle.moveForward();

        //Fetch the vehicle gyro values using the whereIs() method.
        testGyro = vehicle.whereIs();
        //Test if the vehicle is said to be in the correct position.
        assertEquals(testGyro.longitude, 100, "Should be at the end of the street.");
        assertEquals(testGyro.latitude, 0, "Should be in the right-most lane.");
    }

    @org.junit.jupiter.api.Test
    void tc5_whereIs(){
        Gyro testGyro; //Setup a test Gyro
        //Sets values to the gyro that we can fetch.
        vehicle.gyro.longitude = 100;
        vehicle.gyro.latitude = 1; //Should be in the second lane.

        //Fetch the vehicle gyro values using the whereIs() method.
        testGyro = vehicle.whereIs();
        //Test if the vehicle is said to be in the correct position.
        assertEquals(testGyro.longitude, 100, "Should be at the end of the street.");
        assertEquals(testGyro.latitude, 1, "Should be in the middle lane.");
    }

    @org.junit.jupiter.api.Test
    void tc6_whereIs(){
        Gyro testGyro; //Setup a test Gyro
        //Sets values to the gyro that we can fetch.
        vehicle.gyro.longitude = 100;
        vehicle.gyro.latitude = 2; //Should be in the left-most lane.

        //Fetch the vehicle gyro values using the whereIs() method.
        testGyro = vehicle.whereIs();
        //Test if the vehicle is said to be in the correct position.
        assertEquals(testGyro.longitude, 100, "Should be at the end of the street.");
        assertEquals(testGyro.latitude, 2, "Should be in the left-most lane.");
    }
}