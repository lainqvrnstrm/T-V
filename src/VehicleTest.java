
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VehicleTest {
    private Vehicle vehicle; // would always be the same though

    @BeforeEach
    void setUp() {
        // Initializes a new vehicle object, to be used on all test cases.
        vehicle = new Vehicle();
    }

    /**
     * Calling move forward from the initiali start position on the first most right lane.
     * It asserts if after the function call that the gyro's position has correctly updated
     * with the indicated 5 meters.
     */
    @org.junit.jupiter.api.Test
    void tc0_moveForward() {        // Sets the gyro to the initial start position.
        vehicle.getGyro().setLongitude(0);
        // Call the method to be tested, which should update the gyro.
        boolean moved = vehicle.moveForward(); // needs the appropriate Gyro inputs.
        assertTrue(moved, "Vehicle is suppose to move.");
        // Make a secondary gyro to be the expected result.
        Gyro gyro = new Gyro();
        gyro.setLongitude(5);
        // Compare the expected with the vehicle gyro.
        assertEquals(gyro.getLongitude(), vehicle.getGyro().getLongitude(),
                "Gyro longitude does not match after moving forward.");
    }

    /**
     * Checks a randomly selected middle position which is neither the start or end position.
     * It is meant to test that any randomly selected center value will work.
     */
    @org.junit.jupiter.api.Test
    void tc1_moveForward() {
        // Set the gyro to be in the middle of the road, where no obstacles are.
        vehicle.getGyro().setLongitude(50);
        // Call the method to be tested, which should update the gyro.
        boolean moved = vehicle.moveForward();
        assertTrue(moved, "Vehicle is suppose to move.");
        // Make a secondary gyro to be the expected result.
        Gyro gyro = new Gyro();
        gyro.setLongitude(55);
        // Compare the expected with the vehicle gyro.
        assertEquals(gyro.getLongitude(), vehicle.getGyro().getLongitude(),
                "Gyro position does not match after moving forward in the middle of the road.");
    }

    @org.junit.jupiter.api.Test
    void tc2_moveForward() {
        // Set the gyro to be 1 meter away from the end of the road.
        vehicle.getGyro().setLongitude(99);
        // Call the method to be tested, which should return false.
        boolean move = vehicle.moveForward();
        assertFalse(move, "Vehicle is not suppose to move");

    }

    @org.junit.jupiter.api.Test
    void tc3_moveForward() {
        // Set the gyro to be the end of the road.
        vehicle.getGyro().setLongitude(100);
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

        boolean caught = false;

        try {
            // Set so that less than 2 sensors are valid readings.
            // This is done by making all radar values invalid, and thus only the lidar is valid.
            vehicle.getBackSideRadar().write(15);
            vehicle.getFrontSideRadar().write(40);
            vehicle.getLidar().writeIndex(45, 50);

            // Make a vehicle clone to fake the second query readings.
            Vehicle vehicle_copy = new Vehicle();
            vehicle_copy.getBackSideRadar().write(-1);
            vehicle_copy.getFrontSideRadar().write(-1);
            vehicle_copy.getLidar().writeIndex(45, 40);

            boolean leftLaneIndicator;
            leftLaneIndicator = vehicle.leftLaneDetect(vehicle_copy);

        }

        // Catches errors.
        catch(Error e){
            caught = true;
        } finally {
            assertTrue(caught, "An Error code must be caught.");

        }
    }
    @org.junit.jupiter.api.Test
        //TC1 for leftLaneDetect, 2 or more senors are working, and nothing is detected on the left lane
    void tc1_leftLaneDetect() throws Error{

        //Set 2 sensors are valid readings
        vehicle.getBackSideRadar().write(15);
        vehicle.getFrontSideRadar().write(15);
        vehicle.getLidar().writeIndex(45, 15);

        // Second query readings
        Vehicle clone = new Vehicle();
        clone.getBackSideRadar().write(15);
        clone.getFrontSideRadar().write(15);
        clone.getLidar().writeIndex(45, 15);

        // Calls the test method and stores the result.
        boolean leftLaneIndicator;
        leftLaneIndicator = vehicle.leftLaneDetect(clone);

        assertFalse(leftLaneIndicator, "Nothing is detected on the left lane");

    }
    @org.junit.jupiter.api.Test
        //TC2 for leftLaneDetect, 2 or more sensors are working, but there is a obstacle detected on the left lane.
    void tc2_leftLaneDetect() throws Error{

        //Set 2 sensors are valid readings
        //But one of the sensor is detecting someting on the left lane
        vehicle.getBackSideRadar().write(15);
        vehicle.getFrontSideRadar().write(3);
        vehicle.getLidar().writeIndex(45, 5);

        // Second query readings
        Vehicle clone = new Vehicle();
        clone.getBackSideRadar().write(15);
        clone.getFrontSideRadar().write(3);
        clone.getLidar().writeIndex(45, 5);

        // Calls the test method and stores the result.
        boolean leftLaneIndicator;
        leftLaneIndicator = vehicle.leftLaneDetect(clone);


        assertTrue(leftLaneIndicator, "there is something detected on the left lane");
    }


    @org.junit.jupiter.api.Test
    void tc0_changeLane() {
        // Set so that less than 2 sensors are valid readings.
        // This is done by making all radar values invalid, and thus only the lidar is valid.
        vehicle.getBackSideRadar().write(15);
        vehicle.getFrontSideRadar().write(40);
        vehicle.getFrontRadar().write(1);
        vehicle.getLidar().writeIndex(45, 55);

        // Make a vehicle clone to fake the second query readings.
        Vehicle vehicle_copy = new Vehicle();
        vehicle_copy.getBackSideRadar().write(-1);
        vehicle_copy.getFrontSideRadar().write(-1);
        vehicle_copy.getFrontRadar().write(-1);
        vehicle_copy.getLidar().writeIndex(45, -1);

        // We are not able to move forward, because the end of road will be detected.
        // Because the radar values are invalid moving forward will be invalidated using two different methods
        // For this test case.
        int gyro_value = 96;
        vehicle.getGyro().setLongitude(gyro_value);

        // Calls the test method and stores the result.
        boolean changeLaneIndicator = vehicle.changeLane(vehicle_copy);

        // Assert that we did not change lane.
        assertFalse(changeLaneIndicator,
                "The car can not change lane while less than two sensors are " +
                        "functioning and the road is at the end.");

        // Confirm that the car did move.
        assertEquals(vehicle.getGyro().getLongitude(), gyro_value,
                "The car cannot move beyond the end of the road even " +
                        "when using changeLane.");

        // Confirm that the car did move to the correct lane.
        assertEquals(vehicle.getGyro().getLatitude(), 0,
                "The car can not change lane.");
    }

    @org.junit.jupiter.api.Test
    void tc1_changeLane() {
        // Set so that more than two sensors provide a valid reading.
        vehicle.getBackSideRadar().write(15);
        vehicle.getFrontSideRadar().write(15);
        vehicle.getLidar().writeIndex(45, 18);

        // Front facing radar, has to be accurate with the amount of space left on the road.
        vehicle.getFrontRadar().write(4);

        // We are not able to move forward, because the end of road will be detected.
        int gyro_value = 96;
        vehicle.getGyro().setLongitude(gyro_value);
        vehicle.getGyro().setLatitude(0);

        // Calls the test method and stores the result.
        boolean changeLaneIndicator = vehicle.changeLane();

        // Assert that we did not change lane.
        assertFalse(changeLaneIndicator,
                "The car can not change lane when there is no longitude distance to do so. " +
                        "functioning and the road is at the end.");

        // Confirm that the car did move.
        assertEquals(vehicle.getGyro().getLongitude(), gyro_value,
                "The car cannot move beyond the end of the road even " +
                        "when using changeLane.");

        // Confirm that the car did move to the correct lane.
        assertEquals(vehicle.getGyro().getLatitude(), 0,
                "The car can not change lane.");
    }

    @org.junit.jupiter.api.Test
    void tc2_changeLane() {
        // Set so that more than two sensors provide a valid reading.
        vehicle.getBackSideRadar().write(4);
        vehicle.getFrontSideRadar().write(4);

        // Front facing radar, has to be accurate with the amount of space left on the road.
        vehicle.getFrontRadar().write(4);

        // We are not able to move forward, because the end of road will be detected.
        int gyro_value = 96;
        vehicle.getGyro().setLongitude(gyro_value);

        // Calls the test method and stores the result.
        boolean changeLaneIndicator = vehicle.changeLane();

        // Assert that we did not change lane, because of an obstacle.
        assertFalse(changeLaneIndicator,
                "The car can not change lane when there is a car detected in the lane to the left of it.");

        // Confirm that the car did move.
        assertEquals(vehicle.getGyro().getLongitude(), gyro_value,
                "The car cannot move beyond the end of the road even " +
                        "when using changeLane.");

        // Confirm that the car did move to the correct lane.
        assertEquals(vehicle.getGyro().getLatitude(), 0,
                "The car can not change lane.");
    }

    @org.junit.jupiter.api.Test
    void tc3_changeLane() { // TODO this test might be wrong.
        // Set it so that less than two sensors are functioning.
        vehicle.getBackSideRadar().write(45);
        vehicle.getFrontSideRadar().write(5);

        // Make a vehicle clone to fake the second query readings.
        Vehicle vehicle_copy = new Vehicle();
        vehicle_copy.getBackSideRadar().write(5);
        vehicle_copy.getFrontSideRadar().write(45);

        // Invalid lidar reading, because it does not match the original.
        vehicle_copy.getLidar().writeIndex(45, -1);

        // The car will be able to move forward, because we are in the middle of the road.
        int gyro_value = 50;
        vehicle.getGyro().setLongitude(gyro_value);

        // Calls the test method and stores the result.
        boolean changeLaneIndicator = vehicle.changeLane(vehicle_copy);

        // Assert that we did not change lane, because incorrect sensor readings.
        assertFalse(changeLaneIndicator,
                "The car can not change lane when there is a car detected in the lane to the left of it.");

        // Confirm that the car did move.
        assertEquals(vehicle.getGyro().getLongitude(), gyro_value +5,
                "The car is suppose to move forward, even if it cannot change lane.");

        // Confirm that the car did move to the correct lane.
        assertEquals(vehicle.getGyro().getLatitude(), 0,
                "The car can not change lane.");
    }

    @org.junit.jupiter.api.Test
    void tc4_changeLane() {
        // Set so that more than two sensors provide a valid reading.
        vehicle.getBackSideRadar().write(4);
        vehicle.getFrontSideRadar().write(4);

        // Front facing radar, has to be accurate with the amount of space left on the road.
        vehicle.getFrontRadar().write(50);

        // The car will be able to move forward, because we are in the middle of the road.
        int gyro_value = 50;
        vehicle.getGyro().setLongitude(gyro_value);

        // Calls the test method and stores the result.
        boolean changeLaneIndicator = vehicle.changeLane();

        // Assert that we did not change lane, because incorrect sensor readings.
        assertFalse(changeLaneIndicator,
                "The car can not change lane when there is a car detected in the lane to the left of it.");

        // Confirm that the car did move.
        assertEquals(vehicle.getGyro().getLongitude(), gyro_value +5,
                "The car is suppose to move forward, even if it cannot change lane.");

        // Confirm that the car did move to the correct lane.
        assertEquals(vehicle.getGyro().getLatitude(), 0,
                "The car can not change lane.");
    }

    @org.junit.jupiter.api.Test
    void tc5_changeLane() {
        // Set so that more than two sensors provide a valid reading.
        vehicle.getBackSideRadar().write(50);
        vehicle.getFrontSideRadar().write(50);
        // There is no car adjacent.
        vehicle.getLidar().writeIndex(45, 20);

        // Front facing radar, has to be accurate with the amount of space left on the road.
        vehicle.getFrontRadar().write(50);

        // The car will be able to move forward, because we are in the middle of the road.
        int gyro_value = 50;
        vehicle.getGyro().setLongitude(gyro_value);

        // Set lane to be 0.
        vehicle.getGyro().setLatitude(0);

        // Calls the test method and stores the result.
        boolean changeLaneIndicator = vehicle.changeLane();

        // Assert that we did not change lane, because incorrect sensor readings.
        assertTrue(changeLaneIndicator,
                "The car should change lane when there is no car detected in the lane to the left of it.");

        // Confirm that the car did move.
        assertEquals(vehicle.getGyro().getLongitude(), gyro_value +5,
                "The car is suppose to move forward, even if it cannot change lane.");

        // Confirm that the car did move to the correct lane.
        assertEquals(vehicle.getGyro().getLatitude(), 1,
                "The car must change lane.");
    }


    @org.junit.jupiter.api.Test
    void tc0_whereIs() {
        Gyro newGyro; //Setup a test Gyro
        vehicle.getGyro().setLongitude(0);
        vehicle.getGyro().setLatitude(0); // Should be in the right-most lane.

        //Fetch the vehicle gyro values using whereIs().
        newGyro = vehicle.whereIs();
        //Test if the vehicle is said to be in the correct position.
        assertEquals(newGyro.getLongitude(), 0, "Should be in start position.");
        assertEquals(newGyro.getLatitude(), 0, "Should be in the first lane.");
    }

    @org.junit.jupiter.api.Test
    void tc1_whereIs(){
        Gyro testGyro; //Setup a test Gyro
        vehicle.getGyro().setLongitude(55);
        vehicle.getGyro().setLatitude(0); //Should be in the right-most lane.
        vehicle.moveForward();
        //Fetch the vehicle gyro values using whereIs().
        testGyro = vehicle.whereIs();
        //Test if the vehicle is said to be in the correct position.
        assertEquals(testGyro.getLongitude(), 60, "Should be in the middle of the street.");
        assertEquals(testGyro.getLatitude(), 0, "Should be in the first lane.");

    }

    @org.junit.jupiter.api.Test
    void tc2_whereIs(){
        Gyro testGyro; //Setup a test Gyro
        vehicle.getGyro().setLongitude(35);
        vehicle.getGyro().setLatitude(1); //Should be in the middle lane.
        vehicle.moveForward();
        //Fetch the vehicle gyro values using whereIs().
        testGyro = vehicle.whereIs();
        //Test if the vehicle is said to be in the correct position.
        assertEquals(testGyro.getLongitude(), 40, "Should be in the middle of the street.");
        assertEquals(testGyro.getLatitude(), 1, "Should be in the middle lane");

    }

    @org.junit.jupiter.api.Test
    void tc3_whereIs(){
        Gyro testGyro; //Setup a test Gyro
        //Sets radar values so that we can use changeLane().
        vehicle.getBackSideRadar().write(15);
        vehicle.getFrontSideRadar().write(15);
        // Makes sure no vehicle to the left.
        vehicle.getLidar().writeIndex(45, 20);

        //Sets values to the vehicle gyro that we can fetch.
        vehicle.getGyro().setLongitude(45);
        vehicle.getGyro().setLatitude(1); //Should be in the left-most lane.
        //Move forward to the expected position
        vehicle.changeLane();
        //Fetch the vehicle gyro values using whereIs().
        testGyro = vehicle.whereIs();

        //Test if the vehicle is said to be in the correct position.
        assertEquals(testGyro.getLongitude(), 50, "Should be in the middle of the street.");
        assertEquals(testGyro.getLatitude(), 2, "Should be in the left most lane.");

    }

    @org.junit.jupiter.api.Test
    void tc4_whereIs(){
        Gyro testGyro; //Setup a test Gyro
        //Sets values to the gyro that we can fetch.
        vehicle.getGyro().setLongitude(95);
        vehicle.getGyro().setLatitude(0); //Should be in the right-most lane.

        //Move the vehicle forward using the moveForward() method to end up at the street end.
        vehicle.moveForward();

        //Fetch the vehicle gyro values using the whereIs() method.
        testGyro = vehicle.whereIs();
        //Test if the vehicle is said to be in the correct position.
        assertEquals(testGyro.getLongitude(), 100, "Should be at the end of the street.");
        assertEquals(testGyro.getLatitude(), 0, "Should be in the right-most lane.");
    }

    @org.junit.jupiter.api.Test
    void tc5_whereIs(){
        Gyro testGyro; //Setup a test Gyro
        //Sets values to the gyro that we can fetch.
        vehicle.getGyro().setLongitude(100);
        vehicle.getGyro().setLatitude(1); //Should be in the second lane.

        //Fetch the vehicle gyro values using the whereIs() method.
        testGyro = vehicle.whereIs();
        //Test if the vehicle is said to be in the correct position.
        assertEquals(testGyro.getLongitude(), 100, "Should be at the end of the street.");
        assertEquals(testGyro.getLatitude(), 1, "Should be in the middle lane.");
    }

    @org.junit.jupiter.api.Test
    void tc6_whereIs(){
        Gyro testGyro; //Setup a test Gyro
        //Sets values to the gyro that we can fetch.
        vehicle.getGyro().setLongitude(100);
        vehicle.getGyro().setLatitude(2); //Should be in the left-most lane.

        //Fetch the vehicle gyro values using the whereIs() method.
        testGyro = vehicle.whereIs();
        //Test if the vehicle is said to be in the correct position.
        assertEquals(testGyro.getLongitude(), 100, "Should be at the end of the street.");
        assertEquals(testGyro.getLatitude(), 2, "Should be in the left-most lane.");
    }

    /**
     * The vehicle's longitude position remains the same if there is an obstacle detected in front of it.
     */
    @org.junit.jupiter.api.Test
    void tc0_driveForward(){

        // Vehicle at the start position
        vehicle.getGyro().setLongitude(0);
        vehicle.getGyro().setLatitude(0);

        // Use the actuator's driveForward method to move the car forward
        vehicle.getActuator().driveForward(true, vehicle.getGyro(), vehicle.getSpeed());

        assertEquals(0, vehicle.getGyro().getLongitude(), "This should be 5 meters into the street.");
        assertEquals(0, vehicle.getGyro().getLatitude(), "This should be the starting lane.");
    }
    /**
     * The vehicle's longitude position is updated if there is no obstacle detected in front of it.
     */
    @org.junit.jupiter.api.Test
    void tc1_driveForward(){

        // Vehicle at the start position
        vehicle.getGyro().setLongitude(0);
        vehicle.getGyro().setLatitude(0);

        // Use the actuator's driveForward method to move the car forward
        vehicle.getActuator().driveForward(false, vehicle.getGyro(), vehicle.getSpeed());

        assertEquals(5, vehicle.getGyro().getLongitude(), "This should be 0 meters into the street.");
        assertEquals(0, vehicle.getGyro().getLatitude(), "This should be the starting lane.");
    }

    /**
     * The vehicle's longitude and latitude position remains the same if there is no obstacle detected to the left of it.
     */
    @org.junit.jupiter.api.Test
    void tc0_changeLeft(){
        // Vehicle at the start position
        vehicle.getGyro().setLongitude(0);
        vehicle.getGyro().setLatitude(0);

        // Use the actuator's driveForward method to move the car forward
        vehicle.getActuator().changeLeft(true, vehicle.getGyro());

        assertEquals(0, vehicle.getGyro().getLongitude(), "This should be 0 meters into the street.");
        assertEquals(0, vehicle.getGyro().getLatitude(), "This should be the starting lane.");

    }

    /**
     * The vehicle's longitude and latitude position is updated if there is no obstacle detected to the left of it.
     */
    @org.junit.jupiter.api.Test
    void tc1_changeLeft(){
        // Vehicle at the start position
        vehicle.getGyro().setLongitude(0);
        vehicle.getGyro().setLatitude(0);

        // Use the actuator's driveForward method to move the car forward
        vehicle.getActuator().changeLeft(false, vehicle.getGyro());

        assertEquals(0, vehicle.getGyro().getLongitude(), "This should be 5 meters into the street.");
        assertEquals(1, vehicle.getGyro().getLatitude(), "This should be lane nr 1.");
    }
}