public class Vehicle {
    public Gyro gyro;
    Radar[] radars;
    Lidar lidar;


    public Vehicle() {
        gyro = new Gyro();
    }

    /**
     * Moves the car 5 meters forward in the longitude position.
     * @return If the car's position has changed or not.
     */
    public boolean moveForward() {
        Radar radar = new Radar();
        // Increments the longitude of the gyro to simulate moving forward.
        this.gyro.longitude += 5;
        if(radar.getValue1()==radar.getValue2()&&radar.getValue1() <= 5.0){
            return false;
        }else {
            // Returns true because incrementing the longitude moves the car. Returning true indicates a change of longitude.
            return true;
        }
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
        throw new Error("Not Implemented.");
    }
}
