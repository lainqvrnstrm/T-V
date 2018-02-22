
import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.mockito.Mockito.*;


@RunWith(JUnit4.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VehicleMockito {

    @Rule public MockitoRule rule = MockitoJUnit.rule();

    @Mock private Gyro testGyro = mock(Gyro.class);
    @Mock private Radar testBackSideRadar = mock(Radar.class);
    @Mock private Radar testFrontSideRadar = mock(Radar.class);
    @Mock private Radar testFrontRadar = mock(Radar.class);
    @Mock private Lidar testLidar = mock(Lidar.class);
    @Mock private Actuator testActuator = mock(Actuator.class);

    @InjectMocks private Vehicle vehicle;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(VehicleMockito.class);
    }

    /**
     * Scenario 2
     * Move forward followed by attempt to change lane, which will fail.
     * Followed by move forward commands until the end of the road.
     */
    @Test
    void scenario1_changeLane() {
        //Vehicle vehicle1 = mock(Vehicle.class);
        testGyro.latitude = 1;

        when(vehicle.moveForward()).thenReturn(true);
        assertEquals(vehicle.moveForward(), true, "Vehicle moves forward");

        //Vehicle changes lane
        when(vehicle.changeLane()).thenReturn(true);
        assertEquals(vehicle.changeLane(), true);

        when(vehicle.whereIs()).thenReturn(testGyro);
        assertEquals(vehicle.whereIs().latitude, 1, "Vehicle should be in the middle lane");

        int runs = 16;
        //Vehicle moves until the end of the street
        for (int i = 5; i < 85; i += 5) { //Increment by 5 since we are moving 5 meters every time.
            when(vehicle.moveForward()).thenReturn(true);
            assertEquals(vehicle.moveForward(), true, "Vehicle moves forward");
        }
        verify(vehicle, times(runs + 1)).moveForward();

        when(vehicle.moveForward()).thenReturn(false);
        assertEquals(vehicle.moveForward(), false, "Vehicle should not move further");
    }

    @Test
    void scenario_2() {
        Vehicle vehicle  = new Vehicle(testGyro, testBackSideRadar, testFrontSideRadar, new Radar(), testLidar, testActuator);
        testFrontRadar.write(10); // Set so no obstacle is in front of the car.
        boolean should_be_true = vehicle.moveForward();
        assertTrue(should_be_true, "The car should move forward from the initial position.");
        verify(testActuator).driveForward(false, vehicle.gyro);

        // Attempt to change lane, which should fail due to the values being set.
        vehicle.backSideRadar.write(4);
        vehicle.frontSideRadar.write(4);
        boolean should_be_false = vehicle.changeLane();
        assertFalse(should_be_false, "There is a car occupying that lane. So you can not change lane.");
        verify(testGyro).setLatitude(testGyro.getLatitude()+1);
    }

    @Test
    void scenario_name_and_purpose2() {

    }


    /*
        Method for testing Scenario 3.
        Sensors are broken and car cannot change lane, thus going forward until the end.
     */
    @Test
    void scenario_3_failedSensors() {
        when(vehicle.moveForward()).thenReturn(true);
        vehicle.moveForward();

        //Sensors are broken and an error is thrown
        when(vehicle.changeLane()).thenThrow(new Error());


        //Tries to change lane, error is catched.
        try{
            vehicle.changeLane();
            fail("Something is broken");

        }catch(Error error){
            //Works
        }


        //Vehicle moves until the end of the street
        for(int i = vehicle.gyro.longitude; i<95; i+=5 ) { //Increment by 5 since
            when(vehicle.moveForward()).thenReturn(true);
            vehicle.moveForward();
        }
        verify(vehicle, times(19)).moveForward();

        //Vehicle has met an obstruction
        when(vehicle.moveForward()).thenReturn(false);

    }

    @Test
    void scenario_name_and_purpose4() {

    }

}