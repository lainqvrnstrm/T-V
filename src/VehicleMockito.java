
import org.junit.Before;
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
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VehicleMockito {

    @Rule public MockitoRule rule = MockitoJUnit.rule();

    @InjectMocks private Vehicle vehicle;

    @Mock private Gyro testGyro = mock(Gyro.class);
    @Mock private Radar testBackSideRadar = mock(Radar.class);
    @Mock private Radar testFrontSideRadar = mock(Radar.class);
    @Mock private Radar testFrontRadar = mock(Radar.class);
    @Mock private Lidar testLidar = mock(Lidar.class);
    @Mock private Actuator testActuator = mock(Actuator.class);

    @Before
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @BeforeEach
    void setUp_vehicle() {
        vehicle = mock(Vehicle.class);
        vehicle.setActuator(mock(Actuator.class));
    }

    /**
     * Scenario 2
     * Move forward followed by attempt to change lane, which will fail.
     * Followed by move forward commands until the end of the road.
     */
    @Test
    void scenario1_changeLane() {
        //Vehicle vehicle1 = mock(Vehicle.class);
        testGyro.setLatitude(1);

        when(vehicle.moveForward()).thenReturn(true);
        assertEquals(vehicle.moveForward(), true, "Vehicle moves forward");

        //Vehicle changes lane
        when(vehicle.changeLane()).thenReturn(true);
        assertEquals(vehicle.changeLane(), true);

        when(vehicle.whereIs()).thenReturn(testGyro);
        assertEquals(vehicle.whereIs().getLatitude(), 1, "Vehicle should be in the middle lane");

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

        /*
        Only use as a reference, don't copy paste stuff.
        Follows this structure:
            Set mocked objects return value
            Call controller method
            Verify that the model got the request to change the data.
        */

        /*
        Create a vehicle Object which is the Controller and the mocked objects are the models' which store all the data
        for it's given state. This enables the use of the Vehicle methods as a controller and to test that the model
        updates according to the specified function call in the scenario.
         */
        Vehicle vehicle  = new Vehicle(testGyro, testBackSideRadar, testFrontSideRadar, testFrontRadar, testLidar, testActuator);
        /*
        Sets the return values of all of the sensor readings to match this scenario. In this case it would be to
        move forward followed by an attempt to change lane then proceeding to the end of the road.
        To confirm that the vehicle has not moved we use the implemented Actuator class. While the gyro is only checked
        to confirm the number of continuous stubbing required in the declaration. It is assumed the car to the left
         */
        int[] obstacle = new int[360];
        int[] no_obstacle = new int[360];
        obstacle[45] = 4;

        when(vehicle.getLidar().read()).thenReturn(no_obstacle, obstacle, no_obstacle); // stets no obstacle on the 2nd iteration of the lidar reading.
        when(vehicle.getGyro().getLongitude()).thenReturn(5, 5, 10, 10, 100); // Jumps in distance between 10 and 100 since nothing intresting happens inbetween.
        when(vehicle.getFrontRadar().read()).thenReturn(10.0); // Set front radar reading to 10.
        when(vehicle.getFrontSideRadar().read()).thenReturn(4.0, 0.0); // side to 4 then 0.
        when(vehicle.getBackSideRadar().read()).thenReturn(4.0); // Obstacle detected when read.

        vehicle.moveForward(); // call the controller method to move forward.
        verify(vehicle.getActuator()).driveForward(false, vehicle.getGyro());

        vehicle.changeLane(); // call the controller method to change lane.
        verify(vehicle.getActuator(), never()).changeLeft(false, vehicle.getGyro());

        reset(vehicle.getActuator()); // Reset to check that it won't be called at the end.
        vehicle.moveForward(); // The car won't be able to move.
        verify(vehicle.getActuator(), never()).driveForward(anyBoolean(), anyObject());
        verify(vehicle.getGyro(), times(5)).getLongitude();

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
        for(int i = vehicle.getGyro().getLongitude(); i<95; i+=5 ) { //Increment by 5 since
            when(vehicle.moveForward()).thenReturn(true);
            vehicle.moveForward();
        }
        verify(vehicle, times(19)).moveForward();

        //Vehicle has met an obstruction
        when(vehicle.moveForward()).thenReturn(false);

    }
     /*
    Method for testing Scenario 4
    Car tries to change lane, the first query detects an obstacle and the second query broke.
    So the car moves forward till the end of the street
    */
    @Test
    void scenario_4_failedSensor() {
        //The car moves forward
        when(vehicle.moveForward()).thenReturn(true);
        vehicle.moveForward();

        //The car tries to change lane by calling leftLaneDetect()
        // there is something detected.

        when(vehicle.leftLaneDetect()).thenReturn(true);
        assertEquals(vehicle.changeLane(), true);

        //When car wants to change lane, the second query returns error code showing sensors are broken
        when(vehicle.changeLane()).thenThrow(new Error());
        try{
            vehicle.changeLane();
            fail("Something is broken");

        }catch(Error error){

        }

        //The car moves till the end of the street
        for(int i = vehicle.getGyro().getLongitude(); i<95; i+=5 ) { //Increment by 5 since
            when(vehicle.moveForward()).thenReturn(true);
            vehicle.moveForward();
        }
        verify(vehicle, times(19)).moveForward();

        //Vehicle has met an obstruction
        when(vehicle.moveForward()).thenReturn(false);

    }

    /**
     * An obstacle is detected in the first query but not the second, when trying to change to the left lane.
     * This should result in the vehicle NOT changing lane.
     */

    @Test
    void scenario5_obstacleDetectedOnce() {

        testGyro = mock(Gyro.class);
        testBackSideRadar = mock(Radar.class);
        testFrontSideRadar = mock(Radar.class);
        testFrontRadar = mock(Radar.class);
        testLidar = mock(Lidar.class);
        testActuator = mock(Actuator.class);

        // The vehicle starts at the beginning of the street. The sensors are set to their default value, which means they are initially not detecting anything.
        Vehicle vehicle  = new Vehicle(testGyro, testBackSideRadar, testFrontSideRadar, testFrontRadar, testLidar, testActuator);

        int[] front_obstacle = new int[360];
        int[] leftSide_obstacle = new int[360];
        int[] no_obstacle = new int[360];
        front_obstacle[0] = 1;  // Dubbelkolla, så vettiga värden
        leftSide_obstacle[45] = 3;  // Dubbelkolla, så vettiga värden


        /*
                Step                Obstacle detected
                ---------------------------------------
                Move forward        |false
                Left lane detect    |true
                Left lane detect    |false
                Move forward (cont) |false
                Last move forward   |true

         */
        when(vehicle.getLidar().read()).thenReturn(no_obstacle, leftSide_obstacle, no_obstacle, no_obstacle, front_obstacle);
        when(vehicle.getGyro().getLongitude()).thenReturn( 5, , 10, 100);
        when(vehicle.getFrontRadar().read()).thenReturn(50.0, 10.0);
        when(vehicle.getFrontSideRadar().read()).thenReturn(4.0, 0.0);
        when(vehicle.getBackSideRadar().read()).thenReturn(4.0);

        // The vehicle moves forward once without hindrance
        vehicle.moveForward();
        verify(vehicle.getActuator(), times(1)).driveForward(false, vehicle.getGyro());
        verify(vehicle.getActuator(), never()).driveForward(true, vehicle.getGyro());

        // Vehicle requests to change left by querying the sensors
        vehicle.changeLane();
        verify(vehicle.getActuator(), times(1)).changeLeft(true, vehicle.getGyro());
        verify(vehicle.getActuator(), never()).changeLeft(false, vehicle.getGyro());

        // Vehicle moves to the end of the street and stops when faced with an obstacle
        verify(vehicle.getActuator(), times(19)).driveForward(false, vehicle.getGyro());
        verify(vehicle.getActuator(), times(1)).driveForward(true, vehicle.getGyro());


        verifyNoMoreInteractions(vehicle.getActuator());

        reset(vehicle);



    }
}