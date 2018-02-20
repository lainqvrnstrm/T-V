
import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.Mockito.*;


@RunWith(JUnit4.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VehicleMockito {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

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

    @Test
    void scenario_name_and_purpose3() {

    }

    @Test
    void scenario_name_and_purpose4() {

    }

}