
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
        // Initializes a new vehicle object, to be used on all test cases.
        vehicle = mock(Vehicle.class);
    }

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
        for(int i = 5; i<85; i+=5 ) { //Increment by 5 since we are moving 5 meters every time.
            when(vehicle.moveForward()).thenReturn(true);
            assertEquals(vehicle.moveForward(), true, "Vehicle moves forward");
        }
        verify(vehicle, times(runs +1)).moveForward();

        when(vehicle.moveForward()).thenReturn(false);
        assertEquals(vehicle.moveForward(), false, "Vehicle should not move further");

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