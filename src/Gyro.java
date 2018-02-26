//Class added to satisfy tc0_moveForward(), modified for tc0_whereIs()
public class Gyro {
    private int longitude = 4;
    private int latitude;

    public int getLatitude() {
        return latitude;
    }

    public int getLongitude() {
        return longitude;
    }

    // Added for scenario 2.
    public void setLongitude(int longitude) {
        this.longitude = longitude;
    }

    // Added for scenario 2.
    public void setLatitude(int latitude) {
        this.latitude = latitude;
    }
}
