import org.junit.jupiter.api.BeforeEach;
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
    void tc1_leftLaneDetect() {

        //assertEquals(false, vehicle.leftLaneDetect(), "A car " +
        //        "is detected when there is no car present.");

    }

    /**
     *
     */
    @org.junit.jupiter.api.Test
    void tc2_leftLaneDetect() {

        //assertEquals(true, vehicle.leftLaneDetect());

    }

    @org.junit.jupiter.api.Test
    void tc0_changeLane() {
        // Set so that less than 2 sensors are valid readings.
        // This si done by making all radar values invalid, and thus only the lidar is valid.
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
    }

    void tc0_whereIs() {
        Gyro newGyro;
        vehicle.gyro.longitude = 0;
        vehicle.gyro.latitude = 1;

        newGyro = vehicle.whereIs();

        assertEquals(newGyro.longitude, 0, "Should be in start position.");
        assertEquals(newGyro.latitude, 1, "Should be in the first lane.");
    }

    @org.junit.jupiter.api.Test
    void tc1_whereIs() {
        Gyro testGyro;
        vehicle.gyro.longitude = 50;
        vehicle.gyro.latitude = 1;

        testGyro = vehicle.whereIs();

        assertEquals(testGyro.longitude, 50, "Should be in the middle of the street.");
        assertEquals(testGyro.latitude, 1, "Should be in the first lane.");

    }

    @org.junit.jupiter.api.Test
    void tc2_whereIs() {
        Gyro testGyro;
        vehicle.gyro.longitude = 50;
        vehicle.gyro.latitude = 2;

        testGyro = vehicle.whereIs();

        assertEquals(testGyro.longitude, 50, "Should be in the middle of the street.");
        assertEquals(testGyro.latitude, 2, "Should be in the middle lane");

    }

    @org.junit.jupiter.api.Test
    void tc3_whereIs() {
        Gyro testGyro;
        //Sets values to the vehicle gyro that we can fetch.
        vehicle.gyro.longitude = 50;
        vehicle.gyro.latitude = 3;
        //Use the whereIs() method to fetch the vehicle gyro values.
        testGyro = vehicle.whereIs();

        //Test if the vehicle is in the correct positions.
        assertEquals(testGyro.longitude, 50, "Should be in the middle of the street.");
        assertEquals(testGyro.latitude, 3, "Should be in the left most lane.");

    }

    @org.junit.jupiter.api.Test
    void tc4_whereIs() {
        Gyro testGyro;
    }
}