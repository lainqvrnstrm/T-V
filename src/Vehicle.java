public class Vehicle {
    public Gyro gyro;
    Radar[] radars;
    Lidar lidar;

    public boolean moveForward(Gyro position) {
        throw new Error("Not Implemented.");
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
