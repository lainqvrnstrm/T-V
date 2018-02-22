
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
    }

    /**
     * Scenario 2
     * Move forward followed by attempt to change lane, which will fail.
     * Followed by move forward commands until the end of the road.
     */
    @Test
    void scenario1_changeLane() {
        //Vehicle vehicle1 = mock(Vehicle.class);

        when(vehicle.moveForward()).thenReturn(true);
        vehicle.moveForward();


        //Vehicle should change lane
        when(vehicle.changeLane()).thenReturn(true);

        //Vehicle changes lane
        vehicle.changeLane();

        assertEquals(vehicle.gyro.getLatitude(), 1);
        

        int runs = 17;
        //Vehicle moves until the end of the street
        for (int i = 5; i < 85; i += 5) { //Increment by 5 since we are moving 5 meters every time.
            when(vehicle.moveForward()).thenReturn(true);
            vehicle.moveForward();
        }

        verify(vehicle, times(runs)).moveForward();

        when(vehicle.moveForward()).thenReturn(false);

    }

    @Test
    void scenario_2() {
        //Vehicle vehicle  = new Vehicle(testGyro, new Radar(), new Radar(), new Radar(), testLidar, testActuator);
        //testFrontRadar.write(10); // Set so no obstacle is in front of the car.
        doReturn(true).when(vehicle).moveForward();
        verify(testActuator).driveForward(false, vehicle.gyro);

        // Attempt to change lane, which should fail due to the values being set.
        //vehicle.backSideRadar.write(4);
        //vehicle.frontSideRadar.write(4);
        //when(vehicle.changeLane()).thenReturn(false);
        //verify(testActuator).changeLeft(true, testGyro);
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