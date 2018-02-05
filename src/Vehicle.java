public class Vehicle {
    public Gyro gyro;
    Radar[] radars = {new Radar(), new Radar(), new Radar()};
    Lidar lidar;


    public Vehicle() {
        gyro = new Gyro();
    }

    /**
     * Moves the car 5 meters forward in the longitude position.
     * @return If the car's position has changed or not.
     */
    public boolean moveForward() {
        // Forward facing radar.
        Radar radar = radars[2];
        double[] readings = radar.getValues();

        // If the queries values are different the sensor is not working properly.
        // And we should not move with not working sensors.
        // A half meter of different values is indicated as faulty readings.
        double faultrange = 0.5;
        if (readings[0] > readings[1] +faultrange || readings[0] < readings[1] -faultrange) {
            return false;
        }

        // If the sensor readings are less than the move distance, we do not move.
        double move_distance = 5.0;
        if (radar.getValue1() <= move_distance) {
            return false;
        }

        // If the gyro is less than the road distance and the move distance so we can't go beyond the road.
        double road_distance = 100;
        if(gyro.longitude <= road_distance-move_distance ){

            // Increments the longitude of the gyro to simulate moving forward.
            this.gyro.longitude += (int) move_distance;

            // Returns true because incrementing the longitude moves the car. Returning true indicates a change of longitude.
            return true;
        }

        // Return false because no happy branch was successful.
        return false;
    }

    /*
    signature updated to take arguments.
     */
    public boolean leftLaneDetect() throws Error {
        Radar radar0 = radars[0];
        Radar radar1 = radars[1];
        double[] readings = radar1.getValues();
        double[] readings0 = radar0.getValues();
        double faultrange = 0.5;
        // If the queries values are different the sensor is not working properly.
        // And we should not move with not working sensors.
        // A half meter of different values is indicated as faulty readings.
        if(readings[0] > readings[1] +faultrange || readings[0] < readings[1] -faultrange){
            if(readings0[0] > readings0[1] +faultrange || readings0[0] < readings0[1] -faultrange) {
                throw new java.lang.Error("do not move");
            }
        }

        //If the queries values are the same.
        //And the 4 meters is the max safe range.
        if(readings[0] < readings[1] +faultrange || readings[0] > readings[1] -faultrange) {
            if (readings0[0] < readings0[1] +faultrange || readings0[0] > readings0[1] -faultrange){
                if (readings[0] <= 4 || readings[1] <= 4) {
                    return true;
                } else {
                    return false;
                }
        }
        }


        return true;
    }

    /**
     *
     * @return whether or not the car has changed lane.
     */
    public boolean changeLane() {

        // Catches invalid leftLaneDetect errors.
        try {

            // If we can change lane.
            if (!leftLaneDetect() && gyro.longitude <= 95) {

                // Changes the lane to the left.
                gyro.latitude += 1;

                // Return a successful change lane.
                return true;
            }

            // Less than two working sensors will receive an error.
        } catch (Error error) {
            // Is alright, don't worry about it.
        } finally {

            // Moves the car forward if possible after changing the lane.
            // This is not dependent on whether we changed lane or not.
            moveForward();
        }

        // Catches all sad paths which do not return true.
        return false;
    }

    public Gyro whereIs() {
        //Returns the vehicle Gyro object and its related values.
        //Added for tc0 but used by all related test cases.
        return this.gyro;
    }
}
