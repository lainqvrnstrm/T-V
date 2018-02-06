public class Vehicle {
    public Gyro gyro;
    public Radar backSideRadar;
    public Radar frontSideRadar;
    public Radar frontRadar;
    private Lidar lidar;

    public Vehicle() {
        gyro = new Gyro();
        backSideRadar = new Radar();
        frontSideRadar = new Radar();
        frontRadar = new Radar();
    }

    /**
     * Moves the car 5 meters forward in the longitude position.
     * @return If the car's position has changed or not.
     */
    public boolean moveForward() {
        double reading = frontRadar.read();

        // If the queries values are different the sensor is not working properly.
        // And we should not move with not working sensors.
        // A half meter of different values is indicated as faulty readings.
        double faultrange = 0.5;
        if (reading > reading +faultrange || reading < reading -faultrange) {
            return false;
        }

        // If the sensor readings are less than the move distance, we do not move.
        double move_distance = 5.0;
        if (reading <= move_distance) {
            return false;
        }

        // If the gyro is less than the road distance and the move distance so we can't go beyond the road.
        double road_distance = 100;
        if(gyro.longitude <= road_distance-move_distance){

            // tc0: Increments the longitude of the gyro to simulate moving forward.
            this.gyro.longitude += (int) move_distance;

            // tc0: Returns true because incrementing the longitude moves the car. Returning true indicates a change of longitude.
            return true;
        }

        // Return false because no happy branch was successful.
        return false;
    }

    /*
    signature updated to take arguments.
     */
    public boolean leftLaneDetect(Vehicle... secondQuery) throws Error {
        double[] readings = {backSideRadar.read(), frontSideRadar.read()};

        double[] readingsSecondQuery = {0,0};
        if (secondQuery.length != 0) {
            readingsSecondQuery[0] = secondQuery[0].backSideRadar.read();
            readingsSecondQuery[1] = secondQuery[0].frontSideRadar.read();
        } else {
            readingsSecondQuery = readings;
        }

        double faultrange = 0.5;
        // If the queries values are different the sensor is not working properly.
        // And we should not move with not working sensors.
        // A half meter of different values is indicated as faulty readings.
        /*
        if(readings[0] > readings[1] +faultrange || readings[0] < readings[1] -faultrange){
            if(secondQuery[0] > readingsSecondQuery +faultrange || secondQuery[0] < secondQuery[1] -faultrange) {
                throw new Error("do not move");
            }
        }

        //If the queries values are the same.
        //And the 4 meters is the max safe range.
        if(readings[0] < readings[1] +faultrange || readings[0] > readings[1] -faultrange) {
            if (secondQuery[0] < secondQuery[1] +faultrange || secondQuery[0] > secondQuery[1] -faultrange){
                if (readings[0] <= 4 || readings[1] <= 4) {
                    return true;
                } else {
                    return false;
                }
        }
        }
        */


        return true;
    }

    /**
     *
     * @return whether or not the car has changed lane.
     */
    public boolean changeLane(Vehicle... secondQuery) {

        // tc0: Catches invalid leftLaneDetradar.getValue1()ect errors.
        try {

            // tc1: If we can change lane.
            if (!leftLaneDetect(secondQuery) && gyro.longitude <= 95) {

                // tc1: Changes the lane to the left.
                gyro.latitude += 1;

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
