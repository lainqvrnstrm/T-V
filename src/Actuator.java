/* Class added for tc0_driveForward.
 *
 */
public class Actuator {

    /*
     * Method tested in tc0_driveForward and tc1_driveForward.
     */
    void driveForward(boolean frontDetected, Gyro gyro, int speed) { // Speed added for the interface.
        if(!frontDetected)   // Added for tc0_driveForward, after implementation of solution for tc1_driveForward had it failing.
            gyro.setLongitude(gyro.getLongitude() + speed); // Added for tc1_driveForward. Modified for scenario 2 to used setter.
    }
    /**
     * Method tested in tc0_changeLeft and tc1_changeLeft.
     */
    void changeLeft(boolean leftLaneDetected, Gyro gyro){
        if(!leftLaneDetected){   // Added for tc0_changeLeft, after implementation of solution for tc1_changeLeft had it failing.
            gyro.setLatitude(gyro.getLatitude()+1); // Added for tc1_changeLeft. Modified for scenario 2 to used setter.
        }
    }
}

