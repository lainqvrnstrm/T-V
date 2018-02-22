
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
    @Mock private Radar testRadar = mock(Radar.class);
    @Mock private Lidar testLidar = mock(Lidar.class);

    @InjectMocks private Vehicle vehicle;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(VehicleMockito.class);
        vehicle = mock(Vehicle.class);
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
        vehicle.moveForward();


        //Vehicle changes lane
        when(vehicle.changeLane()).thenReturn(true);


        when(vehicle.whereIs()).thenReturn(testGyro);


        int runs = 17;
        //Vehicle moves until the end of the street
        for (int i = 5; i < 85; i += 5) { //Increment by 5 since we are moving 5 meters every time.
            when(vehicle.moveForward()).thenReturn(true);
            vehicle.moveForward();
        }
        verify(vehicle, times(runs)).moveForward();

        when(vehicle.moveForward()).thenReturn(false);


    }

    void scenario_2() {
        Vehicle vehicle = mock(Vehicle.class);

        when(vehicle.moveForward()).thenReturn(true);
        when(vehicle.changeLane()).thenReturn(false);

        assertTrue(vehicle.moveForward(), "The Vehicle is suppose to move.");
        assertFalse(vehicle.changeLane(), "The car is not suppose to change lane.");

        for (int i = 0; i < 18; i++){
            when(vehicle.moveForward()).thenReturn(true);
            assertTrue(vehicle.moveForward(), "The Vehicle is suppose to move.");
        }

        verify(vehicle, times(19)).moveForward();

        when(vehicle.moveForward()).thenReturn(false);

        assertFalse(vehicle.moveForward(),
                "The Vehicle is suppose to not move anymore.");
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

    /*
     *Testing scenario 5.
     */
    @Test
    void scenario_name_and_purpose5(){

    }

}