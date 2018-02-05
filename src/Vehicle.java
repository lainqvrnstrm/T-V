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
    public boolean leftLaneDetect() {
        boolean result = false;
        return result;
    }

    public boolean changeLane(Radar[] radars, Lidar lidar) {
        throw new Error("Not Implemented.");
    }

    public Gyro whereIs() {
        //Returns the vehicle Gyro object and its related values.
        return this.gyro;
    }
}
