public class Vehicle {
    public Gyro gyro;

    public Vehicle() {
        gyro = new Gyro();
    }

    public boolean moveForward() {
        this.gyro.longitude += 5;
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
