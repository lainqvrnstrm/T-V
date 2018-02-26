public class Lidar {

    /**
     *  To test leftLaneDetect we need a lidar.
     */

    // For every degree surrounding the lidar there is a value representing the distance between the lidar and a detected object.
    // When the degree is 0 we are getting a reading from the absolute front of the car.
    // Class added for use of the lidar sensor, this to satisfy requirements for tc0_leftLaneDetect as well as following ones.
    private int[] reading = new int[360];

    Lidar(){
        for (int i = 0; i < reading.length; i++){
            reading[i] = 50;
        }
    }

    /**
     * @return the last sensor data stored.
     */
    public int[] read(){
        return reading;
    }

    void write(int[] newReading){
        try{
            if(newReading.length == 360){
                reading = newReading;
            }
            else{
                throw new Exception("The write method requires a an array of length 360");
            }
        }
        catch(Exception e){

        }
    }

    /**
     * Sets a value at the given index. Added for tc0_leftLaneDetect
     * @param index
     * @param value
     */
    public void writeIndex(int index, int value) {
        reading[index] = value;
    }




}
