//Class added to satisfy tc0_moveForward(), modified for tc0_whereIs()
public class Gyro {
    int longitude;
    int latitude;

    public Gyro() {
        longitude = 0;
        latitude = 0;
    }

    public int getLatitude() {
        return latitude;
    }

    public int getLongitude() {
        return longitude;
    }
}
