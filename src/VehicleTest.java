class VehicleTest {

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        Vehicle vehicle = new Vehicle();
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
        vehicle.moveForward();
        // Make a secondary gyro to be the expected result.
        Gyro gyro = new Gyro();
        gyro.longitude = 5;
        // Compare the expected with the vehicle gyro.
        assertEquals(gyro, vehicle.gyro,
                "Gyro position does not match after moving forward.");
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
        vehicle.moveForward();
        // Make a secondary gyro to be the expected result.
        Gyro gyro = new Gyro();
        gyro.longitude = 55;
        // Compare the expected with the vehicle gyro.
        assertEquals(gyro, vehicle.gyro,
                "Gyro position does not match after moving forward in the middle of the road.");
    }

    @org.junit.jupiter.api.Test
    void leftLaneDetect() {
    }

    @org.junit.jupiter.api.Test
    void changeLane() {
    }

    @org.junit.jupiter.api.Test
    void whereIs() {
    }
}