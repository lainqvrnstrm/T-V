import java.lang.reflect.Array;
import java.util.Arrays;

public class Vehicle { //tc0_moveForward()
    private Gyro gyro;
    private Radar backSideRadar;
    private Radar frontSideRadar;
    private Radar frontRadar;
    private Lidar lidar;
    private int speed = 5;
    private Actuator actuator; // Added for tc0_driveForward.

    public Vehicle() { //tc0_moveForward()
        gyro = new Gyro();
        backSideRadar = new Radar();
        frontSideRadar = new Radar();
        frontRadar = new Radar();
        lidar = new Lidar();
        actuator = new Actuator(); // Added for tc0_driveForward.
    }

    public Vehicle(Gyro gyro, Radar backSideRadar, Radar frontSideRadar, Radar frontRadar, Lidar lidar, Actuator actuator) { //tc0_moveForward()
        this.gyro = gyro;
        this.backSideRadar = backSideRadar;
        this.frontSideRadar = frontSideRadar;
        this.frontRadar = frontRadar;
        this.lidar = lidar;
        this.actuator = actuator; // Added for tc0_driveForward.
    }

    public int getSpeed() {
        return speed;
    }

    public Gyro getGyro() {
        return gyro;
    }

    public Lidar getLidar() {
        return lidar;
    }

    public Radar getFrontRadar() {
        return frontRadar;
    }

    public Radar getFrontSideRadar() {
        return frontSideRadar;
    }

    public Radar getBackSideRadar() {
        return backSideRadar;
    }

    public Actuator getActuator() {    // Added to test methods changeLeft and driveForward.
        return actuator;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void setActuator(Actuator actuator) {
        this.actuator = actuator;
    }

    public void setBackSideRadar(Radar backSideRadar) {
        this.backSideRadar = backSideRadar;
    }

    public void setFrontSideRadar(Radar frontSideRadar) {
        this.frontSideRadar = frontSideRadar;
    }

    public void setLidar(Lidar lidar) {
        this.lidar = lidar;
    }

    /**
     * Moves the car 5 meters forward in the longitude position.
     * @return If the car's position has changed or not.
     */
    public boolean moveForward() {
        double reading = frontRadar.read(); // Added for tc2.

        // If the queries values are different the sensor is not working properly.
        // And we should not move with not working sensors.
        // A half meter of different values is indicated as faulty readings.
        double faultrange = 0.5;
        if (reading > reading +faultrange || reading < reading -faultrange) { //Added for tc2.
            return false;
        }

        // If the sensor readings are less than the move distance, we do not move.
        double move_distance = speed;
        if (reading <= move_distance) { //Added for tc2.
            return false;
        }

        // If the gyro is less than the road distance and the move distance so we can't go beyond the road.
        double road_distance = 100;


        // Variable added for mockito to use continuous stubbing easier.
        int longitude = gyro.getLongitude();

        // tc0: Increments the longitude of the gyro to simulate moving forward.
        actuator.driveForward(!(longitude <= road_distance-move_distance), gyro, speed);
        //this.gyro.longitude += (int) move_distance; //Added for tc0.

        if(longitude <= road_distance-move_distance){ //Added for tc0.

            // tc0: Returns true because incrementing the longitude moves the car. Returning true indicates a change of longitude.
            return true;
        }

        // Return false because no happy branch was successful.
        return false; //Added for tc0.
    }

    /**
     * @param secondQuery Given no parameters the car will mimic it’s own cars readings twice.
     *                    Give a secondary vehicle it will be used as the second query to the vehicle.
     *                    If they different, it will be indicated as a not working sensor and throw an error.
     * @return true for obstacle to the left, false for no obstacle. Error if incorrect readings provided.
     * @throws Error when less than two sensors are working.
     */
    //signature updated to take arguments.
    public boolean leftLaneDetect(Vehicle... secondQuery) throws Error {
        //tc0: variables added to create leftLaneDetect.
        double[] readings = {backSideRadar.read(), frontSideRadar.read()};
        int[] lidars = lidar.read();

        double[] readingsSecondQuery = Arrays.copyOf(readings, 2);
        int[] lidarsSecondQuery = lidar.read();
        if (secondQuery.length != 0) {
            System.out.println("heeeey");
            readingsSecondQuery[0] = secondQuery[0].backSideRadar.read();
            readingsSecondQuery[1] = secondQuery[0].frontSideRadar.read();
            lidarsSecondQuery = secondQuery[0].lidar.read();
        }

        // tc0: Throw error with less than two working sensors.
        // Keeps track of which sensor works.

        {
            boolean[] workingSensors = new boolean[3];
            workingSensors[0] = (readings[0] == readingsSecondQuery[0]);
            workingSensors[1] = (readings[1] == readingsSecondQuery[1]);
            workingSensors[2] = Arrays.equals(lidars, lidarsSecondQuery);

            int workingSensors_count = 0;
            for (boolean item: workingSensors)
                workingSensors_count += item?1:0;
            // tc0: throws errors for invalid number of functioning sensors.
            if (workingSensors_count < 2)
                throw new Error("Not enough working sensors.");

        }

        //Queries needed for tc1_leftlaneDetect to determine if 2 or more sensors are working.
        double obstacle_distance = speed;
        int angle = 45;
        // Query 1.
        if (readings[0] < obstacle_distance ||
                readings[1] < obstacle_distance ||
                lidars[angle] < obstacle_distance)
            return true;


        // Query 2.
        if (readingsSecondQuery[0] < obstacle_distance ||
                readingsSecondQuery[1] < obstacle_distance ||
                lidarsSecondQuery[angle] < obstacle_distance)
            return true;


        // tc1: No obstacle detected.
        return false;
    }

    /**
     * @param secondQuery Are passed to LeftLaneDetect and serves the same purpose
     * @return whether or not the car has changed lane.
     */
    public boolean changeLane(Vehicle... secondQuery) {

        // tc0: Catches invalid leftLaneDetect.getValue1()ect errors.
        try {
            boolean leftLaneDetect = leftLaneDetect(secondQuery);

            // tc1: Changes the lane to the left.
            actuator.changeLeft(leftLaneDetect && gyro.getLongitude() <= 95, gyro);
            System.out.println("WT!!!!!!!!!!!");

            // tc1: If we can change lane.
            if (!leftLaneDetect && gyro.getLongitude() <= 95) {

                // tc1: Return a successful change lane.
                return true;
            }

            // tc0: Less than two working sensors will receive an error.
        } catch (Error error) {
            // tc0: No action is intended.
        } finally {

            // tc3: Moves the car forward if possible after changing the lane.
            // tc3: This is not dependent on whether we changed lane or not.
            moveForward();
        }

        // tc2: Catches all sad paths, i.e: tc0, tc2, tc3, tc4.
        return false;
    }

    public Gyro whereIs() {
        //Returns the vehicle Gyro object and its related values.
        //Added for tc0 but used by all related test cases.
        return this.gyro;
    }
}
