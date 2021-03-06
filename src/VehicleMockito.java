import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.runner.RunWith;

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

    @Mock private Radar cloneBackSideRadar = mock(Radar.class);
    @Mock private Radar cloneFrontSideRadar = mock(Radar.class);
    @Mock private Lidar cloneLidar = mock(Lidar.class);


    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(VehicleMockito.class);
        reset(testActuator);
        reset(testBackSideRadar);
        reset(testFrontRadar);
        reset(testFrontSideRadar);
        reset(testGyro);
        reset(testLidar);
    }

    /*
     * Scenario 1
     * Move forward followed by attempt to change lane, which will fail.
     * Followed by move forward commands until the end of the road.
     */
    @Test
    void scenario1_changeLane() {

        int[] lidarReading = new int[360];
        lidarReading[45] = 25;

        //Assign values to the mocked objects.
        when(testGyro.getLongitude()).thenReturn(4,9,85);
        when(testLidar.read()).thenReturn(lidarReading);
        when(testFrontRadar.read()).thenReturn(50.0, 40.0, 4.0);
        when(testBackSideRadar.read()).thenReturn(20.00);
        when(testFrontSideRadar.read()).thenReturn(20.00);

        //Instantiate the vehicle object.
        Vehicle vehicle = new Vehicle(testGyro,testBackSideRadar,testFrontSideRadar,testFrontRadar,testLidar,testActuator);

        vehicle.moveForward(); //move forward
        verify(testActuator).driveForward(false, testGyro,5 );

        //Vehicle changes lane and move forward.
        vehicle.changeLane();
        //Verify the lane has been changed.
        verify(testActuator).changeLeft(false, testGyro);

        //move forward until end
        vehicle.moveForward();
        verify(testActuator, times(2)).driveForward(false, testGyro,5);
        //Vehicle should not go further.
        reset(testActuator);

        vehicle.moveForward();
        verify(testActuator,never()).driveForward(false, testGyro,5);


    }

    /*
    Moves forward
    Tries to change lane -> will detect object in both query reading.
    move toward the end of the road until you run in the wall.
     */
    @Test
    void scenario2_obstacleDetectedTwice() {
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

        when(testLidar.read()).thenReturn(no_obstacle); // sets no obstacle on the 2nd iteration of the lidar reading.
        when(cloneLidar.read()).thenReturn(obstacle);
        when(testGyro.getLongitude()).thenReturn( 5,   10, 100); // Ignore 11-99 as they are tested elsewhere.
        when(testFrontRadar.read()).thenReturn(10.0, 10.0, 10.0, 0.0); // Set front radar reading to 10.
        when(testFrontSideRadar.read()).thenReturn(4.0); // side to 4 then 0.
        when(cloneFrontSideRadar.read()).thenReturn(0.0); // side to 4 then 0.
        when(testBackSideRadar.read()).thenReturn(4.0); // Obstacle detected when read.

        vehicle.moveForward(); // call the controller method to move forward.
        verify(testActuator).driveForward(anyBoolean(), anyObject(), anyInt());

        // Vehicle requests to change left by querying the sensors
        cloneFrontSideRadar.write(2.0);
        cloneLidar.writeIndex(45,2);
        Vehicle clone = new Vehicle(cloneLidar, cloneFrontSideRadar, cloneBackSideRadar);

        vehicle.changeLane(clone); // call the controller method to change lane.
        verify(testActuator, never()).changeLeft(false, testGyro);

        reset(testActuator); // Reset to check that it won't be called at the end.
        vehicle.moveForward(); // The car won't be able to move.
        verify(testActuator, never()).driveForward(false, testGyro,5);
        verify(testGyro, times(3)).getLongitude();
    }

    /*
        Method for testing Scenario 3.
        Sensors are broken and car cannot change lane, thus going forward until the end.
     */
    @Test
    void scenario_3_failedSensors() {

        // Lidar faulty readings
        int[] errorReadings = new int[360];
        for(int i = 0; i<errorReadings.length; i++)
            errorReadings[i] = -1;

        //testing more extensively.
        when(testGyro.getLongitude()).thenReturn(4,  9, 14, 19, 24, 29, 34, 39, 44, 49, 54, 59, 64, 69, 74, 79, 84, 89, 99);
        when(testLidar.read()).thenReturn(errorReadings);
        when(testFrontRadar.read()).thenReturn(50.0);
        when(testBackSideRadar.read()).thenReturn(-1.0);

        //Initialize the object for mockInjection
        Vehicle vehicle = new Vehicle(testGyro,testBackSideRadar,testFrontSideRadar,testFrontRadar,testLidar,testActuator);


        //Move forward and make sure that the subMethods are used.
        vehicle.moveForward();
        verify(testActuator).driveForward(false, testGyro,5);

        //Tries to change lane, error is catched.
        try{
            vehicle.changeLane();
            fail("Something is not working");

        }catch(Error error){
            //OK
        }
        //Make testActuator, never()).changeLeft(anyBoolean(), anyObject());

        for (int i = 5; i<95; i +=5) // Something here is fishy
            vehicle.moveForward();

        verify(testActuator, times(18)).driveForward(false, testGyro,5);

        //Vehicle has met an obstruction and should not be able to move.
        reset(testActuator);
        vehicle.moveForward();
        verify(testActuator, never()).driveForward(false, testGyro,5);

    }
     /*
    Method for testing Scenario 4
    Car tries to change lane, the first query detects an obstacle and the second query broke.
    So the car moves forward till the end of the street
    */
    @Test
    void scenario_4_failedSensor() {

        // Lidar faulty readings
        int[] errorReadings = new int[360];
        for(int i = 0; i<errorReadings.length; i++)
            errorReadings[i] = -1;

        Vehicle vehicle  = new Vehicle(testGyro, testBackSideRadar, testFrontSideRadar, testFrontRadar, testLidar, testActuator);

        int[] leftSide_obstacle = new int[360];
        int[] no_obstacle = new int[360];
        leftSide_obstacle[45] = 4;

        //the first query should detect something, the second query is returning invalid readings indicating broken sensor

        when(testLidar.read()).thenReturn(leftSide_obstacle);
        when(cloneLidar.read()).thenReturn(errorReadings);
        when(testGyro.getLongitude()).thenReturn( 4, 4, 9, 14, 19, 24, 29, 34, 39,
                44, 49, 54, 59, 64, 69, 74, 79, 84, 89, 99);
        when(testFrontSideRadar.read()).thenReturn(4.5);
        when(cloneFrontSideRadar.read()).thenReturn(-1.0); // side to 4 then 0.
        when(testFrontRadar.read()).thenReturn(50.0);
        when(testBackSideRadar.read()).thenReturn(4.0);
        when(cloneBackSideRadar.read()).thenReturn(6.0);


        vehicle.moveForward();
        verify(testActuator).driveForward(false, testGyro,5);

        //changeLeft return false, the first query tells it there is a car on the left

        // Vehicle requests to change left by querying the sensors
        cloneFrontSideRadar.write(-1.0);
        for(int i = 0; i < errorReadings.length; i++)
            cloneLidar.writeIndex(i,-1);
        Vehicle clone = new Vehicle(cloneLidar, cloneFrontSideRadar, cloneBackSideRadar);

        //The changeLane should throw an error, because the second query indicates broken sensor

        try{
            vehicle.changeLane(clone);
            fail("broken sensor");
        }catch(Error error){
            System.out.println("Error catched");
        }

        //check the car is not changing left
        verify(testActuator, never()).changeLeft(anyBoolean(), anyObject());

        //let the car moves 19 time till it reaches the end of the road
        for (int i = 5; i<100; i = i + 5)
            vehicle.moveForward();

        verify(testActuator, times(19)).driveForward(false, testGyro,5);

        reset(testActuator);
        vehicle.moveForward();
        verify(testActuator, never()).driveForward(false, testGyro,5);

    }

    /**
     * Scenario 5.
     * An obstacle is NOT detected in the first query but is in the second, when trying to change to the left lane.
     * This should result in the vehicle NOT changing lane.
     */


    @Test
    void scenario5_obstacleDetectedOnce() {

        // Lidar readings
        int[] leftSide_obstacle = new int[360];
        int[] no_obstacle = new int[360];
        leftSide_obstacle[45] = 2;

        /*
                Step                Obstacle detected
                ---------------------------------------
                Move forward        |false

                Left lane detect    |false
                Left lane detect    |true

                Move forward (cont) |false
                Last move forward   |true

         */

        when(testLidar.read()).thenReturn(no_obstacle);  // 1st query - nothing detected.
        when(cloneLidar.read()).thenReturn(leftSide_obstacle);  // 2nd - detected.
        when(testGyro.getLongitude()).thenReturn( 4,9, 14, 19, 24, 29, 34, 39, 44, 49, 54, 59, 64, 69, 74, 79, 84, 89, 99);
        when(testFrontRadar.read()).thenReturn(50.0,50.0, 50.0, 50.0, 50.0, 50.0, 50.0, 50.0, 50.0, 50.0, // 10 first calls
                46.0, 41.0, 36.0, 31.0, 26.0, 21.0, 16.0, 11.0, 6.0 , 1.0); // 10 last calls
        when(testFrontSideRadar.read()).thenReturn(50.0);  // 1st query - nothing detected.
        when(cloneFrontSideRadar.read()).thenReturn(2.0);   // 2nd - detected.
        when(testBackSideRadar.read()).thenReturn(50.0);  // 1st query - nothing detected.
        when(cloneBackSideRadar.read()).thenReturn(50.0); //2nd - nothing detected by the back side radar.


        // The vehicle starts at the beginning of the street. The sensors are set to their default value, which means they are initially not detecting anything.
        Vehicle vehicle  = new Vehicle(testGyro, testBackSideRadar, testFrontSideRadar, testFrontRadar, testLidar, testActuator);

        // The vehicle moves forward once without hindrance
        vehicle.moveForward();
        // Verify
        verify(testActuator, times(1)).driveForward(false, testGyro, 5);
        verify(testActuator, never()).driveForward(true, testGyro, 5);
        reset(testActuator);

        // Vehicle requests to change left by querying the sensors
        cloneFrontSideRadar.write(2.0);
        cloneBackSideRadar.write(50.0);
        cloneLidar.writeIndex(45,2);

        Vehicle clone = new Vehicle(cloneLidar, cloneFrontSideRadar, cloneBackSideRadar);

        vehicle.changeLane(clone);

        // Verify
        verify(testActuator, times(1)).changeLeft(true, testGyro);
        verify(testActuator, never()).changeLeft(false, testGyro);
        verify(testActuator, times(1)).driveForward(false, testGyro, 5);
        reset(testActuator);


        // Vehicle moves to the end of the street and stops when faced with the obstacle at the end of the street./
        while (vehicle.moveForward());
        // Verify
        verify(testActuator, times(16)).driveForward(false, testGyro, 5);
        verify(testActuator, times(1)).driveForward(true, testGyro, 5);

        verifyNoMoreInteractions(testActuator);

    }

    /*
    Sets the car to have a very high speed, Making it only able to move one between indicating not enough room to move
    again.
     */
    @Test
    void scenario6_veryFastVehicle() {
        //Assign values to the mocked objects.
        when(testFrontRadar.read()).thenReturn(50.0, 50.0, 45.0);
        when(testGyro.getLongitude()).thenReturn(4, 55); //2nd - nothing detected by the back side radar.

        // The vehicle starts at the beginning of the street. The sensors are set to their default value, which means they are initially not detecting anything.
        Vehicle vehicle  = new Vehicle(testGyro, testBackSideRadar, testFrontSideRadar, testFrontRadar, testLidar, testActuator);
        vehicle.setSpeed(49);

        // Can move.
        vehicle.moveForward();
        verify(testActuator, times(1)).driveForward(false, testGyro, vehicle.getSpeed());

        // This should not be able to move.
        vehicle.moveForward();
        verify(testActuator, times(1)).driveForward(true, testGyro, vehicle.getSpeed());


    }
    
}