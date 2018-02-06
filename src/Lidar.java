public class Lidar {

    /**
     *  To test leftLaneDetect we need a lidar.
     */

    // For every degree surrounding the lidar there is a value representing the distance between the lidar and a detected object.
    // When the degree is 0 we are getting a reading from the absolute front of the car.
    int[] reading = new int[360];

    /**
     * @return the last sensor data stored.
     */
    int[] read(){
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
     * Sets a value at the given index.
     * @param index
     * @param value
     */
    public void writeIndex(int index, int value) {
        reading[index] = value;
    }




}
