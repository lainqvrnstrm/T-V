
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

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(VehicleMockito.class);
        vehicle = mock(Vehicle.class);
        vehicle.actuator = mock(Actuator.class);
    }

    /*
     * Scenario 1
     * Move forward followed by attempt to change lane, which will fail.
     * Followed by move forward commands until the end of the road.
     */
    @Test
    void scenario1_changeLane() {
        //Instantiate the vehicle object.
        Vehicle vehicle = new Vehicle();

        //Start by moving forward.
        vehicle.moveForward();

        //Vehicle changes lane and move forward.
        vehicle.changeLane();

        //Vehicle should have changed lane.
        verify(testGyro, times(1)).getLatitude();
        
        //Expected number of runs.
        int runs = 17;

        //Vehicle moves until the end of the street
        for (int i = 5; i < 85; i += 5) { //Increment by 5 since we are moving 5 meters every time.
            when(vehicle.moveForward()).thenReturn(true);
            vehicle.moveForward();
        }

        //Verify that the method has been envoked the expected number of times.
        verify(vehicle, times(runs)).moveForward();

        //The vehicle will then move forward.
        when(vehicle.moveForward()).thenReturn(false);

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

        when(vehicle.lidar.read()).thenReturn(no_obstacle, obstacle, no_obstacle); // stets no obstacle on the 2nd iteration of the lidar reading.
        when(vehicle.gyro.getLongitude()).thenReturn(5, 5, 10, 10, 100); // Jumps in distance between 10 and 100 since nothing intresting happens inbetween.
        when(vehicle.frontRadar.read()).thenReturn(10.0); // Set front radar reading to 10.
        when(vehicle.frontSideRadar.read()).thenReturn(4.0, 0.0); // side to 4 then 0.
        when(vehicle.backSideRadar.read()).thenReturn(4.0); // Obstacle detected when read.

        vehicle.moveForward(); // call the controller method to move forward.
        verify(vehicle.actuator).driveForward(false, vehicle.gyro);

        vehicle.changeLane(); // call the controller method to change lane.
        verify(vehicle.actuator, never()).changeLeft(false, vehicle.gyro);

        reset(vehicle.actuator); // Reset to check that it won't be called at the end.
        vehicle.moveForward(); // The car won't be able to move.
        verify(vehicle.actuator, never()).driveForward(anyBoolean(), anyObject());
        verify(vehicle.gyro, times(5)).getLongitude();


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
        for(int i = 5; i<95; i+=5 ) { //Increment by 5 since
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
        for(int i = vehicle.gyro.longitude; i<95; i+=5 ) { //Increment by 5 since
            when(vehicle.moveForward()).thenReturn(true);
            vehicle.moveForward();
        }
        verify(vehicle, times(19)).moveForward();

        //Vehicle has met an obstruction
        when(vehicle.moveForward()).thenReturn(false);

    }
}