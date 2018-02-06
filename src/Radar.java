public class Radar {

    /**
     *  To test leftLaneDetect we need a radar.
     */
    volatile private double reading;

    public Radar() {
        // The initial reading is the maximum range where it can reliably detect an obstacle.
        reading = 50.0;
    }

    /**
     * @return the last sensor data stored.
     */
    public double read(){
        return reading;
    }

    /**
     * @param newReading Sets a new reading externally to the hardware.
     */
    public void write(double newReading){
        this.reading = newReading;
    }
}
