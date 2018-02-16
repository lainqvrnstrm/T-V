
import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.Mockito.*;


@RunWith(JUnit4.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VehicleMockito {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock
    Vehicle vehicle;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(VehicleMockito.class);
        // Initializes a new vehicle object, to be used on all test cases.
        vehicle = mock(Vehicle.class);
    }

    @Test
    void scenario_name_and_purpose1() {
        when(vehicle.moveForward()).thenReturn(true, true, true);
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