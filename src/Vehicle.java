public class Vehicle {
    public Gyro gyro;

    public Vehicle() {
        gyro = new Gyro();
    }

    /**
     * Moves the car 5 meters forward in the longitude position.
     * @return If the car's position has changed or not.
     */
    public boolean moveForward() {
        // Increments the longitude of the gyro to simulate moving forward.
        this.gyro.longitude += 5;
        // Returns true because incrementing the longitude moves the car. Returning true indicates a change of longitude.
        return true;
    }

    public boolean leftLaneDetect(Radar[] radars, Lidar lidar) {
        throw new Error("Not Implemented.");
    }

    public boolean changeLane(Radar[] radars, Lidar lidar) {
        throw new Error("Not Implemented.");
    }

    public Gyro whereIs() {
        throw new Error("Not Implemented.");
    }
}
